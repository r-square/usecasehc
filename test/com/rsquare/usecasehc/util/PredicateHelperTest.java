package com.rsquare.usecasehc.util;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.rsquare.usecasehc.model.ProviderSpecialtyNode;
import com.rsquare.usecasehc.model.ProviderSpecialtyNodes;

public class PredicateHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetFlatFilteredProviderSpecialtyNodes() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("WebContent/sources/provider_taxonomy_specialty_sample.json"));
			Gson gson = new Gson();
			ProviderSpecialtyNodes nodes = gson.fromJson(reader, ProviderSpecialtyNodes.class);
			Map<String, ProviderSpecialtyNode> providers = PredicateHelper.getFlatFilteredProviderSpecialtyNodes(nodes.getNodes(), "789");
			assertNotNull(providers);
			System.out.println(providers);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetFlatFilteredProviderSpecialtyNodes2() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("WebContent/sources/provider_taxonomy_specialty_sample.json"));
			Gson gson = new Gson();
			ProviderSpecialtyNodes nodes = gson.fromJson(reader, ProviderSpecialtyNodes.class);
			Map<String, ProviderSpecialtyNode> providers = PredicateHelper.getFlatFilteredProviderSpecialtyNodes(nodes.getNodes(), "1234");
			assertNotNull(providers);
			System.out.println(providers);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetFlatFilteredProviderSpecialtyNodes3() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("WebContent/sources/provider_taxonomy_specialty_sample.json"));
			Gson gson = new Gson();
			ProviderSpecialtyNodes nodes = gson.fromJson(reader, ProviderSpecialtyNodes.class);
			Map<String, ProviderSpecialtyNode> providers = PredicateHelper.getFlatFilteredProviderSpecialtyNodes(nodes.getNodes(), "890");
			assertNotNull(providers);
			System.out.println(providers);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
