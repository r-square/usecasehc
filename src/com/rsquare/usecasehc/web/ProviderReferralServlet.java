package com.rsquare.usecasehc.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.rsquare.usecasehc.model.ProviderReferralResult;
import com.rsquare.usecasehc.util.ProviderReferralHelper;

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
	
	private static Logger logger = Logger.getLogger(ProviderReferralServlet.class);
	
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
		String format = request.getParameter("format");
		int fileNum = Integer.parseInt(request.getParameter("params"), 2);
		PrintWriter out = response.getWriter();
		if(pid==null)
		{
			out.println("Missing the query parameters...");
			return;
		}
		Dataset dataset = TDBFactory.createDataset(directory);
		Model model = dataset.getDefaultModel();
		String sql1 = FileManager.get().readWholeFileAsUTF8(queryFilePath + "/query_" + (fileNum==0 ? 1 : fileNum) + ".txt");
		
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
		   
//		   Iterator<ProviderReferralResult> iterator = results.iterator();
//		   Map<String, Provider> providers = new HashMap<String, Provider>();
//		   Provider p = new Provider(tempId, tempId, null, null, null, null, null);
//		   providers.put(tempId, p);
//		   while(iterator.hasNext())
//		   {
//			   ProviderReferralResult pr = iterator.next();
//			   p = new Provider(pr.getReferredDoctor(), pr.getReferredDoctor(), null, null, null, null, null);
//			   providers.put(pr.getReferredDoctor(), p);
//			}
		   
//		   HiveClient hc = new HiveClient();
//		   t1 = System.currentTimeMillis();
//		   Map<String, Provider> providers = hc.getProvidersByReferrals(results, tempId);
//		   t2 = System.currentTimeMillis();
//		   logger.info("getProviders() took time(mSec): " + (t2-t1));
		   
		   if("json".equalsIgnoreCase(format))
		   {
		   	out.println(ProviderReferralHelper.getUIGraphResultOutputJSON(results, tempId));
		   }
		   else
		   {
		   	out.println(ProviderReferralHelper.getUIGraphResultOutputXML(results, tempId));
		   }
		   
		}
//		catch(SQLException exception)
//		{
//			logger.error(exception);
//			Iterator<ProviderReferralResult> iterator = results.iterator();
//			Map<String, Provider> providers = new HashMap<String, Provider>();
//			Provider p = new Provider(tempId, tempId, null, null, null, null, null);
//			providers.put(tempId, p);
//			while(iterator.hasNext())
//			{
//				ProviderReferralResult pr = iterator.next();
//				p = new Provider(pr.getReferredDoctor(), pr.getReferredDoctor(), null, null, null, null, null);
//				providers.put(pr.getReferredDoctor(), p);
//			}
//			
//			 if("json".equalsIgnoreCase(format))
//			 {
//		    	out.println(ProviderReferralHelper.getUIGraphResultOutputJSON(results, providers, tempId));
//		     }
//		     else
//		     {
//		    	out.println(ProviderReferralHelper.getUIGraphResultOutputXML(results, providers, tempId));
//		     }
//		}
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
	
}
