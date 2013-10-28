package com.rsquare.usecasehc.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.sparql.util.FmtUtils;
import com.hp.hpl.jena.sparql.util.NodeFactoryExtra;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.rsquare.usecasehc.hive.HiveClient;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderReferralResult;

/**
 * Servlet implementation class Usecase_HC_Ecample
 */
@WebServlet(urlPatterns={"/usecase_hc"}, 
		initParams={ @WebInitParam(name="prop",value="./config/dev.properties") }
)
public class ProviderReferralServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private String directory;
	private String queryFilePath;
	private Properties props = new Properties();
	
	private Logger logger = Logger.getLogger(ProviderReferralServlet.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		//load a properties file
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(config.getInitParameter("prop")));
		} catch (FileNotFoundException e) {
			logger.debug(e);
		} catch (IOException e) {
			logger.debug(e);
		}
		
		directory = props.getProperty("winPath");
		File f = new File(directory);
		if(!f.exists())
		{
			//Try the linux path env
			directory = props.getProperty("linuxPath");
			f = new File(directory);
			if(!f.exists())
			{
				logger.error("DB Path not found: " + directory);
				throw new ServletException("Failed to find the DB directory");
			}
		}
		queryFilePath = props.getProperty("queryFilePath");
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(directory==null)
		{
			return;
		}
		logger.info("Querying DB Path: " + directory);
		String pid = (request.getParameter("pid"));
		PrintWriter out = response.getWriter();
		if(pid==null)
		{
			out.println("Missing the query parameters...");
			return;
		}
		Dataset dataset = TDBFactory.createDataset(directory);
		Model model = dataset.getDefaultModel();
		
		String sql1 = FileManager.get().readWholeFileAsUTF8(queryFilePath + "/query_3.txt");
//		String sql2 = FileManager.get().readWholeFileAsUTF8(queryFilePath + "/query_2.txt");
		Map<String, String> prefix = new HashMap<String, String>();
		prefix.put("", "http://demo.marklogic.com/provider/");
		 
		PrefixMapping nsMapping = new PrefixMappingImpl();
		nsMapping.setNsPrefixes(prefix);
		 
		ParameterizedSparqlString pql1 = new ParameterizedSparqlString(sql1, nsMapping);
//		ParameterizedSparqlString pql2 = new ParameterizedSparqlString(sql2, nsMapping);
		 
		pql1.setParam("provider", NodeFactoryExtra.parseNode(pid, PrefixMapFactory.create(nsMapping)));
//		pql2.setParam("provider", NodeFactoryExtra.parseNode(pid, PrefixMapFactory.create(nsMapping)));
		 
		Query query1 = QueryFactory.create(pql1.toString());
//		Query query2 = QueryFactory.create(pql2.toString());
		logger.info("Query1: " + query1.toString());
//		logger.info("Query2: " + query2.toString());
		QueryExecution qexec1 = QueryExecutionFactory.create(query1, model) ;
