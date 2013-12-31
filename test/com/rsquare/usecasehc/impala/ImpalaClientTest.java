package com.rsquare.usecasehc.impala;

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderReferralResult;
import com.rsquare.usecasehc.model.ProviderSpecialtyNode;

public class ImpalaClientTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConnect() throws SQLException {
		ImpalaClient hc = new ImpalaClient();
		assertNotNull(hc.getConnection());
	}

	@Test
	public void testGetProviderById() throws SQLException {
		ImpalaClient hc = new ImpalaClient();
		Provider p = hc.getProviderById("1043377500");
		assertNotNull(p);
		System.out.printf("%s%n", p);
	}
	
	@Test
	public void testMakeQueryStringForProviderId() throws SQLException {
		ImpalaClient hc = new ImpalaClient();
		List<ProviderReferralResult> results = new ArrayList<ProviderReferralResult>();
		results.add(new ProviderReferralResult("x", "1", "", "", ""));
		results.add(new ProviderReferralResult("x", "2", "", "", ""));
		results.add(new ProviderReferralResult("x", "3", "", "", ""));
		hc.getProvidersByReferrals(results, "1043377500");
	}
	
	@Test
	public void testMakeQueryStringForTaxonomy() throws SQLException {
		ImpalaClient hc = new ImpalaClient();
		Map<String, ProviderSpecialtyNode> nodes = new HashMap<String, ProviderSpecialtyNode>();
		nodes.put("1234", new ProviderSpecialtyNode("1", "1", null));
		nodes.put("678", new ProviderSpecialtyNode("2", "2", null));
		nodes.put("800", new ProviderSpecialtyNode("3", "3", null));
		nodes.put("1000", new ProviderSpecialtyNode("4", "5", null));
		hc.getProvidersBySpecialty(nodes);
	}
	
	@Test
	public void testMakeQueryStringForTaxonomyAndState() throws SQLException {
		ImpalaClient hc = new ImpalaClient();
		Map<String, ProviderSpecialtyNode> nodes = new HashMap<String, ProviderSpecialtyNode>();
		nodes.put("1234", new ProviderSpecialtyNode("1", "1", null));
		nodes.put("678", new ProviderSpecialtyNode("2", "2", null));
		nodes.put("800", new ProviderSpecialtyNode("3", "3", null));
		nodes.put("1000", new ProviderSpecialtyNode("4", "5", null));
		hc.getProvidersByStateAndSpecialty(nodes, "NJ");
	}

}
