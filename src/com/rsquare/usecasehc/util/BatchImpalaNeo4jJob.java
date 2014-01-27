package com.rsquare.usecasehc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rsquare.usecasehc.impala.ImpalaClient;
import com.rsquare.usecasehc.model.ProviderCountData;

public class BatchImpalaNeo4jJob {
	
	private int counter = 0;
	private List<ProviderCountData> threadData = new ArrayList<ProviderCountData>();
	private boolean executionFailed = false;
	private static int THREAD_COUNT = 10;
	private static int CONNECTION_COUNT = 10;
	private ExecutorService executor = null;
	private boolean writeFromScratch = false;

	public static void main(String[] args) {
		boolean appendData = false;
		if(args.length > 0)
		{
			String appendArg = args[0];
			if("true".equalsIgnoreCase(appendArg))
			{
				appendData = true;
			}
		}
		new BatchImpalaNeo4jJob().populateCount(appendData);
	}
	
	private void populateCount(boolean appendData)
	{
		File f = new File("provider_count_data.csv");
		Map<String, ProviderCountData> existingData = new HashMap<String, ProviderCountData>();
		try
		{
			ImpalaClient hc = new ImpalaClient();
			ResultSet rs = hc.getProviderIds();
			Class.forName("org.neo4j.jdbc.Driver");
			Connection [] conn = new Connection[CONNECTION_COUNT];
			for(int i=0;i<CONNECTION_COUNT;i++)
			{
				conn[i] = DriverManager.getConnection("jdbc:neo4j://localhost:7474", "", "");
			}
			if(f.exists() && appendData)
			{
				existingData = readExistingData(f);
				counter = existingData.size();
			}
			executor = Executors.newFixedThreadPool(THREAD_COUNT);
			int connIndex = 0;
			while(rs.next() && !executionFailed)
			{
				String npi = rs.getString("npi");
				if(!existingData.containsKey(npi))
				{
					executor.execute(new JobExecutor(npi, conn[connIndex]));
				}
				if(++connIndex == CONNECTION_COUNT)
				{
					connIndex = 0;
				}
				
			}
			rs.close();
			executor.shutdown();
			boolean executionFinished = false;
//			int fileCounter = 0;
			while(!executionFinished && !executionFailed)
			{
				System.out.println("======= Waiting for 10 minutes to check again =======================");
				executionFinished = executor.awaitTermination(10, TimeUnit.MINUTES);
				System.out.println("======= Checking if tasks finished =======================");
				if(!executionFinished && !executionFinished)
				{
					if(writeFromScratch)
					{
						performWrite(f, existingData, appendData);
					}
					else
					{
						performWrite(f);
					}
//					File temp = new File("provider_count_data_" + fileCounter++);
//					performWrite(temp, existingData, appendData);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			executor.shutdownNow();
		}
		finally {
			if(writeFromScratch)
			{
				performWrite(f, existingData, appendData);
			}
			else
			{
				performWrite(f);
			}
		}
	}
	
	private void performWrite(File f)
	{
		BufferedWriter out = null;
		System.out.println("======== PERFORM INTERMEDIATE FILE WRITE - COUNT ========" + counter);
		try {
			out = new BufferedWriter(new FileWriter(f, true));
			writeThreadData(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void performWrite(File f, Map<String, ProviderCountData> existingData, boolean appendData)
	{
		BufferedWriter out = null;
		System.out.println("======== PERFORM FILE WRITE - COUNT ========" + counter);
		try {
			if(appendData)
			{
				Iterator<ProviderCountData> iterator = existingData.values().iterator();
				out = new BufferedWriter(new FileWriter(f));
				out.write("NPI,RELATIONSHIP_COUNT,REFERRAL_COUNT,REFERRED_COUNT");
				out.newLine();
				while(iterator.hasNext())
				{
					out.write(iterator.next().toCSVString());
					out.newLine();
				}
				writeThreadData(out);
			}
			else
			{
				out = new BufferedWriter(new FileWriter(f));
				out.write("NPI,RELATIONSHIP_COUNT,REFERRAL_COUNT,REFERRED_COUNT");
				out.newLine();
				writeThreadData(out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeThreadData(BufferedWriter out) throws Exception
	{
		synchronized (executor) {
			Iterator<ProviderCountData> iter = threadData.iterator();
			while(iter.hasNext())
			{
				ProviderCountData pcd = iter.next();
				out.write(pcd.toCSVString());
				out.newLine();
			}
			if(!writeFromScratch)
			{
				threadData.clear();
			}
		}
	}
	
	private void addToWriteQueue(ProviderCountData pcd) throws Exception
	{
		synchronized (executor) {
			threadData.add(pcd);
			counter++;
			System.out.println("======== nodes processed: " + counter);
		}
		
	}
	
	private Map<String, ProviderCountData> readExistingData(File f)
	{
		Map<String, ProviderCountData> data = new HashMap<String, ProviderCountData>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			//Ignore the first line
			String line = reader.readLine();
			String delim = "[,]";
			while((line = reader.readLine())!=null)
			{
				String [] tokens = line.split(delim);
				if(tokens!=null && tokens.length==4)
				{
					data.put(tokens[0], new ProviderCountData(tokens[0], tokens[1], tokens[2], tokens[3]));
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return data;
	}
	
	private class JobExecutor implements Runnable
	{
		private Connection c;
		private String npi;
		private int attempt = 1;
		
		public JobExecutor(String npi, Connection c) throws Exception
		{
			this.c = c;
			this.npi = npi;
			attempt = 1;
			System.out.println("Creating thread for npi: " + npi);
		}

		@Override
		public void run() {
			try
			{
				System.out.println("Value of attempt: " + attempt + " for npi: " + npi);
				if(attempt++ <= 3)
				{
					ProviderCountData pcd = getReferralCountByProvider(npi, c.createStatement() );
					addToWriteQueue(pcd);
				}
			}
			catch(SQLException exc)
			{
				//This is sql exception. Log and try one more time with the same npi
				exc.printStackTrace();
				if(attempt > 2)
				{
					System.out.println("Forceful shutdown now!!!");
					executionFailed = true;
					executor.shutdownNow();
				}
				else
				{
					run();
				}
			}
			catch(ConnectException ex)
			{
				ex.printStackTrace();
				executionFailed = true;
				executor.shutdownNow();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				executionFailed = true;
				executor.shutdownNow();
			}
		}
		
		private ProviderCountData getReferralCountByProvider(String npi, Statement s) throws SQLException
		{
			ResultSet rs = null;
			ProviderCountData pcd = new ProviderCountData(npi, "-1", "-1", "-1");
			try
			{
				StringBuilder sql = new StringBuilder("START n=node:lucidxprov(\"npi:");
				sql.append(npi);
				sql.append("\") MATCH (a:provider {npi:");
				sql.append(npi);
				sql.append("})<-[r:REFERRAL]->(b:provider) OPTIONAL MATCH (a:provider {npi:");
				sql.append(npi);
				sql.append("})-[r1:REFERRAL]->(b:provider) OPTIONAL MATCH (a:provider {npi:");
				sql.append(npi);
				sql.append("})-[r2:REFERRED]->(b:provider) RETURN a.npi, count(DISTINCT b.npi) as relation_count, SUM(r1.count) as referral_count, SUM(r2.count) as referred_count");
				rs = s.executeQuery(sql.toString());
				while(rs.next())
				{
					pcd = new ProviderCountData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
				}
				if("-1".equalsIgnoreCase(pcd.getRelationCount()))
				{
					pcd.setReferralCount("0");
					pcd.setReferredCount("0");
					pcd.setRelationCount("0");
				}
			}
	        catch(SQLException sqe)
	        {
	            throw sqe;
	        }
			finally
			{
//				if(rs!=null) rs.close();
//				if(s!=null) s.close();
			}
			return pcd;
		}
		
	}

}
