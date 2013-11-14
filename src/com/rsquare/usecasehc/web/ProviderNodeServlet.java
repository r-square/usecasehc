package com.rsquare.usecasehc.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.rsquare.usecasehc.model.ProviderSpecialtyNodes;

/**
 * Servlet implementation class ProviderNodeServlet
 */
@WebServlet("/provider_nodes")
public class ProviderNodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ProviderNodeServlet.class);
	public static ProviderSpecialtyNodes nodes;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProviderNodeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String path = getServletContext().getRealPath("/");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path + "/sources/provider_taxonomy_specialty.json"));
			Gson gson = new Gson();
			nodes = gson.fromJson(reader, ProviderSpecialtyNodes.class);
			System.out.println(nodes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e);
		}
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
		Gson gson = new Gson();
		response.getWriter().println(gson.toJson(nodes.getNodes()));
	}

}
