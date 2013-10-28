package com.rsquare.usecasehc.hive;

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderReferralResult;

public class HiveClientTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConnect() throws SQLException {
		HiveClient hc = new HiveClient();
		assertNotNull(hc.getConnection());
	}

	@Test
	public void testGetProviderById() throws SQLException {
		HiveClient hc = new HiveClient();
		Provider p = hc.getProviderById("1043377500");
		assertNotNull(p);
		System.out.printf("%s%n", p);
	}
	
	@Test
	public void testMakeQueryString() throws SQLException {
		HiveClient hc = new HiveClient();
		List<ProviderReferralResult> results = new ArrayList<ProviderReferralResult>();
		results.add(new ProviderReferralResult("x", "1", "", "", ""));
		results.add(new ProviderReferralResult("x", "2", "", "", ""));
		results.add(new ProviderReferralResult("x", "3", "", "", ""));
		StringBuilder sql = hc.makeQueryString(results, "1043377500");
		assertNotNull(sql);
		System.out.println(sql);
	}

}
