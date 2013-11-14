package com.rsquare.usecasehc.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.rsquare.usecasehc.hive.HiveClient;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderGridResult;
import com.rsquare.usecasehc.util.PredicateHelper;
import com.rsquare.usecasehc.util.ProviderSortHelper;

/**
 * Servlet implementation class ProviderGridServlet
 */
@WebServlet("/providerdata")
public class ProviderGridServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ProviderGridServlet.class);
	private static String tempFilePath = "./tmp";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProviderGridServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String path = getServletContext().getRealPath("/");
		File f = new File(path + "../../tmp");
		boolean exists = f.exists();
		if(!exists)
		{
			exists = f.mkdir();
			if(!exists)
			{
				logger.error("Failed to initialize temp directory for ProviderGrid data");
				throw new ServletException("Failed to initialize temp directory for ProviderGrid data");
			}
		}
		tempFilePath = f.getAbsolutePath();
		logger.info("Temp file path is: " + tempFilePath);
		
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
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		List<Provider> list = new ArrayList<Provider>();
		final String pid = request.getParameter("pid");
		final String state = request.getParameter("state");
		final String specialty = request.getParameter("specialty");
		final int startIndex = Integer.parseInt(request.getParameter("iDisplayStart"));
		final int records = Integer.parseInt(request.getParameter("iDisplayLength"));
		final String searchString = request.getParameter("sSearch");
		final int sortCol = Integer.parseInt(request.getParameter("iSortCol_0"));
		final String sortDir = request.getParameter("sSortDir_0");
		HttpSession session = request.getSession(true);
		File f = (File)session.getAttribute(pid + state + specialty);
		String sortDone = (String)session.getAttribute("sort");
		boolean filterResults = false;
		if(searchString!=null &&  !searchString.equals("") && !searchString.startsWith("debug"))
		{
			filterResults = true;
			
		}
