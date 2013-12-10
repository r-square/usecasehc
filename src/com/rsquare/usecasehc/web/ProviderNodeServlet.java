package com.rsquare.usecasehc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rsquare.usecasehc.hive.HiveClient;
import com.rsquare.usecasehc.model.Provider;

/**
 * Servlet implementation class ProviderNodeServlet
 */
@WebServlet("/provider_details")
public class ProviderNodeServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(ProviderNodeServlet.class);
	private static final long serialVersionUID = 1L;

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
		PrintWriter out = response.getWriter();
		StringBuilder sb = new StringBuilder();
		HiveClient hc = new HiveClient();
		try {
		   long t1 = System.currentTimeMillis();
		   Provider p = hc.getProviderById(pid);
		   long t2 = System.currentTimeMillis();
		   logger.info("getProviders() took time(mSec): " + (t2-t1));
		   sb.append("<b>PID:</b> ");
		   sb.append(p.getNpi());
		   sb.append("<br/><b>Name:</b> ");
		   sb.append(p.getName());
		   sb.append("<br/><b>General Area:</b> ");
		   sb.append(p.getGeneral_area());
		   sb.append("<br/><b>Specialty:</b> ");
		   sb.append(p.getSpecialty());
		   sb.append("<br/><b>City:</b> ");
		   sb.append(p.getCity());
		   sb.append("<br/><b>State:</b> ");
		   sb.append(p.getState());
		}
		catch(SQLException exception)
		{
			sb.append("<html><body>Could not find details for this provider</body></html>");
		}
		finally
		{
			out.println(sb.toString());
			try {
				hc.getConnection().close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

}
