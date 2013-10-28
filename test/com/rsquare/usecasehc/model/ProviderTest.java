package com.rsquare.usecasehc.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class ProviderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetGraphViewButtonHTML() {
		Provider p = new Provider("1043377500", null, null, null, null, null, null);
		assertEquals("<input type='button' value='View' onclick=\"javascript:loadIframe('graphFrame', '1043377500');javascript:$('#tab-container').easytabs('select', '#network-graph-tab'); showNetworkGraphLeftPanel();\" />", 
				p.getGraphViewButtonHTML());
		System.out.println(p.getGraphViewButtonHTML());
	}
	
	@Test
	public void testProviderJSON() {
		Provider p1 = new Provider("1043377500", "Detroit Medical Labs", "", "", "xyz", "Lab", "Agency");
		Provider p2 = new Provider("1477664738", "", "John", "Smith", "xyz", "Surgeon", "Surgery");
		List<Provider> list = new ArrayList<Provider>();
		list.add(p1);
		list.add(p2);
		StringBuilder b = new StringBuilder();
		Iterator<Provider> iterator = list.iterator();
		b.append("{ \"providerList\": [");
		while(iterator.hasNext())
		{
			b.append("\n");
			Provider p = iterator.next();
			b.append(p.toJSONString());
			b.append(",");
		}
		b.deleteCharAt(b.length()-1);
		b.append("\n]}");
		System.out.println(b.toString());
//		Gson g = new Gson();
//		System.out.println(g.toJson(list));
	}

}