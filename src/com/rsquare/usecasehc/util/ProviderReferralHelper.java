package com.rsquare.usecasehc.util;

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
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderReferralResult;

public class ProviderReferralHelper {
	
	private static Logger logger = Logger.getLogger(ProviderReferralHelper.class);
	
	public static String getUIGraphResultOutputJSON(List<ProviderReferralResult> results, Map<String, Provider> providers, String pid)
	{
		List<ProviderGraphNode> nodes = new ArrayList<ProviderGraphNode>();
		List<ProviderGraphEdge> edges = new ArrayList<ProviderGraphEdge>();
		List<ProviderReferralResult> rNodes = new ArrayList<ProviderReferralResult>();
		Iterator<ProviderReferralResult> iterator = results.iterator();
		Provider p = providers.get(pid);
		ProviderGraphNodeData nData = new ProviderGraphNodeData((p.getName() + "\\n" + p.getGeneral_area() + "-" + p.getSpecialty()), 
				"2", p.getName(), "Impact, Charcoal, sans-serif", "#CC0000", "", "", "", "");
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
	        p = providers.get(result.getReferredDoctor());
	        nData = new ProviderGraphNodeData((p.getName() + "\\n" + p.getGeneral_area() + "-" + p.getSpecialty()), 
					"1", p.getName(), "", "", "image", "images/doctor_icon.png", "50", "50");
	        node = new ProviderGraphNode(result.getReferredDoctor(), nData);
	        nodes.add(node);
	        
	        if("\"referred\"".equalsIgnoreCase(result.getDirection()) || "referred".equalsIgnoreCase(result.getDirection()))
		    {
	        	ProviderGraphEdgeData eData = new ProviderGraphEdgeData("provided " + result.getReferralCount() + " referrals", 
	        			"#2262A0", "false");
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((result.getDoctor() + result.getReferredDoctor()).hashCode())), 
	        			result.getReferredDoctor(), result.getDoctor(), eData);
	        	edges.add(edge);
		    }
		    else if("\"was referred by\"".equalsIgnoreCase(result.getDirection()) || "was referred by".equalsIgnoreCase(result.getDirection()))
		    {
		    	ProviderGraphEdgeData eData = new ProviderGraphEdgeData("received " + result.getReverseCount() + " referrals", 
	        			"#DA6315", "false");
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((result.getDoctor() + result.getReferredDoctor()).hashCode())), 
	        			result.getDoctor(), result.getReferredDoctor(), eData);
	        	edges.add(edge);
		    }
		    else
		    {
		    	String tooltip = "provided " + result.getReferralCount() + " referrals\\n" + "received " + result.getReverseCount() + " referrals";
		    	ProviderGraphEdgeData eData = new ProviderGraphEdgeData(tooltip,"#FF0000", "true");
	        	ProviderGraphEdge edge = new ProviderGraphEdge(String.valueOf(Math.abs((result.getDoctor() + result.getReferredDoctor()).hashCode())), 
	        			result.getReferredDoctor(), result.getDoctor(), eData);
	        	edges.add(edge);
		    }
	    }
		
		ProviderGraph graph = new ProviderGraph(nodes, edges);
		Gson gson = new Gson();
		return gson.toJson(graph);
	}
	
	public static String getUIGraphResultOutputXML(List<ProviderReferralResult> results, Map<String, Provider> providers, String pid)
	{
		final String begin = "<graph_data>";
		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();
		final String end = "</graph_data>";
		List<ProviderReferralResult> rNodes = new ArrayList<ProviderReferralResult>();
		Iterator<ProviderReferralResult> iterator = results.iterator();
		Provider p = providers.get(pid);
		String name = p.getName();
		nodes.append("<nodes>\n<node id=\"" + pid + "\" label=\"" + name + "\\n" + p.getGeneral_area() + " - " + p.getSpecialty() + "\" depth_loaded=\"2\" tooltip=\"" + 
				name + "\" label_font_family=\"Impact, Charcoal, sans-serif\" selected_graphic_fill_color=\"#CC0000\" />\n");
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
	      
	      p = providers.get(result.getReferredDoctor());
	      name = p.getName();
	      nodes.append("<node id=\"");
	      nodes.append(result.getReferredDoctor());
	      nodes.append("\" label=\"");
	      nodes.append(name);
	      nodes.append("\\n" + p.getGeneral_area() + " - " + p.getSpecialty());
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
