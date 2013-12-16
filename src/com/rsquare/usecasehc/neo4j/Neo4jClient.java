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
	
	public List<ProviderReferralResult> getReferralsByProvider(String npi, int option, String limit) throws SQLException
	{
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		List<ProviderReferralResult> results = new ArrayList<ProviderReferralResult>();
		try
		{
			c = getConnection();
			s = c.createStatement();
			String sql = makeCypherQuery(npi, option, limit);
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
                String doctor_pid = String.valueOf(doctor.get("npi"));
                String refdoctor_pid = String.valueOf(referredDoctor.get("npi"));
				results.add(new ProviderReferralResult(doctor_pid, refdoctor_pid, count, direction, reverse_count));
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
	
	private String makeReferralCypherQuery(String npi, String limit)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)-[r:REFERRAL]->(b:provider)  WHERE a.npi = ");
		builder.append(npi);
		builder.append(" RETURN a as provider,b as referral,r.count as count,\"0\" as reverse_count,\"referred\" as direction order by r.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeReferredByCypherQuery(String npi, String limit)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)<-[r:REFERRAL]-(b:provider)  WHERE a.npi = ");
		builder.append(npi);
		builder.append(" RETURN a as provider,b as referral,\"0\" as count,r.count as reverse_count,\"was referred by\" as direction order by r.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeAllRelationsCypherQuery(String npi, String limit)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)<-[r1:REFERRAL]->(b:provider) WHERE a.npi = ");
		builder.append(npi);
		builder.append(" OPTIONAL MATCH (a:provider)-[r2:REFERRAL]->(b:provider) OPTIONAL MATCH (a:provider)<-[r3:REFERRAL]-(b:provider) RETURN DISTINCT a as provider,b as referral,r2.count as count,r3.count as reverse_count,CASE WHEN TYPE(r3) = 'REFERRAL' AND TYPE(r2) IS NULL THEN \"was referred by\" WHEN TYPE(r2) = 'REFERRAL' AND TYPE(r3) IS NULL THEN \"referred\" ELSE \"bidirectional\" END as direction order by r2.count,r3.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeBidirectionalOnlyCypherQuery(String npi, String limit)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)-[r1:REFERRAL]->(b:provider)-[r2:REFERRAL]->(a:provider)  WHERE a.npi = ");
		builder.append(npi);
		builder.append(" RETURN a as provider,b as referral,r1.count as count,r2.count as reverse_count,\"bidirectional\" as direction order by r1.count,r2.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	
	private String makeCypherQuery(String npi, int option, String limit)
	{
		String retString = null;
		switch(option)
		{
		case 0:
		case 1:
			retString = makeReferralCypherQuery(npi, limit);
			break;
		case 2:
			retString = makeReferredByCypherQuery(npi, limit);
			break;
		case 3:
		case 7:
			retString = makeAllRelationsCypherQuery(npi, limit);
			break;
		case 4:
		case 5:
		case 6:
			retString = makeBidirectionalOnlyCypherQuery(npi, limit);
			break;
		}
		return retString;
	}
	
	private String addLimitCypher(String limit)
	{
		return (" LIMIT " + limit);
	}

}
