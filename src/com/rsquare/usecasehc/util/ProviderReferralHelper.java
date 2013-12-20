package com.rsquare.usecasehc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.rsquare.usecasehc.dto.ProviderGraph;
import com.rsquare.usecasehc.dto.ProviderGraphEdge;
import com.rsquare.usecasehc.dto.ProviderGraphEdgeData;
import com.rsquare.usecasehc.dto.ProviderGraphNode;
import com.rsquare.usecasehc.dto.ProviderGraphNodeData;
import com.rsquare.usecasehc.model.ProviderReferralResult;

public class ProviderReferralHelper {
	
	private static Logger logger = Logger.getLogger(ProviderReferralHelper.class);
	
	public static String getUIGraphResultOutputJSON(ResultSet rs, String pid) throws SQLException
	{
		List<ProviderGraphNode> nodes = new ArrayList<ProviderGraphNode>();
		List<ProviderGraphEdge> edges = new ArrayList<ProviderGraphEdge>();
		List<String> rNodes = new ArrayList<String>();
		int maxCount = 0;
		
		while ( rs.next() )
	    {
			@SuppressWarnings("unchecked")
			Map<String, String> doctor = (Map<String, String>)rs.getObject(1);
			@SuppressWarnings("unchecked")
			Map<String, String> referredDoctor = (Map<String, String>)rs.getObject(2);
			String count = rs.getString(3);
			String reverse_count = rs.getString(4);
			String direction = rs.getString(5);
            String doctor_pid = String.valueOf(doctor.get("npi"));
            String refdoctor_pid = String.valueOf(referredDoctor.get("npi"));
            ProviderGraphNodeData nData;
            ProviderGraphNode node;
            
            //this is the first loop, now set the selected node details
            if(maxCount==0)
            {
            	String name = makeNameString(doctor);
            	nData = new ProviderGraphNodeData(name,"2", name, "Impact, Charcoal, sans-serif", "#CC0000", "", "", "", "", name, doctor.get("specialty"), doctor.get("general_area"), doctor.get("provider_business_mailing_address_city_name"), doctor.get("provider_business_mailing_address_state_name"), "#ffffcc");
        		node = new ProviderGraphNode(pid, nData);
        		nodes.add(node);
            }
            
	        if(rNodes.contains(refdoctor_pid))
	        {
	        	logger.info("==== Node Already present ====== " + refdoctor_pid);
	        	continue;
	        }
	        rNodes.add(doctor_pid);
	        
	        int fCount = Util.isInteger(count) ? Integer.parseInt(count) : 0;
	        int rCount = Util.isInteger(reverse_count) ? Integer.parseInt(reverse_count) : 0;
	        int display_count = Math.max(fCount, rCount);
	        String name = makeNameString(referredDoctor);
//	        p = providers.get(result.getReferredDoctor());
	        
	        if("\"referred\"".equalsIgnoreCase(direction) || "referred".equalsIgnoreCase(direction))
		    {
	        	nData = new ProviderGraphNodeData(name,"1", name, "", "", "image", "images/doctor_icon.png", "50", "50", name, doctor.get("specialty"), doctor.get("general_area"), doctor.get("provider_business_mailing_address_city_name"), doctor.get("provider_business_mailing_address_state_name"), "#2262A0");
	        	
	        	ProviderGraphEdgeData eData = new ProviderGraphEdgeData("provided " + count + " referrals", 
	        			"#2262A0", null);
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((doctor_pid + refdoctor_pid).hashCode())), 
	        			refdoctor_pid, doctor_pid, eData);
	        	edges.add(edge);
		    }
		    else if("\"was referred by\"".equalsIgnoreCase(direction) || "was referred by".equalsIgnoreCase(direction))
		    {
		    	nData = new ProviderGraphNodeData(name,"1", name, "", "", "image", "images/doctor_icon.png", "50", "50", name, doctor.get("specialty"), doctor.get("general_area"), doctor.get("provider_business_mailing_address_city_name"), doctor.get("provider_business_mailing_address_state_name"), "#DA6315");
		    	ProviderGraphEdgeData eData = new ProviderGraphEdgeData("received " + reverse_count + " referrals", 
	        			"#DA6315", null);
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((doctor_pid + refdoctor_pid).hashCode())), 
	        			doctor_pid, refdoctor_pid, eData);
	        	edges.add(edge);
		    }
		    else
		    {
		    	nData = new ProviderGraphNodeData(name,"1", name, "", "", "image", "images/doctor_icon.png", "50", "50", name, doctor.get("specialty"), doctor.get("general_area"), doctor.get("provider_business_mailing_address_city_name"), doctor.get("provider_business_mailing_address_state_name"), "#FF0000");
		    	String tooltip = "provided " + count + " referrals&#13;" + "received " + reverse_count + " referrals";
		    	ProviderGraphEdgeData eData = new ProviderGraphEdgeData(tooltip,"#FF0000", "true");
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((doctor_pid + refdoctor_pid).hashCode())), 
	        			doctor_pid, refdoctor_pid, eData);
	        	edges.add(edge);
		    }
	        
	        node = new ProviderGraphNode(refdoctor_pid, nData);
	        node.setRefCount(display_count);
	        nodes.add(node);
	        
	        if(maxCount < display_count) maxCount = display_count;
	        			
	    }
		
		Iterator<ProviderGraphNode> iterator2 = nodes.iterator();
		while(iterator2.hasNext())
		{
			ProviderGraphNode n = iterator2.next();
			int graphicSize = 40 + (int) (n.getRefCount() * 1d / maxCount * 150);
			n.getData().setGraphicSize(String.valueOf(graphicSize));
			n.getData().setSelectedgraphicsize(String.valueOf(graphicSize));
		}
		
		ProviderGraph graph = new ProviderGraph(nodes, edges);
		Gson gson = new Gson();
		return gson.toJson(graph);
	}
	
	private static String makeNameString(Map<String, String> referredDoctor) {
		StringBuilder builder = new StringBuilder();
		String organization = referredDoctor.get("provider_organization_name_legal_business_name_");
		String first = referredDoctor.get("provider_first_name");
		String last = referredDoctor.get("provider_last_name_legal_name_");
		if(organization==null || organization.equals(""))
		{
			builder.append((first==null || first.equals("")) ? "" : first);
			builder.append((first==null || first.equals("")) && (last==null || last.equals("")) ? "" : " ");
			builder.append((last==null || last.equals("")) ? "" : last);
		}
		else
		{
			builder.append(organization);
		}
		return builder.toString();
	}
	
	public static String getUIGraphResultOutputJSON(List<ProviderReferralResult> results, String pid)
	{
		List<ProviderGraphNode> nodes = new ArrayList<ProviderGraphNode>();
		List<ProviderGraphEdge> edges = new ArrayList<ProviderGraphEdge>();
		List<ProviderReferralResult> rNodes = new ArrayList<ProviderReferralResult>();
		Iterator<ProviderReferralResult> iterator = results.iterator();
		int maxCount = 0;
//		Provider p = providers.get(pid);
		ProviderGraphNodeData nData = new ProviderGraphNodeData(pid, 
				"2", pid, "Impact, Charcoal, sans-serif", "#CC0000", "", "", "", "", "", "", "", "", "", "#ffffcc");
		ProviderGraphNode node = new ProviderGraphNode(pid, nData);
		nodes.add(node);
		while ( iterator.hasNext() )
	    {
			ProviderReferralResult result = iterator.next();
	        if(rNodes.contains(result))
	        {
	        	logger.info("==== Node Already present ======" + result);
	        	continue;
	        }
	        rNodes.add(result);
	        
	        int fCount = Util.isInteger(result.getReferralCount()) ? Integer.parseInt(result.getReferralCount()) : 0;
	        int rCount = Util.isInteger(result.getReverseCount()) ? Integer.parseInt(result.getReverseCount()) : 0;
	        int count = Math.max(fCount, rCount);
//	        p = providers.get(result.getReferredDoctor());
	        nData = new ProviderGraphNodeData(result.getReferredDoctor(), 
					"1", result.getReferredDoctor(), "", "", "image", "images/doctor_icon.png", "50", "50",  "", "", "", "", "", "#ffffcc");
	        node = new ProviderGraphNode(result.getReferredDoctor(), nData);
	        node.setRefCount(count);
	        nodes.add(node);
	        
	        if("\"referred\"".equalsIgnoreCase(result.getDirection()) || "referred".equalsIgnoreCase(result.getDirection()))
		    {
	        	ProviderGraphEdgeData eData = new ProviderGraphEdgeData("provided " + count + " referrals", 
	        			"#2262A0", null);
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((result.getDoctor() + result.getReferredDoctor()).hashCode())), 
	        			result.getReferredDoctor(), result.getDoctor(), eData);
	        	edges.add(edge);
		    }
		    else if("\"was referred by\"".equalsIgnoreCase(result.getDirection()) || "was referred by".equalsIgnoreCase(result.getDirection()))
		    {
		    	ProviderGraphEdgeData eData = new ProviderGraphEdgeData("received " + result.getReverseCount() + " referrals", 
	        			"#DA6315", null);
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((result.getDoctor() + result.getReferredDoctor()).hashCode())), 
	        			result.getDoctor(), result.getReferredDoctor(), eData);
	        	edges.add(edge);
		    }
		    else
		    {
		    	String tooltip = "provided " + count + " referrals\\n" + "received " + result.getReverseCount() + " referrals";
		    	ProviderGraphEdgeData eData = new ProviderGraphEdgeData(tooltip,"#FF0000", "true");
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((result.getDoctor() + result.getReferredDoctor()).hashCode())), 
	        			result.getReferredDoctor(), result.getDoctor(), eData);
	        	edges.add(edge);
		    }
	        
	        if(maxCount < count) maxCount = count;
	        			
	    }
		
		Iterator<ProviderGraphNode> iterator2 = nodes.iterator();
		while(iterator2.hasNext())
		{
			ProviderGraphNode n = iterator2.next();
			int graphicSize = 20 + (int) (n.getRefCount() * 1d / maxCount * 50);
			n.getData().setGraphicSize(String.valueOf(graphicSize));
			n.getData().setSelectedgraphicsize(String.valueOf(graphicSize));
		}
		
		ProviderGraph graph = new ProviderGraph(nodes, edges);
		Gson gson = new Gson();
		return gson.toJson(graph);
	}
	
	public static String getUIGraphResultOutputXML(List<ProviderReferralResult> results, String pid)
	{
		final String begin = "<graph_data>";
		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();
		final String end = "</graph_data>";
		List<ProviderReferralResult> rNodes = new ArrayList<ProviderReferralResult>();
		Iterator<ProviderReferralResult> iterator = results.iterator();
//		Provider p = providers.get(pid);
//		String name = p.getName();
		nodes.append("<nodes>\n<node id=\"" + pid + "\" label=\"" + pid + "\" depth_loaded=\"2\" tooltip=\"" + 
				pid + "\" label_font_family=\"Impact, Charcoal, sans-serif\" selected_graphic_fill_color=\"#CC0000\" />\n");
		edges.append("<edges>\n");
		while ( iterator.hasNext() )
	    {
	      ProviderReferralResult result = iterator.next();
	      if(rNodes.contains(result))
	      {
	    	  logger.info("==== Node Already present ======" + result);
	    	  continue;
	      }
	      rNodes.add(result);
	      
//	      p = providers.get(result.getReferredDoctor());
	      String name = result.getReferredDoctor(); //p.getName();
	      nodes.append("<node id=\"");
	      nodes.append(result.getReferredDoctor());
	      nodes.append("\" label=\"");
	      nodes.append(name);
//	      nodes.append("\\n" + p.getGeneral_area() + " - " + p.getSpecialty());
	      nodes.append("\" depth_loaded=\"1\" tooltip=\"");
	      nodes.append(name);
	      nodes.append("\" graphic_type=\"image\" graphic_image_url=\"images/doctor_icon.png\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
	      
	      
	      edges.append("<edge id=\"" + Math.abs((result.getDoctor() + result.getReferredDoctor()).hashCode()) + "\"");
	      if("\"referred\"".equalsIgnoreCase(result.getDirection()) || "referred".equalsIgnoreCase(result.getDirection()))
	      {
	    	  edges.append(" head_node_id=\"");
		      edges.append(result.getReferredDoctor());
		      edges.append("\" tail_node_id=\"");
		      edges.append(result.getDoctor());
	    	  edges.append("\" tooltip=\"provided " + result.getReferralCount() + " referrals\" edge_line_color=\"#2262A0\"/>\n");
	      }
	      else if("\"was referred by\"".equalsIgnoreCase(result.getDirection()) || "was referred by".equalsIgnoreCase(result.getDirection()))
	      {
	    	  edges.append(" head_node_id=\"");
		      edges.append(result.getDoctor());
		      edges.append("\" tail_node_id=\"");
		      edges.append(result.getReferredDoctor());
	    	  edges.append("\" tooltip=\"received " + result.getReverseCount() + " referrals\" edge_line_color=\"#DA6315\"/>\n");
	      }
	      else
	      {
	    	  edges.append(" head_node_id=\"");
		      edges.append(result.getReferredDoctor());
		      edges.append("\" tail_node_id=\"");
		      edges.append(result.getDoctor());
	    	  String tooltip = "provided " + result.getReferralCount() + " referrals\\n" + "received " + result.getReverseCount() + " referrals";
	    	  edges.append("\" tooltip=\"" + tooltip + "\" edge_line_color=\"#FF0000\" bidirectional=\"true\"/>\n"); 
	      }
	    }
		
		nodes.append("</nodes>\n");
		edges.append("</edges>\n");
		
		return (begin + "\n" + (nodes.append(edges.toString())).toString() + "\n" + end);
	}
	
	public static StringBuilder getNodesXML(List<ProviderReferralResult> results, String pid)
	{
		List<ProviderReferralResult> nodes = new ArrayList<ProviderReferralResult>();
		StringBuilder sb = new StringBuilder();
		sb.append("<nodes>\n<node id=\"" + pid.replace(":", "") + "\" label=\"" + pid.replace(":", "") + "\" depth_loaded=\"2\" tooltip=\"" + 
				pid.replace(":", "") + "\" label_font_family=\"Impact, Charcoal, sans-serif\" selected_graphic_fill_color=\"#CC0000\" />\n");
		Iterator<ProviderReferralResult> iterator = results.iterator();
		for ( ; iterator.hasNext() ; )
	    {
	      ProviderReferralResult result = iterator.next();
	      if(nodes.contains(result))
	      {
	    	  logger.info("==== Node Already present ======" + result);
	    	  continue;
	      }
	      nodes.add(result);
	      String referred_doctor = result.getReferredDoctor();
	      sb.append("<node id=\"");
	      sb.append(referred_doctor);
	      sb.append("\" label=\"");
	      sb.append(referred_doctor);
	      sb.append("\" depth_loaded=\"1\" tooltip=\"");
	      sb.append(referred_doctor);
	      sb.append("\" graphic_type=\"image\" graphic_image_url=\"images/doctor_icon.png\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
	      
	    }
		sb.append("</nodes>\n");
		return sb;
	}
	
	public static StringBuilder getEdgesXML(List<ProviderReferralResult> results)
	{
		List<ProviderReferralResult> edges = new ArrayList<ProviderReferralResult>();
		StringBuilder sb = new StringBuilder();
		sb.append("<edges>\n");
		Iterator<ProviderReferralResult> iterator = results.iterator();
		for ( ; iterator.hasNext() ; )
	    {
	      ProviderReferralResult result = iterator.next();
	      if(edges.contains(result))
	      {
	    	  logger.info("==== Already present ======" + result);
	    	  continue;
	      }
	      edges.add(result);
	      String doctor = result.getDoctor();
	      String referred_doctor = result.getReferredDoctor();
	      String referral_count = result.getReferralCount();
	      String direction = result.getDirection();
	      String rev_referral_count = result.getReverseCount();
	      sb.append("<edge id=\"" + Math.abs((doctor + referred_doctor).hashCode()) + "\"");
	      sb.append(" head_node_id=\"");
	      sb.append(referred_doctor);
	      sb.append("\" tail_node_id=\"");
	      sb.append(doctor);
	      if("\"referred\"".equalsIgnoreCase(direction))
	      {
	    	  sb.append("\" tooltip=\"provided " + referral_count + " referrals\" edge_line_color=\"#2262A0\"/>\n");
	      }
	      else if("\"was referred by\"".equalsIgnoreCase(direction))
	      {
	    	  sb.append("\" tooltip=\"received " + referral_count + " referrals\" edge_line_color=\"#DA6315\"/>\n");
	      }
	      else
	      {
	    	  String tooltip = "provided " + referral_count + " referrals\\n" + "received " + rev_referral_count + " referrals";
	    	  sb.append("\" tooltip=\"" + tooltip + "\" edge_line_color=\"#FF0000\"/>\n"); 
	      }
	      
	    }
		sb.append("</edges>\n");
		return sb;
	}

}
