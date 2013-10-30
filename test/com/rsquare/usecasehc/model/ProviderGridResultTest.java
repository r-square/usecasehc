package com.rsquare.usecasehc.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class ProviderGridResultTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Provider p1 = new Provider("1043377500", "Detroit Medical Labs", "", "", "xyz", "Lab", "Agency");
		Provider p2 = new Provider("1477664738", "", "John", "Smith", "xyz", "Surgeon", "Surgery");
		Provider p3 = new Provider("14776647380000", "", "John", "Smith", "xyz", "Surgeon", "Surgery");
		List<Provider> list = new ArrayList<Provider>();
		list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);
		list.add(p2);
		list.add(p2);
		list.add(p2);
		list.add(p2);
		list.add(p2);
		list.add(p2);
		list.add(p2);
		list.add(p2);
		list.add(p3);
		ProviderGridResult pgr = new ProviderGridResult(String.valueOf(list.size()), String.valueOf(list.size()), list);
		Gson g = new Gson();
		System.out.println(g.toJson(pgr));
	}
}
