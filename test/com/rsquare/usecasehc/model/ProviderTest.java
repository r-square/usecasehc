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
//		assertEquals("<input type='button' value='View' onclick=\"javascript:loadIframe('graphFrame', '1043377500');javascript:$('#tab-container').easytabs('select', '#network-graph-tab'); showNetworkGraphLeftPanel();\" />", 
//				p.getGraphViewButtonHTML());
//		System.out.println(p.getGraphViewButtonHTML());
	}
	

}