package com.rsquare.usecasehc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rsquare.usecasehc.model.ProviderReferralResult;
import com.rsquare.usecasehc.neo4j.Neo4jClient;
import com.rsquare.usecasehc.util.ProviderReferralHelper;

/**
 * Servlet implementation class ProviderReferralNeo4jServlet
 */
@WebServlet("/referral")
public class ProviderReferralNeo4jServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ProviderReferralNeo4jServlet.class);

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
		String pid = (request.getParameter("pid"));
		int option = Integer.parseInt(request.getParameter("params"), 2);
		String format = request.getParameter("format");
		String limit = request.getParameter("limit");
		PrintWriter out = response.getWriter();
		List<ProviderReferralResult> results = new ArrayList<ProviderReferralResult>();
		if(pid==null)
		{
			out.println("Missing the query parameters...");
			return;
		}
		Neo4jClient client = new Neo4jClient();
		try {
			results = client.getReferralsByProvider(pid, option, limit);
			
//			Iterator<ProviderReferralResult> iterator = results.iterator();
//			Map<String, Provider> providers = new HashMap<String, Provider>();
//			Provider p = new Provider(pid, pid, null, null, null, null, null);
//			providers.put(pid, p);
//			while(iterator.hasNext())
//			{
//				ProviderReferralResult pr = iterator.next();
//				p = new Provider(pr.getReferredDoctor(), pr.getReferredDoctor(), null, null, null, null, null);
//				providers.put(pr.getReferredDoctor(), p);
//			}
			
//			HiveClient hc = new HiveClient();
//			long t1 = System.currentTimeMillis();
//		    Map<String, Provider> providers = hc.getProvidersByReferrals(results, pid);
//		    long t2 = System.currentTimeMillis();
//		    logger.info("getProviders() took time(mSec): " + (t2-t1));
		   
		    if("json".equalsIgnoreCase(format))
		    {
		    	out.println(ProviderReferralHelper.getUIGraphResultOutputJSON(results, pid));
		    }
		    else
		    {
		    	out.println(ProviderReferralHelper.getUIGraphResultOutputXML(results, pid));
		    }
		} 
		catch(SQLException exception)
		{
			logger.error(exception);
//			Iterator<ProviderReferralResult> iterator = results.iterator();
//			Map<String, Provider> providers = new HashMap<String, Provider>();
//			Provider p = new Provider(pid, pid, null, null, null, null, null);
//			providers.put(pid, p);
//			while(iterator.hasNext())
//			{
//				ProviderReferralResult pr = iterator.next();
//				p = new Provider(pr.getReferredDoctor(), pr.getReferredDoctor(), null, null, null, null, null);
//				providers.put(pr.getReferredDoctor(), p);
//			}
			if("json".equalsIgnoreCase(format))
		    {
		    	out.println(ProviderReferralHelper.getUIGraphResultOutputJSON(results, pid));
		    }
		    else
		    {
		    	out.println(ProviderReferralHelper.getUIGraphResultOutputXML(results, pid));
		    }
		}
		finally { 
		}
	}

}
