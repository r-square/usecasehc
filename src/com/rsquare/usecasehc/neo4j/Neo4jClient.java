package com.rsquare.usecasehc.neo4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rsquare.usecasehc.model.ProviderReferralResult;
import com.rsquare.usecasehc.util.Util;

public class Neo4jClient {
	

	private Logger logger = Logger.getLogger(Neo4jClient.class);
	
	public Neo4jClient() {
		String driver = "org.neo4j.jdbc.Driver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.debug(e);
		}

	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				"jdbc:neo4j://localhost:7474", "", "");
	}
	
	public List<ProviderReferralResult> getReferralsByProvider(String pid, int option, String limit) throws SQLException
	{
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		List<ProviderReferralResult> results = new ArrayList<ProviderReferralResult>();
		try
		{
			c = getConnection();
			s = c.createStatement();
			String sql = makeCypherQuery(pid, option, limit);
			logger.info(sql);
			rs = s.executeQuery(sql);
			while(rs.next())
			{
				@SuppressWarnings("unchecked")
				Map<String, String> doctor = (Map<String, String>)rs.getObject(1);
				@SuppressWarnings("unchecked")
				Map<String, String> referredDoctor = (Map<String, String>)rs.getObject(2);
				String count = rs.getString(3);
				String reverse_count = rs.getString(4);
				String direction = rs.getString(5);
				results.add(new ProviderReferralResult(doctor.get("pid"), referredDoctor.get("pid"), count, direction, reverse_count));
			}
		}
		catch(SQLException sqe)
		{
			throw sqe;
		}
		finally
		{
			logger.debug(results);
			if(rs!=null) rs.close();
			if(s!=null) s.close();
			if(c!=null) c.close();
		}
		return results;
	}
	
	private String makeReferralCypherQuery(String pid, String limit)
	{
		StringBuilder builder = new StringBuilder("START a=node(*) match (a)-[r:REFERRAL]->(b)  WHERE a.pid = \"");
		builder.append(pid);
		builder.append("\" RETURN a as provider,b as referral,r.count as count,\"0\" as reverse_count,\"referred\" as direction order by r.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeReferredByCypherQuery(String pid, String limit)
	{
		StringBuilder builder = new StringBuilder("START a=node(*) match (a)<-[r:REFERRAL]-(b)  WHERE a.pid = \"");
		builder.append(pid);
		builder.append("\" RETURN a as provider,b as referral,\"0\" as count,r.count as reverse_count,\"was referred by\" as direction order by r.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeAllRelationsCypherQuery(String pid, String limit)
	{
		StringBuilder builder = new StringBuilder("START a=node(*) match (a)<-[r1:REFERRAL]->(b) WHERE a.pid = \"");
		builder.append(pid);
		builder.append("\" OPTIONAL MATCH (a)-[r2:REFERRAL]->(b) OPTIONAL MATCH (a)<-[r3:REFERRAL]-(b) RETURN DISTINCT a as provider,b as referral,r2.count as count,r3.count as reverse_count,CASE WHEN TYPE(r3) = 'REFERRAL' AND TYPE(r2) IS NULL THEN \"was referred by\" WHEN TYPE(r2) = 'REFERRAL' AND TYPE(r3) IS NULL THEN \"referred\" ELSE \"bidirectional\" END as direction order by r2.count,r3.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeBidirectionalOnlyCypherQuery(String pid, String limit)
	{
		StringBuilder builder = new StringBuilder("START a=node(*) match (a)-[r1:REFERRAL]->(b)-[r2:REFERRAL]->(a)  WHERE a.pid = \"");
		builder.append(pid);
		builder.append("\" RETURN a as provider,b as referral,r1.count as count,r2.count as reverse_count,\"bidirectional\" as direction order by r1.count,r2.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	
	private String makeCypherQuery(String pid, int option, String limit)
	{
		String retString = null;
		switch(option)
		{
		case 0:
		case 1:
			retString = makeReferralCypherQuery(pid, limit);
			break;
		case 2:
			retString = makeReferredByCypherQuery(pid, limit);
			break;
		case 3:
		case 7:
			retString = makeAllRelationsCypherQuery(pid, limit);
			break;
		case 4:
		case 5:
		case 6:
			retString = makeBidirectionalOnlyCypherQuery(pid, limit);
			break;
		}
		return retString;
	}
	
	private String addLimitCypher(String limit)
	{
		return (" LIMIT " + limit);
	}

}
