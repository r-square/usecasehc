package com.rsquare.usecasehc.neo4j;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rsquare.usecasehc.model.Location;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderReferralResult;

public class Neo4jClientTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetConnection() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		assertNotNull(nc.getConnection());
	}
	
	@Test
	public void testGetReferralsByProvider() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<ProviderReferralResult> results = nc.getReferralsByProvider("1184764110", 1, "10");
		assertNotNull(results);
		assertTrue(results.size()==2);
		System.out.println(results);
	}
	
	@Test
	public void testGetReferralsByProvider2() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<ProviderReferralResult> results = nc.getReferralsByProvider("1184764110", 2, "10");
		assertNotNull(results);
		assertTrue(results.size()==2);
		System.out.println(results);
	}
	
	@Test
	public void testGetReferralsByProvider3() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<ProviderReferralResult> results = nc.getReferralsByProvider("1184764110", 3, "1");
		assertNotNull(results);
		assertTrue(results.size()==1);
		System.out.println(results);
	}
	
	@Test
	public void testGetReferralsByProvider4() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<ProviderReferralResult> results = nc.getReferralsByProvider("1184764110", 4, "10");
		assertNotNull(results);
		assertTrue(results.size()==2);
		System.out.println(results);
	}
	
	@Test
	public void testGetReferralsByProvider5() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<ProviderReferralResult> results = nc.getReferralsByProvider("1164464541", 1, "10");
		assertNotNull(results);
		assertTrue(results.size()==1);
		System.out.println(results);
	}
	
	@Test
	public void testGetReferralsByProvider6() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<ProviderReferralResult> results = nc.getReferralsByProvider("1164464541", 2, "10");
		assertNotNull(results);
		assertTrue(results.size()==0);
		System.out.println(results);
	}
	
	@Test
	public void testGetReferralsByProvider7() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<ProviderReferralResult> results = nc.getReferralsByProvider("1164464541", 3, "10");
		assertNotNull(results);
		assertTrue(results.size()==1);
		System.out.println(results);
	}
	
	@Test
	public void testGetReferralsByProvider8() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<ProviderReferralResult> results = nc.getReferralsByProvider("1164464541", 4, "10");
		assertNotNull(results);
		assertTrue(results.size()==0);
		System.out.println(results);
	}
	
	@Test
	public void testGetReferralCountByProvider() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		Provider p1 = new Provider("1043377500", "", "", "", "", "", "");
		Provider p2 = new Provider("1851613178", "", "", "", "", "", "");
		List<Provider> results = new ArrayList<Provider>(Arrays.asList(p1, p2));
		nc.getReferralCountByProvider(results);
		assertNotNull(results);
//		assertTrue(results.size()==0);
		System.out.println(results);
	}
	
	@Test
	public void testGetLocations() throws SQLException{
		Neo4jClient nc = new Neo4jClient();
		List<Location> locations = nc.getLocations();
		assertNotNull(locations);
		assertTrue(locations.size() == 78688);
//		System.out.println(locations);
	}

}
