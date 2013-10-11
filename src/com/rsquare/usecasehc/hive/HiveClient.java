package com.rsquare.usecasehc.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.web.Usecase_HC_Ecample;

public class HiveClient {

	private Logger logger = Logger.getLogger(HiveClient.class);
	
	public HiveClient() {
		String driver = "org.apache.hadoop.hive.jdbc.HiveDriver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.debug(e);
		}

	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				"jdbc:hive://localhost:10000/default", "", "");
	}
	
	public Provider getProvider(int npi) throws SQLException {
        Statement stmt = getConnection().createStatement();
        String sql = "" +
                "SELECT npi, provider_organization_name_legal_business_name_,provider_first_name,provider_last_name,healthcare_provider_taxonomy_code_1" +
                "FROM providers" +
                "WHERE providers.npi = " + npi;
 
        ResultSet res = stmt.executeQuery(sql);
        res.next();
        return new Provider(res);
    }


}
