package com.rsquare.usecasehc.neo4j;

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

import org.apache.log4j.Logger;

import com.rsquare.usecasehc.model.Location;
import com.rsquare.usecasehc.model.LocationType;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderReferralResult;
import com.rsquare.usecasehc.model.ProviderSpecialtyNode;
import com.rsquare.usecasehc.util.PredicateHelper;
import com.rsquare.usecasehc.util.ProviderReferralHelper;
import com.rsquare.usecasehc.util.Util;
import com.rsquare.usecasehc.web.ProviderSpecialtyTreeServlet;

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
	
	public String getGraphDataByProvider(String npi, int option, String limit, String specialty) throws SQLException
	{
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		String results = "";
		try
		{
			c = getConnection();
			s = c.createStatement();
			String sql = makeCypherQuery(npi, option, limit, specialty);
			logger.info(sql);
			rs = s.executeQuery(sql);
			results = ProviderReferralHelper.getUIGraphResultOutputTwoLevelJSON(rs, npi); //getUIGraphResultOutputJSON(rs, npi);
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
	
	public void getReferralCountByProvider(List<Provider> results) throws SQLException
	{
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		try
		{
			c = getConnection();
			s = c.createStatement();
			StringBuilder sql = new StringBuilder("START n=node:lucidxprov(\"npi:");
			Iterator<Provider> iterator = results.iterator();
			Map<String, Provider> providers = new HashMap<String, Provider>();
			while(iterator.hasNext())
			{
				Provider p = iterator.next();
				providers.put(p.getNpi(), p);
				sql.append(p.getNpi());
				sql.append(" npi:");
			}
			sql.delete(sql.length()-5, sql.length());
			sql.append("\") MATCH n<-[r:REFERRAL]->m RETURN n.npi, count(DISTINCT m.npi)");
			logger.info(sql);
			rs = s.executeQuery(sql.toString());
			while(rs.next())
			{
				Provider p = providers.get(rs.getString(1));
				if(p!=null)	p.setCount(rs.getString(2));
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
	}
	
	public List<Location> getLocations() throws SQLException
	{
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		List<Location> results = new ArrayList<Location>();
		try
		{
			c = getConnection();
			s = c.createStatement();
			String sql = "MATCH (a:location) return a, labels(a)[1]";
			logger.info(sql);
			rs = s.executeQuery(sql);
			while(rs.next())
			{
				@SuppressWarnings("unchecked")
				Map<String, Object> location = (Map<String, Object>)rs.getObject(1);
				String location_type = rs.getString(2);
				if(location_type!=null && !location_type.equals(""))
				{
					if(LocationType.ZIPCODE.equalsType(location_type))
					{
						results.add(new Location(String.valueOf(location.get("zip_code")), ((Double)location.get("lat")).doubleValue(), ((Double)location.get("lon")).doubleValue(), 
								String.valueOf(location.get("city")), String.valueOf(location.get("state")), String.valueOf(location.get("county")),
								String.valueOf(location.get("country")), LocationType.ZIPCODE));
					}
					else if(LocationType.CITY.equalsType(location_type))
					{
						results.add(new Location(String.valueOf(location.get("name")), 0d, 0d, 
								String.valueOf(location.get("name")), String.valueOf(location.get("state")), String.valueOf(location.get("county")),
								String.valueOf(location.get("country")), LocationType.CITY));
					}
					else if(LocationType.COUNTY.equalsType(location_type))
					{
						results.add(new Location(String.valueOf(location.get("name")), 0d, 0d, 
								"", String.valueOf(location.get("state")), String.valueOf(location.get("county")),
								String.valueOf(location.get("country")), LocationType.COUNTY));
					}
					else if(LocationType.STATE.equalsType(location_type))
					{
						results.add(new Location(String.valueOf(location.get("name")), 0d, 0d, 
								"", String.valueOf(location.get("state")), "",
								String.valueOf(location.get("country")), LocationType.STATE));
					}
					else if(LocationType.COUNTRY.equalsType(location_type))
					{
						results.add(new Location(String.valueOf(location.get("name")), 0d, 0d, 
								"", "", "", String.valueOf(location.get("country")), LocationType.COUNTRY));
					}
				}
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
			String sql = makeCypherQuery(npi, option, limit, "");
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
	
	private String makeReferralCypherQuery(String npi, String limit, String specialty)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)-[r:REFERRAL]->(b:provider)  WHERE a.npi = ");
		builder.append(npi);
		if(specialtyPresent(specialty))
		{
			appendSpecialtyCypher(specialty, builder, true);
		}
		builder.append(" RETURN a as provider,b as referral,r.count as count,\"0\" as reverse_count,\"referred\" as direction order by r.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeReferralCypherQueryTwoLevel(String npi, String limit, String specialty)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)-[r:REFERRAL]->(b:provider)  WHERE a.npi = ");
		builder.append(npi);
		if(specialtyPresent(specialty))
		{
			appendSpecialtyCypher(specialty, builder, true);
		}
		builder.append(" RETURN a as provider,b as referral,r.count as count,\"0\" as reverse_count,\"referred\" as direction order by r.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		builder.append(" UNION MATCH (a:provider)-[r1:REFERRAL]->(b:provider)-[r2:REFERRAL]->(c:provider) WHERE (a)-->(c) AND a.npi = ");
		builder.append(npi);
		if(specialtyPresent(specialty))
		{
			appendSpecialtyCypher(specialty, builder, true);
		}
		builder.append(" RETURN b as provider,c as referral,r1.count as count,\"0\" as reverse_count,\"referred\" as direction");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeReferredByCypherQuery(String npi, String limit, String specialty)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)<-[r:REFERRAL]-(b:provider)  WHERE a.npi = ");
		builder.append(npi);
		if(specialtyPresent(specialty))
		{
			appendSpecialtyCypher(specialty, builder, true);
		}
		builder.append(" RETURN a as provider,b as referral,\"0\" as count,r.count as reverse_count,\"was referred by\" as direction order by r.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	private String makeAllRelationsCypherQueryOptimized(String npi, String limit, String specialty)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider {npi:");
		builder.append(npi);
		builder.append("})<-[r1:REFERRAL]->(b:provider)");
		if(specialtyPresent(specialty))
		{
			builder.append(" WHERE ");
			appendSpecialtyCypher(specialty, builder, false);
		}
		builder.append(" WITH a, b ORDER BY r1.count DESC");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		builder.append(" OPTIONAL MATCH (a:provider {npi:");
		builder.append(npi);
		builder.append("})-[r2:REFERRAL]->(b:provider) OPTIONAL MATCH (a:provider {npi:");
		builder.append(npi);
		builder.append("})<-[r3:REFERRAL]-(b:provider) RETURN DISTINCT a as provider,b as referral,r2.count as count,r3.count as reverse_count,CASE WHEN TYPE(r3) = 'REFERRAL' AND TYPE(r2) IS NULL THEN \"was referred by\" WHEN TYPE(r2) = 'REFERRAL' AND TYPE(r3) IS NULL THEN \"referred\" ELSE \"bidirectional\" END as direction");
		return builder.toString();
	}
	
	private String makeAllRelationsCypherQuery(String npi, String limit, String specialty)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)<-[r1:REFERRAL]->(b:provider) WHERE a.npi = ");
		builder.append(npi);
		if(specialtyPresent(specialty))
		{
			appendSpecialtyCypher(specialty, builder, true);
		}
		builder.append(" OPTIONAL MATCH (a:provider)-[r2:REFERRAL]->(b:provider) OPTIONAL MATCH (a:provider)<-[r3:REFERRAL]-(b:provider) WITH r2, r2.count IS NULL AS count_is_null, r3, r3.count IS NULL AS count_is_null_3, a, b WITH r2, STR(count_is_null) as null_count, r3, STR(count_is_null_3) as null_count_3, a, b ORDER BY null_count, null_count_3, CASE WHEN r2.count > r3.count THEN r2.count ELSE r3.count END DESC");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		builder.append(" RETURN DISTINCT a as provider,b as referral,r2.count as count,r3.count as reverse_count,CASE WHEN TYPE(r3) = 'REFERRAL' AND TYPE(r2) IS NULL THEN \"was referred by\" WHEN TYPE(r2) = 'REFERRAL' AND TYPE(r3) IS NULL THEN \"referred\" ELSE \"bidirectional\" END as direction");
		return builder.toString();
	}
	
	private String makeBidirectionalOnlyCypherQuery(String npi, String limit, String specialty)
	{
		StringBuilder builder = new StringBuilder("MATCH (a:provider)-[r1:REFERRAL]->(b:provider)-[r2:REFERRAL]->(a:provider)  WHERE a.npi = ");
		builder.append(npi);
		if(specialtyPresent(specialty))
		{
			appendSpecialtyCypher(specialty, builder, true);
		}
		builder.append(" RETURN a as provider,b as referral,r1.count as count,r2.count as reverse_count,\"bidirectional\" as direction order by r1.count,r2.count desc");
		if(Util.isInteger(limit))
		{
			builder.append(addLimitCypher(limit));
		}
		return builder.toString();
	}
	
	
	private String makeCypherQuery(String npi, int option, String limit, String specialty)
	{
		String retString = null;
		switch(option)
		{
		case 0:
		case 1:
			retString = makeReferralCypherQueryTwoLevel(npi, limit, specialty); //makeReferralCypherQuery(npi, limit, specialty);
			break;
		case 2:
			retString = makeReferredByCypherQuery(npi, limit, specialty);
			break;
		case 3:
		case 7:
			retString = makeAllRelationsCypherQueryOptimized(npi, limit, specialty); //makeAllRelationsCypherQuery(npi, limit, specialty);
			break;
		case 4:
		case 5:
		case 6:
			retString = makeBidirectionalOnlyCypherQuery(npi, limit, specialty);
			break;
		}
		return retString;
	}
	
	private String addLimitCypher(String limit)
	{
		return (" LIMIT " + limit);
	}
	
	private boolean specialtyPresent(String specialty)
	{
		if(specialty!=null && !specialty.equals("") && !specialty.equals("undefined"))
		{
			return true;
		}
		return false;
	}
	
	private void appendSpecialtyCypher(String specialty, StringBuilder builder, boolean appendAnd)
	{
		Map<String, ProviderSpecialtyNode> nodes = PredicateHelper.getFlatFilteredProviderSpecialtyNodes(ProviderSpecialtyTreeServlet.nodes.getNodes(), specialty);
		if(appendAnd)
		{
			builder.append(" AND ");
		}
		builder.append("a.healthcare_provider_taxonomy_code_1 IN [\"");
		Iterator<String> iterator = nodes.keySet().iterator();
        
        while(iterator.hasNext())
        {
        	builder.append(iterator.next() + "\",\"");
        }
 
        //remove the last comma
        builder.delete(builder.length()-2, builder.length());
        builder.append("]");
	}

}