//		QueryExecution qexec2 = QueryExecutionFactory.create(query2, model) ;
		List<ProviderReferralResult> results = new ArrayList<ProviderReferralResult>();
		String tempId = pid.replace(":", "");
		try {
		   long t1 = System.currentTimeMillis();
		   ResultSet results1 = qexec1.execSelect() ;
		   long t2 = System.currentTimeMillis();
		   logger.info("Query 1 took time(mSec): " + (t2-t1));
		   
//		   out.println(getUIGraphResultOutput(results1, pid));
		   
		   t1 = System.currentTimeMillis();
		   results = getResultCollection(results1);
		   t2 = System.currentTimeMillis();
		   logger.info("getResultCollection() took time(mSec): " + (t2-t1));
		   
		   HiveClient hc = new HiveClient();
		   t1 = System.currentTimeMillis();
		   Map<String, Provider> providers = hc.getProvidersByReferrals(results, tempId);
		   t2 = System.currentTimeMillis();
		   logger.info("getProviders() took time(mSec): " + (t2-t1));
		   
		   out.println(getUIGraphResultOutput(results, providers, tempId));
		}
		catch(SQLException exception)
		{
			logger.error(exception);
//			out.println("<graph_data><nodes /><edges /></graph_data>");
//			out.println("exception - please check logs. Making graph data with IDs only");
			Iterator<ProviderReferralResult> iterator = results.iterator();
			Map<String, Provider> providers = new HashMap<String, Provider>();
			Provider p = new Provider(tempId, tempId, null, null, null, null, null);
			providers.put(tempId, p);
			while(iterator.hasNext())
			{
				ProviderReferralResult pr = iterator.next();
				p = new Provider(pr.getReferredDoctor(), pr.getReferredDoctor(), null, null, null, null, null);
				providers.put(pr.getReferredDoctor(), p);
			}
			out.println(getUIGraphResultOutput(results, providers, tempId));
		}
		finally { 
			qexec1.close() ;
		}
	}
	
	private List<ProviderReferralResult> getResultCollection(ResultSet results1)
	{
		List<ProviderReferralResult> results = new ArrayList<ProviderReferralResult>();
		for ( ; results1.hasNext() ; )
	    {
	      QuerySolution soln = results1.nextSolution() ;
	      RDFNode x = soln.get("doctor") ;
	      RDFNode y = soln.get("referred_doctor");
	      RDFNode z = soln.get("referral_count");
	      RDFNode d = soln.get("direction");
	      RDFNode c = soln.get("reverse_count");
	      String doctor = FmtUtils.stringForRDFNode(x).replace(":", "");
	      String referred_doctor = FmtUtils.stringForRDFNode(y).replace(":", "");
	      String referral_count = FmtUtils.stringForRDFNode(z);
	      String direction = FmtUtils.stringForRDFNode(d);
	      String rev_referral_count = FmtUtils.stringForRDFNode(c);
	      ProviderReferralResult result = new ProviderReferralResult(doctor, referred_doctor, referral_count, direction, rev_referral_count);
	      results.add(result);
	    }
		return results;
	}
	
	private String getUIGraphResultOutput(List<ProviderReferralResult> results, Map<String, Provider> providers, String pid)
	{
		final String begin = "<graph_data>";
		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();
		final String end = "</graph_data>";
		List<ProviderReferralResult> rNodes = new ArrayList<ProviderReferralResult>();
		Iterator<ProviderReferralResult> iterator = results.iterator();
		Provider p = providers.get(pid);
		String name = (p.getName()==null || "".equals(p.getName()) || " ".equals(p.getName())) ? p.getOrganization() : (p.getName());
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
	      name = (p.getName()==null || "".equals(p.getName()) || " ".equals(p.getName())) ? p.getOrganization() : (p.getName());
	      nodes.append("<node id=\"");
	      nodes.append(result.getReferredDoctor());
	      nodes.append("\" label=\"");
	      nodes.append(name);
	      nodes.append("\\n" + p.getGeneral_area() + " - " + p.getSpecialty());
	      nodes.append("\" depth_loaded=\"1\" tooltip=\"");
	      nodes.append(name);
	      nodes.append("\" graphic_type=\"image\" graphic_image_url=\"images/doctor_icon.png\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
	      
	      
	      edges.append("<edge id=\"" + Math.abs((result.getDoctor() + result.getReferredDoctor()).hashCode()) + "\"");
	      if("\"referred\"".equalsIgnoreCase(result.getDirection()))
	      {
	    	  edges.append(" head_node_id=\"");
		      edges.append(result.getReferredDoctor());
		      edges.append("\" tail_node_id=\"");
		      edges.append(result.getDoctor());
	    	  edges.append("\" tooltip=\"provided " + result.getReferralCount() + " referrals\" edge_line_color=\"#2262A0\"/>\n");
	      }
	      else if("\"was referred by\"".equalsIgnoreCase(result.getDirection()))
	      {
	    	  edges.append(" head_node_id=\"");
		      edges.append(result.getDoctor());
		      edges.append("\" tail_node_id=\"");
		      edges.append(result.getReferredDoctor());
	    	  edges.append("\" tooltip=\"received " + result.getReferralCount() + " referrals\" edge_line_color=\"#DA6315\"/>\n");
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
	
//	private String getUIGraphResultOutput(ResultSet results1, String pid)
//	{
//		final String begin = "<graph_data>";
//		StringBuilder nodes = new StringBuilder();
//		StringBuilder edges = new StringBuilder();
//		final String end = "</graph_data>";
//		List<ProviderReferralResult> rNodes = new ArrayList<ProviderReferralResult>();
//		nodes.append("<nodes>\n<node id=\"" + pid.replace(":", "") + "\" label=\"" + pid.replace(":", "") + "\" depth_loaded=\"2\" tooltip=\"" + 
//				pid.replace(":", "") + "\" label_font_family=\"Impact, Charcoal, sans-serif\" selected_graphic_fill_color=\"#CC0000\" />\n");
//		edges.append("<edges>\n");
//		for ( ; results1.hasNext() ; )
//	    {
//		  QuerySolution soln = results1.nextSolution() ;
//	      RDFNode x = soln.get("doctor") ;
//	      RDFNode y = soln.get("referred_doctor");
//	      RDFNode z = soln.get("referral_count");
//	      RDFNode d = soln.get("direction");
//	      RDFNode c = soln.get("reverse_count");
//	      String doctor = FmtUtils.stringForRDFNode(x).replace(":", "");
//	      String referred_doctor = FmtUtils.stringForRDFNode(y).replace(":", "");
//	      String referral_count = FmtUtils.stringForRDFNode(z);
//	      String direction = FmtUtils.stringForRDFNode(d);
//	      String rev_referral_count = FmtUtils.stringForRDFNode(c);
//	      ProviderReferralResult result = new ProviderReferralResult(doctor, referred_doctor, referral_count, direction, rev_referral_count);
//	      if(rNodes.contains(result))
//	      {
//	    	  logger.info("==== Node Already present ======" + result);
//	    	  continue;
//	      }
//	      rNodes.add(result);
//	      
//	      nodes.append("<node id=\"");
//	      nodes.append(referred_doctor);
//	      nodes.append("\" label=\"");
//	      nodes.append(referred_doctor);
//	      nodes.append("\" depth_loaded=\"1\" tooltip=\"");
//	      nodes.append(referred_doctor);
//	      nodes.append("\" graphic_type=\"image\" graphic_image_url=\"images/doctor_icon.png\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
//	      
//	      
//	      edges.append("<edge id=\"" + Math.abs((doctor + referred_doctor).hashCode()) + "\"");
//	      edges.append(" head_node_id=\"");
//	      edges.append(referred_doctor);
//	      edges.append("\" tail_node_id=\"");
//	      edges.append(doctor);
//	      if("\"referred\"".equalsIgnoreCase(direction))
//	      {
//	    	  edges.append("\" tooltip=\"provided " + referral_count + " referrals\" edge_line_color=\"#2262A0\"/>\n");
//	      }
//	      else if("\"was referred by\"".equalsIgnoreCase(direction))
//	      {
//	    	  edges.append("\" tooltip=\"received " + referral_count + " referrals\" edge_line_color=\"#DA6315\"/>\n");
//	      }
//	      else
//	      {
//	    	  String tooltip = "provided " + referral_count + " referrals\\n" + "received " + rev_referral_count + " referrals";
//	    	  edges.append("\" tooltip=\"" + tooltip + "\" edge_line_color=\"#FF0000\"/>\n"); 
//	      }
//	    }
//		
//		nodes.append("</nodes>\n");
//		edges.append("</edges>\n");
//		
//		return (begin + "\n" + (nodes.append(edges.toString())).toString() + "\n" + end);
//	}
	
//	private String getUIGraphResultOutput(List<ProviderReferralResult> results, String pid)
//	{
//		final String begin = "<graph_data>";
//		StringBuilder nodes = new StringBuilder();
//		StringBuilder edges = new StringBuilder();
//		final String end = "</graph_data>";
//		
//		long t1 = System.currentTimeMillis();
//		nodes = getNodes(results, pid);
//	    long t2 = System.currentTimeMillis();
//	    logger.info("getNodes() took time(mSec): " + (t2-t1));
//		
//		t1 = System.currentTimeMillis();
//		edges = getEdges(results);
//	    t2 = System.currentTimeMillis();
//	    logger.info("getEdges() took time(mSec): " + (t2-t1));
//		
//		return (begin + "\n" + (nodes.append(edges.toString())).toString() + "\n" + end);
//	}
	
//	private StringBuilder getNodes(List<ProviderReferralResult> results, String pid)
//	{
//		List<ProviderReferralResult> nodes = new ArrayList<ProviderReferralResult>();
//		StringBuilder sb = new StringBuilder();
//		sb.append("<nodes>\n<node id=\"" + pid.replace(":", "") + "\" label=\"" + pid.replace(":", "") + "\" depth_loaded=\"2\" tooltip=\"" + 
//				pid.replace(":", "") + "\" label_font_family=\"Impact, Charcoal, sans-serif\" selected_graphic_fill_color=\"#CC0000\" />\n");
//		Iterator<ProviderReferralResult> iterator = results.iterator();
//		for ( ; iterator.hasNext() ; )
//	    {
//	      ProviderReferralResult result = iterator.next();
//	      if(nodes.contains(result))
//	      {
//	    	  logger.info("==== Node Already present ======" + result);
//	    	  continue;
//	      }
//	      nodes.add(result);
//	      String referred_doctor = FmtUtils.stringForRDFNode(result.getReferredDoctor()).replace(":", "");
//	      sb.append("<node id=\"");
//	      sb.append(referred_doctor);
//	      sb.append("\" label=\"");
//	      sb.append(referred_doctor);
//	      sb.append("\" depth_loaded=\"1\" tooltip=\"");
//	      sb.append(referred_doctor);
//	      sb.append("\" graphic_type=\"image\" graphic_image_url=\"images/doctor_icon.png\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
//	      
//	    }
//		
////		for ( ; resultSet2.hasNext() ; )
////	    {
////	      QuerySolution soln = resultSet2.nextSolution() ;
//////	      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
////	      RDFNode y = soln.get("referring_doctor");
//////	      RDFNode z = soln.get("referral_count");
//////	      String doctor = FmtUtils.stringForRDFNode(x).replace(":", "");
////	      String referring_doctor = FmtUtils.stringForRDFNode(y).replace(":", "");
//////	      String referral_count = FmtUtils.stringForRDFNode(z);
////	      if(nodes.contains(referring_doctor))
////	      {
////	    	  logger.info("==== Node Already present ======" + referring_doctor);
////	    	  continue;
////	      }
////	      nodes.add(referring_doctor);
////	      sb.append("<node id=\"");
////	      sb.append(referring_doctor);
////	      sb.append("\" label=\"");
////	      sb.append(referring_doctor);
////	      sb.append("\" depth_loaded=\"1\" tooltip=\"");
////	      sb.append(referring_doctor);
////	      sb.append("\" graphic_type=\"image\" graphic_image_url=\"images/doctor_icon.png\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
////	      
////	    }
//		
//		sb.append("</nodes>\n");
//		return sb;
//	}
	
//	private StringBuilder getEdges(List<ProviderReferralResult> results)
//	{
//		List<ProviderReferralResult> edges = new ArrayList<ProviderReferralResult>();
//		StringBuilder sb = new StringBuilder();
//		sb.append("<edges>\n");
//		Iterator<ProviderReferralResult> iterator = results.iterator();
//		for ( ; iterator.hasNext() ; )
//	    {
//	      ProviderReferralResult result = iterator.next();
//	      if(edges.contains(result))
//	      {
//	    	  logger.info("==== Already present ======" + result);
//	    	  continue;
//	      }
//	      edges.add(result);
//	      String doctor = FmtUtils.stringForRDFNode(result.getDoctor()).replace(":", "");
//	      String referred_doctor = FmtUtils.stringForRDFNode(result.getReferredDoctor()).replace(":", "");
//	      String referral_count = FmtUtils.stringForRDFNode(result.getReferralCount());
//	      String direction = FmtUtils.stringForRDFNode(result.getDirection());
//	      String rev_referral_count = FmtUtils.stringForRDFNode(result.getReverseCount());
//	      sb.append("<edge id=\"" + Math.abs((doctor + referred_doctor).hashCode()) + "\"");
//	      sb.append(" head_node_id=\"");
//	      sb.append(referred_doctor);
//	      sb.append("\" tail_node_id=\"");
//	      sb.append(doctor);
//	      if("\"referred\"".equalsIgnoreCase(direction))
//	      {
//	    	  sb.append("\" tooltip=\"provided " + referral_count + " referrals\" edge_line_color=\"#2262A0\"/>\n");
//	      }
//	      else if("\"was referred by\"".equalsIgnoreCase(direction))
//	      {
//	    	  sb.append("\" tooltip=\"received " + referral_count + " referrals\" edge_line_color=\"#DA6315\"/>\n");
//	      }
//	      else
//	      {
//	    	  String tooltip = "provided " + referral_count + " referrals\\n" + "received " + rev_referral_count + " referrals";
//	    	  sb.append("\" tooltip=\"" + tooltip + "\" edge_line_color=\"#FF0000\"/>\n"); 
//	      }
//	      
//	    }
//		
//		//clear edges since I think it can be in reverse direction as well.
////		edges.clear();
////		
////		for ( ; resultSet2.hasNext() ; )
////	    {
////	      QuerySolution soln = resultSet2.nextSolution() ;
////	      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
////	      RDFNode y = soln.get("referring_doctor");
////	      RDFNode z = soln.get("referral_count");
////	      String doctor = FmtUtils.stringForRDFNode(x).replace(":", "");
////	      String referring_doctor = FmtUtils.stringForRDFNode(y).replace(":", "");
////	      String referral_count = FmtUtils.stringForRDFNode(z);
////	      if(edges.contains(referring_doctor))
////	      {
////	    	  logger.info("==== Already present ======" + referring_doctor + " | " + doctor + " | " + referral_count);
////	    	  continue;
////	      }
////	      edges.add(referring_doctor);
////	      sb.append("<edge id=\"" + Math.abs((referring_doctor + doctor).hashCode()) + "\"");
////	      sb.append(" head_node_id=\"");
////	      sb.append(doctor);
////	      sb.append("\" tail_node_id=\"");
////	      sb.append(referring_doctor);
////	      sb.append("\" tooltip=\"received " + referral_count + " referrals\" edge_line_color=\"#DA6315\"/>\n");
////	      
////	    }
//		
//		sb.append("</edges>\n");
//		return sb;
//	}

}
