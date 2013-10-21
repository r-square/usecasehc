package com.rsquare.usecasehc.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderReferralResult;

public class HiveClient {

	private Logger logger = Logger.getLogger(HiveClient.class);
	
	public HiveClient() {
		String driver = "org.apache.hive.jdbc.HiveDriver"; //org.apache.hadoop.hive.jdbc.HiveDriver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.debug(e);
		}

	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				"jdbc:hive2://localhost:10000/default", "", "");
	}
	
	public Provider getProvider(int npi) throws SQLException {
        Statement stmt = getConnection().createStatement();
        String sql = "" +
                "SELECT npi, provider_organization_name_legal_business_name_,provider_first_name,provider_last_name_legal_name_,"
                + "healthcare_provider_taxonomy_code_1" +
                " FROM providers" +
                " WHERE providers.npi = " + npi + " limit 1";
 
        ResultSet res = stmt.executeQuery(sql);
        if(res.next())
        {
        	return new Provider(res);
        }
        else
        {
        	return null;
        }
    }
	
	public Map<String, Provider> getProviders(List<ProviderReferralResult> results, String pid) throws SQLException {
        Statement stmt = getConnection().createStatement();
        Map<String, Provider> providers = new HashMap<String, Provider>();
        String sql = makeQueryString(results, pid).toString();

        logger.info(sql);
        ResultSet res = stmt.executeQuery(sql);
        while(res.next())
        {
        	Provider p = new Provider(res);
        	providers.put(p.getNpi(), p);
        }
        
        return providers;
        
    }
	
	public StringBuilder makeQueryString(List<ProviderReferralResult> results, String pid)
	{
		StringBuilder sql = new StringBuilder(
                "SELECT npi, provider_organization_name_legal_business_name_,provider_first_name,provider_last_name_legal_name_,"
                + "healthcare_provider_taxonomy_code_1 FROM providers WHERE providers.npi in (" + pid + "," );
        Iterator<ProviderReferralResult> iterator = results.iterator();
        
        while(iterator.hasNext())
        {
        	ProviderReferralResult result = iterator.next();
        	sql.append(result.getReferredDoctor() + ",");
        }
 
        //remove the last comma
        sql.delete(sql.length()-1, sql.length());
        sql.append(")");
        return sql;
	}


}