//		f = new File(tempFilePath + "/" + "b10878b7-a254-4670-ba6e-7529d98c742e");
		if(f==null)
		{
			//First run of the query
			if(pid!=null &&  !pid.equals("") && !pid.startsWith("debug"))
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
			else if(state!=null &&  !state.equals("") && !state.equalsIgnoreCase("All States") && (specialty==null || specialty.equals("") || specialty.equals("undefined")))
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
			else if(state!=null &&  !state.equals("") && !state.equalsIgnoreCase("All States") && specialty!=null && !specialty.equals("") && !specialty.equals("undefined"))
			{
				HiveClient hc = new HiveClient();
				try {
					//get provider by state & specialty
					list = hc.getProvidersByStateAndSpecialty(PredicateHelper.getFlatFilteredProviderSpecialtyNodes(ProviderNodeServlet.nodes.getNodes(), specialty), state);
				}
				catch(SQLException exception)
				{
					logger.error(exception);
				}
			}
			else if((state==null || state.equals("") || state.equalsIgnoreCase("All States")) && specialty!=null && !specialty.equals("") && !specialty.equals("undefined"))
			{
				HiveClient hc = new HiveClient();
				try {
					//get provider by specialty
					list = hc.getProvidersBySpecialty(PredicateHelper.getFlatFilteredProviderSpecialtyNodes(ProviderNodeServlet.nodes.getNodes(), specialty));
				}
				catch(SQLException exception)
				{
					logger.error(exception);
				}
			}
			else
			{
				list = getStubData(pid);
			}
			logger.info("Got the Hive results - Writing temp file");
			if(sortDone==null || !sortDone.equalsIgnoreCase(sortCol + sortDir))
			{
				session.setAttribute("sort", sortCol + sortDir);
				applySorting(list, sortCol, sortDir);
			}
			
			ProviderGridResult pgr = new ProviderGridResult(String.valueOf(list.size()), String.valueOf(list.size()), list);
			if(list.size() > 0)
			{
				File f1 = createTempFile();
				logger.info("File path: " + f1.getAbsolutePath());
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(f1));
				output.writeObject(pgr);
				output.close();
				session.setAttribute(pid + state + specialty, f1);
			}
			if(filterResults)
			{
				List<Provider> filteredList = Lists.newArrayList(Collections2.filter(list, getFilterPredicate(searchString)));
				ProviderGridResult pgr1 = new ProviderGridResult(String.valueOf(list.size()), String.valueOf(filteredList.size()), getProviders(startIndex, records, filteredList));
				writeJSON(pgr1, out);
			}
			else
			{
				ProviderGridResult pgr1 = new ProviderGridResult(String.valueOf(list.size()), String.valueOf(list.size()), getProviders(startIndex, records, list));
				writeJSON(pgr1, out);
			}
		}
		else
		{
			//Look for existing file and get the data.
			logger.info("Reading existing file: " + f.getAbsolutePath());
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(f));
			try {
				ProviderGridResult pgr = (ProviderGridResult)input.readObject();
				if(sortDone==null || !sortDone.equalsIgnoreCase(sortCol + sortDir))
				{
					session.setAttribute("sort", sortCol + sortDir);
					applySorting(pgr.getAaData(), sortCol, sortDir);
					logger.info(sortDir + " " + pgr.getAaData());
					ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(f));
					output.writeObject(pgr);
					output.close();
				}
				if(filterResults)
				{
					List<Provider> filteredList = Lists.newArrayList(Collections2.filter(pgr.getAaData(), getFilterPredicate(searchString)));
					ProviderGridResult pgr1 = new ProviderGridResult(String.valueOf(pgr.getAaData().size()), String.valueOf(filteredList.size()), getProviders(startIndex, records, filteredList));
					writeJSON(pgr1, out);
				}
				else
				{
					ProviderGridResult pgr1 = new ProviderGridResult(String.valueOf(pgr.getAaData().size()), String.valueOf(pgr.getAaData().size()), getProviders(startIndex, records, pgr.getAaData()));
					writeJSON(pgr1, out);
				}
			} catch (ClassNotFoundException e) {
				logger.error(e);
				ProviderGridResult pgr = new ProviderGridResult(String.valueOf(list.size()), String.valueOf(list.size()), list);
				writeJSON(pgr, out);
			}
			finally
			{
				input.close();
			}
		}
	}
	
	private List<Provider> getProviders(int startIndex, int records, List<Provider>providers)
	{
		if(providers.size() > 0)
		{
			if(providers.size() > (startIndex + records))
			{
				return providers.subList(startIndex, startIndex + records);
			}
			else
			{
				return providers.subList(startIndex, providers.size());
			}
		}
		return providers;
	}
	
	private File createTempFile()
	{
		String fileName = UUID.randomUUID().toString();
		File f = new File(tempFilePath + "/" + fileName);
		return f;
	}
	
	private Predicate<Provider> getFilterPredicate(final String searchString)
	{
		return new Predicate<Provider>() {
			  public boolean apply(Provider p) {
			    return (p.getName().toLowerCase().contains(searchString.toLowerCase()) || p.getNpi().toLowerCase().contains(searchString.toLowerCase()) || 
			    		p.getSpecialty().toLowerCase().contains(searchString.toLowerCase()) || p.getOrganization().toLowerCase().contains(searchString.toLowerCase()));
			  }
			};
	}
	
	private void writeJSON(ProviderGridResult pgr, PrintWriter out)
	{
		Gson g = new Gson();
		out.println(g.toJson(pgr));
	}
	
	private void applySorting(List<Provider> list, int sortCol, String sortDir)
	{
		switch (sortCol)
		{
			case 0:
				ProviderSortHelper.getSortedListByNpi(list, sortDir);
				break;
			case 1:
				ProviderSortHelper.getSortedListByOrganization(list, sortDir);
				break;
			case 2:
				ProviderSortHelper.getSortedListByName(list, sortDir);
				break;
			case 3:
				ProviderSortHelper.getSortedListBySpecialty(list, sortDir);
				break;
			
		}
	}
	
	private List<Provider> getStubData(String pid)
	{
		Provider p1 = new Provider("1043377500", "Detroit Medical Labs", "", "", "xyz", "Lab", "Dentistry");
		Provider p2 = new Provider("1477664738", "", "John", "Smith", "xyz", "Surgeon", "Surgery");
		Provider p3 = new Provider("1477664738", "", "John", "Smith2", "xyz", "Surgeon", "Surgery");
		Provider p4 = new Provider("1477664738", "", "ABC", "XYZ", "xyz", "Surgeon", "Dentistry");
		List<Provider> list = new ArrayList<Provider>();
		if("debug1".equals(pid)){
			list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);list.add(p1);
			list.add(p2);
			list.add(p2);
			list.add(p2);
			list.add(p2);
			list.add(p2);
			list.add(p2);
			list.add(p2);
			list.add(p2);
			list.add(p4);list.add(p4);
			list.add(p4);list.add(p4);
		}
		else if("debug2".equals(pid))
		{
			list.add(p3);
		}
		return list;
	}
	
//	private void processRequest1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
//	{
//		PrintWriter out = response.getWriter();
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
//		else if(state!=null &&  !state.equals(""))
//		{
//			HiveClient hc = new HiveClient();
//			try {
//				list = hc.getProvidersByState(state);
//			}
//			catch(SQLException exception)
//			{
//				logger.error(exception);
//			}
//		}
//		StringBuilder b = new StringBuilder();
//		Iterator<Provider> iterator = list.iterator();
//		b.append("{ \"providerList\": [");
//		while(iterator.hasNext())
//		{
//			b.append("\n");
//			Provider p = iterator.next();
//			b.append(p.toJSONString());
//			b.append(",");
//		}
//		if(list.size() > 0)
//			b.deleteCharAt(b.length()-1);
//		b.append("\n]}");
//		out.println(b.toString());
//	}
}
