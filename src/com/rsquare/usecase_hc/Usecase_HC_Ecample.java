package com.rsquare.usecase_hc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.sparql.util.FmtUtils;
import com.hp.hpl.jena.sparql.util.NodeFactoryExtra;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * Servlet implementation class Usecase_HC_Ecample
 */
@WebServlet(urlPatterns={"/usecase_hc"}, 
		initParams={ @WebInitParam(name="winPath",value="B:\\rsquare\\usecase_hc\\rdfout"), 
		@WebInitParam(name="linuxPath",value="/srv/usecase_hc/rdfout"),
		@WebInitParam(name="queryFile",value="./queries") }
)
public class Usecase_HC_Ecample extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private String directory;
	private String queryFilePath;
	
	private Logger logger = Logger.getLogger(Usecase_HC_Ecample.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		directory = config.getInitParameter("winPath");
		File f = new File(directory);
		if(!f.exists())
		{
			//Try the linux path env
			directory = config.getInitParameter("linuxPath");
			f = new File(directory);
			if(f.exists())
			{
				logger.error("DB Path not found: " + directory);
			}
			else
			{
				throw new ServletException("Failed to find the DB directory");
			}
		}
		
		queryFilePath = config.getInitParameter("queryFile");
		
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
//		String directory = "B:\\rsquare\\usecase_hc\\rdfout" ;
//		String directory = "/srv/usecase_hc/rdfout";
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
		
		String sql1 = FileManager.get().readWholeFileAsUTF8(queryFilePath + "/query_1.txt");
		String sql2 = FileManager.get().readWholeFileAsUTF8(queryFilePath + "/query_2.txt");
		Map<String, String> prefix = new HashMap<String, String>();
		prefix.put("", "http://demo.marklogic.com/provider/");
		 
		PrefixMapping nsMapping = new PrefixMappingImpl();
		nsMapping.setNsPrefixes(prefix);
		 
		ParameterizedSparqlString pql1 = new ParameterizedSparqlString(sql1, nsMapping);
		ParameterizedSparqlString pql2 = new ParameterizedSparqlString(sql2, nsMapping);
		 
		pql1.setParam("provider", NodeFactoryExtra.parseNode(pid, PrefixMapFactory.create(nsMapping)));
		pql2.setParam("provider", NodeFactoryExtra.parseNode(pid, PrefixMapFactory.create(nsMapping)));
		 
		Query query1 = QueryFactory.create(pql1.toString());
		Query query2 = QueryFactory.create(pql2.toString());
		logger.info("Query1: " + query1.toString());
		logger.info("Query2: " + query2.toString());
		QueryExecution qexec1 = QueryExecutionFactory.create(query1, model) ;
		QueryExecution qexec2 = QueryExecutionFactory.create(query2, model) ;
		try {
		   long t1 = System.currentTimeMillis();
		   ResultSet results1 = qexec1.execSelect() ;
		   long t2 = System.currentTimeMillis();
		   logger.info("Query 1 took time(mSec): " + (t2-t1));
		   ResultSetRewindable rewindableResultSet1 = ResultSetFactory.makeRewindable(results1);
		   
		   t1 = System.currentTimeMillis();
		   ResultSet results2 = qexec2.execSelect() ;
		   t2 = System.currentTimeMillis();
		   logger.info("Query 2 took time(mSec): " + (t2-t1));
		   ResultSetRewindable rewindableResultSet2 = ResultSetFactory.makeRewindable(results2);
		   
		   out.println(getUIGraphResultOutput(rewindableResultSet1, rewindableResultSet2, pid));
		} finally { qexec1.close() ; qexec2.close();}
	}
	
	private String getUIGraphResultOutput(ResultSetRewindable results1, ResultSetRewindable results2, String pid)
	{
		final String begin = "<graph_data>";
		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();
		final String end = "</graph_data>";
		
		nodes = getNodes(results1, results2, pid);
		
		results1.reset();
		results2.reset();
		
		edges = getEdges(results1, results2);
		
		return (begin + "\n" + (nodes.append(edges.toString())).toString() + "\n" + end);
	}
	
	private StringBuilder getNodes(ResultSetRewindable resultSet1, ResultSetRewindable resultSet2, String pid)
	{
		List<String> nodes = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("<nodes>\n<node id=\"" + pid.replace(":", "") + "\" label=\"" + pid.replace(":", "") + "\" depth_loaded=\"2\" tooltip=\"" + 
				pid.replace(":", "") + "\" label_font_family=\"Impact, Charcoal, sans-serif\" selected_graphic_fill_color=\"#CC0000\" />\n");
		for ( ; resultSet1.hasNext() ; )
	    {
	      QuerySolution soln = resultSet1.nextSolution() ;
//	      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
	      RDFNode y = soln.get("referred_doctor");
//	      RDFNode z = soln.get("referral_count");
//	      String doctor = FmtUtils.stringForRDFNode(x).replace(":", "");
	      String referred_doctor = FmtUtils.stringForRDFNode(y).replace(":", "");
//	      String referral_count = FmtUtils.stringForRDFNode(z);
	      if(nodes.contains(referred_doctor))
	      {
	    	  logger.info("==== Node Already present ======" + referred_doctor);
	    	  continue;
	      }
	      nodes.add(referred_doctor);
	      sb.append("<node id=\"");
	      sb.append(referred_doctor);
	      sb.append("\" label=\"");
	      sb.append(referred_doctor);
	      sb.append("\" depth_loaded=\"1\" tooltip=\"");
	      sb.append(referred_doctor);
	      sb.append("\" graphic_type=\"image\" graphic_image_url=\"images/doctor_icon.png\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
	      
	    }
		
		for ( ; resultSet2.hasNext() ; )
	    {
	      QuerySolution soln = resultSet2.nextSolution() ;
//	      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
	      RDFNode y = soln.get("referring_doctor");
//	      RDFNode z = soln.get("referral_count");
//	      String doctor = FmtUtils.stringForRDFNode(x).replace(":", "");
	      String referring_doctor = FmtUtils.stringForRDFNode(y).replace(":", "");
//	      String referral_count = FmtUtils.stringForRDFNode(z);
	      if(nodes.contains(referring_doctor))
	      {
	    	  logger.info("==== Node Already present ======" + referring_doctor);
	    	  continue;
	      }
	      nodes.add(referring_doctor);
	      sb.append("<node id=\"");
	      sb.append(referring_doctor);
	      sb.append("\" label=\"");
	      sb.append(referring_doctor);
	      sb.append("\" depth_loaded=\"1\" tooltip=\"");
	      sb.append(referring_doctor);
	      sb.append("\" graphic_type=\"image\" graphic_image_url=\"images/doctor_icon.png\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
	      
	    }
		
		sb.append("</nodes>\n");
		return sb;
	}
	
	private StringBuilder getEdges(ResultSetRewindable resultSet1, ResultSetRewindable resultSet2)
	{
		List<String> edges = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("<edges>\n");
		for ( ; resultSet1.hasNext() ; )
	    {
	      QuerySolution soln = resultSet1.nextSolution() ;
	      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
	      RDFNode y = soln.get("referred_doctor");
	      RDFNode z = soln.get("referral_count");
	      String doctor = FmtUtils.stringForRDFNode(x).replace(":", "");
	      String referred_doctor = FmtUtils.stringForRDFNode(y).replace(":", "");
	      String referral_count = FmtUtils.stringForRDFNode(z);
	      if(edges.contains(referred_doctor))
	      {
	    	  logger.info("==== Already present ======" + doctor + " | " + referred_doctor + " | " + referral_count);
	    	  continue;
	      }
	      edges.add(referred_doctor);
	      sb.append("<edge id=\"" + Math.abs((doctor + referred_doctor).hashCode()) + "\"");
	      sb.append(" head_node_id=\"");
	      sb.append(referred_doctor);
	      sb.append("\" tail_node_id=\"");
	      sb.append(doctor);
	      sb.append("\" tooltip=\"provided " + referral_count + " referrals\" edge_line_color=\"#2262A0\"/>\n");
	      
	    }
		
		//clear edges since I think it can be in reverse direction as well.
//		edges.clear();
		
		for ( ; resultSet2.hasNext() ; )
	    {
	      QuerySolution soln = resultSet2.nextSolution() ;
	      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
	      RDFNode y = soln.get("referring_doctor");
	      RDFNode z = soln.get("referral_count");
	      String doctor = FmtUtils.stringForRDFNode(x).replace(":", "");
	      String referring_doctor = FmtUtils.stringForRDFNode(y).replace(":", "");
	      String referral_count = FmtUtils.stringForRDFNode(z);
	      if(edges.contains(referring_doctor))
	      {
	    	  logger.info("==== Already present ======" + referring_doctor + " | " + doctor + " | " + referral_count);
	    	  continue;
	      }
	      edges.add(referring_doctor);
	      sb.append("<edge id=\"" + Math.abs((referring_doctor + doctor).hashCode()) + "\"");
	      sb.append(" head_node_id=\"");
	      sb.append(doctor);
	      sb.append("\" tail_node_id=\"");
	      sb.append(referring_doctor);
	      sb.append("\" tooltip=\"received " + referral_count + " referrals\" edge_line_color=\"#DA6315\"/>\n");
	      
	    }
		
		sb.append("</edges>\n");
		return sb;
	}

}
