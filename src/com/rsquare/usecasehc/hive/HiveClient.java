package com.rsquare.usecasehc.hive;

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

import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderReferralResult;
import com.rsquare.usecasehc.model.ProviderSpecialtyNode;

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
	
	public Provider getProviderById(String npi) throws SQLException {
        Statement stmt = getConnection().createStatement();
        String sql = "" +
        		"SELECT p.npi, p.provider_organization_name_legal_business_name_,p.provider_first_name,p.provider_last_name_legal_name_,p.healthcare_provider_taxonomy_code_1,p.provider_business_mailing_address_city_name,p.provider_business_mailing_address_state_name,ps.general_area, ps.specialty FROM providers p join provider_specialty ps"
        		 + " WHERE p.healthcare_provider_taxonomy_code_1 = ps.taxonomy and p.npi = " + npi + " limit 1";
        logger.info(sql);
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
	
	public Map<String, Provider> getProvidersByReferrals(List<ProviderReferralResult> results, String pid) throws SQLException {
        Statement stmt = getConnection().createStatement();
        Map<String, Provider> providers = new HashMap<String, Provider>();
        String sql = makeQueryStringForProviderId(results, pid).toString();
        logger.info(sql);
        ResultSet res = stmt.executeQuery(sql);
        while(res.next())
        {
        	Provider p = new Provider(res);
        	providers.put(p.getNpi(), p);
        }
        
        return providers;
        
    }
	
	public List<Provider> getProvidersByState(String state) throws SQLException {
		List<Provider> list = new ArrayList<Provider>();
        Statement stmt = getConnection().createStatement();
        String sql = "" +
        		"SELECT p.npi, p.provider_organization_name_legal_business_name_,p.provider_first_name,p.provider_last_name_legal_name_,p.healthcare_provider_taxonomy_code_1,ps.general_area,p.provider_business_mailing_address_city_name,p.provider_business_mailing_address_state_name, ps.specialty FROM providers p join provider_specialty ps"
        		 + " WHERE p.healthcare_provider_taxonomy_code_1 = ps.taxonomy and UPPER(p.provider_business_mailing_address_state_name) = '" + state + "'";
        logger.info(sql);
        ResultSet res = stmt.executeQuery(sql);
        while(res.next())
        {
        	list.add(new Provider(res));
        }
        return list;
    }
	
	public List<Provider> getProvidersBySpecialty(Map<String, ProviderSpecialtyNode> providers) throws SQLException {
		List<Provider> list = new ArrayList<Provider>();
        Statement stmt = getConnection().createStatement();
        String sql = makeQueryStringForTaxonomy(providers).toString();
        logger.info(sql);
        ResultSet res = stmt.executeQuery(sql);
        while(res.next())
        {
        	String taxonomy = res.getString("healthcare_provider_taxonomy_code_1");
        	ProviderSpecialtyNode node = providers.get(taxonomy);
        	Provider p = new Provider(res.getString("npi"), res.getString("provider_organization_name_legal_business_name_"), res.getString("provider_first_name"),
        			res.getString("provider_last_name_legal_name_"),taxonomy,"", node.getLabel());
        	list.add(p);
        }
        return list;
    }
	
	public List<Provider> getProvidersByStateAndSpecialty(Map<String, ProviderSpecialtyNode> providers, String state) throws SQLException {
		List<Provider> list = new ArrayList<Provider>();
        Statement stmt = getConnection().createStatement();
        String sql = makeQueryStringForTaxonomyAndState(providers, state).toString();
        logger.info(sql);
        ResultSet res = stmt.executeQuery(sql);
        while(res.next())
        {
        	String taxonomy = res.getString("healthcare_provider_taxonomy_code_1");
        	ProviderSpecialtyNode node = providers.get(taxonomy);
        	Provider p = new Provider(res.getString("npi"), res.getString("provider_organization_name_legal_business_name_"), res.getString("provider_first_name"),
        			res.getString("provider_last_name_legal_name_"),taxonomy,"", node.getLabel());
        	list.add(p);
        }
        return list;
    }
	
	private StringBuilder makeQueryStringForProviderId(List<ProviderReferralResult> results, String pid)
	{
		StringBuilder sql = new StringBuilder(
                "SELECT p.npi, p.provider_organization_name_legal_business_name_,p.provider_first_name,p.provider_last_name_legal_name_,p.healthcare_provider_taxonomy_code_1,p.provider_business_mailing_address_city_name,p.provider_business_mailing_address_state_name,ps.general_area, ps.specialty FROM providers p join provider_specialty ps"
                + " WHERE p.healthcare_provider_taxonomy_code_1 = ps.taxonomy and p.npi in (" + pid + "," );
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
	
	private StringBuilder makeQueryStringForTaxonomy(Map<String, ProviderSpecialtyNode> nodes)
	{
		StringBuilder sql = new StringBuilder(
				 "SELECT p.npi, p.provider_organization_name_legal_business_name_,p.provider_first_name,p.provider_last_name_legal_name_,p.healthcare_provider_taxonomy_code_1,p.provider_business_mailing_address_city_name,p.provider_business_mailing_address_state_name,ps.general_area, ps.specialty FROM providers p join provider_specialty ps"
                + " WHERE p.healthcare_provider_taxonomy_code_1 = ps.taxonomy and p.healthcare_provider_taxonomy_code_1 in ('");
        Iterator<String> iterator = nodes.keySet().iterator();
        
        while(iterator.hasNext())
        {
        	sql.append(iterator.next() + "','");
        }
 
        //remove the last comma
        sql.delete(sql.length()-2, sql.length());
        sql.append(")");
        return sql;
	}
	
	private StringBuilder makeQueryStringForTaxonomyAndState(Map<String, ProviderSpecialtyNode> nodes, String state)
	{
		StringBuilder sql = new StringBuilder(
				"SELECT p.npi, p.provider_organization_name_legal_business_name_,p.provider_first_name,p.provider_last_name_legal_name_,p.healthcare_provider_taxonomy_code_1,p.provider_business_mailing_address_city_name,p.provider_business_mailing_address_state_name,ps.general_area, ps.specialty FROM providers p join provider_specialty ps"
                + " WHERE p.healthcare_provider_taxonomy_code_1 = ps.taxonomy and UPPER(p.provider_business_mailing_address_state_name) = '" + state + "' and p.healthcare_provider_taxonomy_code_1 in ('");
		Iterator<String> iterator = nodes.keySet().iterator();
        
        while(iterator.hasNext())
        {
        	sql.append(iterator.next() + "','");
        }
 
        //remove the last comma
        sql.delete(sql.length()-2, sql.length());
        sql.append(")");
        return sql;
	}


}
