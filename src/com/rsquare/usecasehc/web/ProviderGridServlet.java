package com.rsquare.usecasehc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.rsquare.usecasehc.hive.HiveClient;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderGridResult;

/**
 * Servlet implementation class ProviderGridServlet
 */
@WebServlet("/providerdata")
public class ProviderGridServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(ProviderGridServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProviderGridServlet() {
        super();
        // TODO Auto-generated constructor stub
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
	
//	private void processRequest1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
//	{
//		PrintWriter out = response.getWriter();
//		Provider p1 = new Provider("1043377500", "Detroit Medical Labs", "", "", "xyz", "Lab", "Agency");
//		Provider p2 = new Provider("1477664738", "", "John", "Smith", "xyz", "Surgeon", "Surgery");
//		Provider p3 = new Provider("14776647380000", "", "John", "Smith", "xyz", "Surgeon", "Surgery");
//		List<Provider> list = new ArrayList<Provider>();
//		String pid = request.getParameter("pid");
//		String state = request.getParameter("state");
//		if(pid!=null &&  !pid.equals(""))
//		{
//			HiveClient hc = new HiveClient();
//			try {
//				Provider p = hc.getProviderById(pid);
//				list.add(p);
//			}
//			catch(SQLException exception)
//			{
//				logger.error(exception);
//			}
//		}
//		if("1".equals(pid)){
//			list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);
//			list.add(p2);
//			list.add(p2);
//			list.add(p2);
//			list.add(p2);
//			list.add(p2);
//			list.add(p2);
//			list.add(p2);
//			list.add(p2);
//		}
//		else if("2".equals(pid))
//		list.add(p3);
//		ProviderGridResult pgr = new ProviderGridResult(1, String.valueOf(list.size()), String.valueOf(list.size()), list);
//		Gson g = new Gson();
//		System.out.println(g.toJson(pgr));
//	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		List<Provider> list = new ArrayList<Provider>();
		String pid = request.getParameter("pid");
		String state = request.getParameter("state");
		if(pid!=null &&  !pid.equals(""))
		{
			HiveClient hc = new HiveClient();
			try {
				Provider p = hc.getProviderById(pid);
				list.add(p);
			}
			catch(SQLException exception)
			{
				logger.error(exception);
			}
		}
		else if(state!=null &&  !state.equals(""))
		{
			HiveClient hc = new HiveClient();
			try {
				list = hc.getProvidersByState(state);
			}
			catch(SQLException exception)
			{
				logger.error(exception);
			}
		}
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
		if(list.size() > 0)
			b.deleteCharAt(b.length()-1);
		b.append("\n]}");
		out.println(b.toString());
	}
	
	

}
