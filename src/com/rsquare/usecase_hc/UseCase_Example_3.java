package com.rsquare.usecase_hc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.riot.system.PrefixMapFactory;

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

public class UseCase_Example_3 {
	
	private static String pid=":1043377500";

	public static void main(String[] args) {
		 String directory = "B:\\rsquare\\usecase_hc\\rdfout" ;
		 Dataset dataset = TDBFactory.createDataset(directory);
		 Model model = dataset.getDefaultModel();
		 
//		 String sql = FileManager.get().readWholeFileAsUTF8("file:///B:\\rsquare\\usecase_hc\\query_1.txt");
		 String sql = FileManager.get().readWholeFileAsUTF8("./queries/query_1.txt");
		 Map<String, String> prefix = new HashMap<String, String>();
		 prefix.put("", "http://demo.marklogic.com/provider/");
		 
		 PrefixMapping nsMapping = new PrefixMappingImpl();
		 nsMapping.setNsPrefixes(prefix);
		 
		 ParameterizedSparqlString pql = new ParameterizedSparqlString(sql, nsMapping);
		 
		 pql.setParam("provider", NodeFactoryExtra.parseNode(pid, PrefixMapFactory.create(nsMapping)));
		 
		 Query query = QueryFactory.create(pql.toString());
		 
//		 System.out.println(query.toString());
		 QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    System.out.println(getUIGraphResultOutput(results));
//		    System.out.println(ResultSetFormatter.asXMLString(results));
//		    ResultSetFormatter.out(System.out, results);
//		    for ( ; results.hasNext() ; )
//		    {
//		      QuerySolution soln = results.nextSolution() ;
//		      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
//		      RDFNode y = soln.get("referred_doctor");
//		      RDFNode z = soln.get("referral_count");
//		      System.out.println(x.toString() + " | " + y.toString() + " | " + z.toString());
//		      System.out.println(FmtUtils.stringForRDFNode(x) + " | " + FmtUtils.stringForRDFNode(y) + " | " + FmtUtils.stringForRDFNode(z));
//		      System.out.println(x.asNode().isURI() ? x.asNode().getNameSpace() : "not a URI");
//		      System.out.println(y.asNode().isURI() ? y.asNode().getNameSpace() : "not a URI");
//		      System.out.println(z.asNode().isLiteral() ? z.asNode().getLiteralValue() : "not a literal");
		      
//		      Resource r = soln.getResource("VarR") ; // Get a result variable - must be a resource
//		      Literal l = soln.getLiteral("VarL") ;   // Get a result variable - must be a literal
//		    }
		  } finally { qexec.close() ; }

	}
	
	private static String getUIGraphResultOutput(ResultSet results)
	{
		final String begin = "<graph_data>";
		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();
		final String end = "</graph_data>";
		ResultSetRewindable rewindableResultSet = ResultSetFactory.makeRewindable(results);
		
		nodes = getNodes(rewindableResultSet);
		
		rewindableResultSet.reset();
		
		edges = getEdges(rewindableResultSet);
		
		return (begin + "\n" + (nodes.append(edges.toString())).toString() + "\n" + end);
	}
	
	private static StringBuilder getNodes(ResultSetRewindable resultSet)
	{
		List<String> nodes = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("<nodes>\n<node id=\"" + pid + "\" label=\"" + pid + "\" depth_loaded=\"2\" tooltip=\"" + pid + "\" />\n");
		for ( ; resultSet.hasNext() ; )
	    {
	      QuerySolution soln = resultSet.nextSolution() ;
	      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
	      RDFNode y = soln.get("referred_doctor");
	      RDFNode z = soln.get("referral_count");
	      String doctor = FmtUtils.stringForRDFNode(x);
	      String referred_doctor = FmtUtils.stringForRDFNode(y);
	      String referral_count = FmtUtils.stringForRDFNode(z);
	      if(nodes.contains(referred_doctor))
	      {
	    	  System.out.println("==== Already present ======" + doctor + " | " + referred_doctor + " | " + referral_count);
	    	  continue;
	      }
	      nodes.add(referred_doctor);
	      sb.append("<node id=\"");
	      sb.append(referred_doctor);
	      sb.append("\" label=\"");
	      sb.append(referred_doctor);
	      sb.append("\" depth_loaded=\"1\" tooltip=\"");
	      sb.append(referred_doctor);
	      sb.append("\" graphic_size=\"50\" selected_graphic_size=\"50\" />\n");
	      
//	      System.out.println(doctor + " | " + referred_doctor + " | " + referral_count);
	    }
		sb.append("</nodes>\n");
		return sb;
	}
	
	private static StringBuilder getEdges(ResultSetRewindable resultSet)
	{
		List<String> edges = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("<edges>\n");
		for ( ; resultSet.hasNext() ; )
	    {
	      QuerySolution soln = resultSet.nextSolution() ;
	      RDFNode x = soln.get("doctor") ;       // Get a result variable by name.
	      RDFNode y = soln.get("referred_doctor");
	      RDFNode z = soln.get("referral_count");
	      String doctor = FmtUtils.stringForRDFNode(x);
	      String referred_doctor = FmtUtils.stringForRDFNode(y);
	      String referral_count = FmtUtils.stringForRDFNode(z);
	      if(edges.contains(referred_doctor))
	      {
	    	  System.out.println("==== Already present ======" + doctor + " | " + referred_doctor + " | " + referral_count);
	    	  continue;
	      }
	      edges.add(referred_doctor);
	      sb.append("<edge id=\"" + Math.abs(referred_doctor.hashCode()) + "\"");
	      sb.append(" head_node_id=\"");
	      sb.append(referred_doctor);
	      sb.append("\" tail_node_id=\"");
	      sb.append(doctor);
	      sb.append("\" tooltip=\"provided " + referral_count + " referrals\" edge_line_color=\"#2262A0\"/>\n");
	      
//	      System.out.println(doctor + " | " + referred_doctor + " | " + referral_count);
	    }
		sb.append("</edges>\n");
		return sb;
	}

}