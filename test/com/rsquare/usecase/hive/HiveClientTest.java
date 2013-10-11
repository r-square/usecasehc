package com.rsquare.usecase.hive;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rsquare.usecasehc.hive.HiveClient;
import com.rsquare.usecasehc.model.Provider;

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
	public void testGetProvider() throws SQLException {
		HiveClient hc = new HiveClient();
		Provider p = hc.getProvider(123123123);
		assertNotNull(p);
		System.out.printf("%s %s%n", p.getFirst(), p.getLast());
	}

}
