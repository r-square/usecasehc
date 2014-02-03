package com.rsquare.usecasehc.controller.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.rsquare.usecasehc.impala.ImpalaClient;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderGridResult;
import com.rsquare.usecasehc.neo4j.Neo4jClient;
import com.rsquare.usecasehc.util.PredicateHelper;
import com.rsquare.usecasehc.web.ProviderSpecialtyTreeServlet;

public class ProviderGridImpalaControllerHelper extends
		AbstractProviderGridControllerHelper {
	
	@Override
	public String processRequest(String tempFilePath, String pid, String state, String specialty, String city, String iDisplayStart, String iDisplayLength,
			String searchString, String iSortCol_0, String sortDir, HttpSession session) throws IOException
	{
		List<Provider> list = new ArrayList<Provider>();
		final int startIndex = Integer.parseInt(iDisplayStart);
		final int records = Integer.parseInt(iDisplayLength);
		final int sortCol = Integer.parseInt(iSortCol_0);
//		HttpSession session = request.getSession(true);
		String fileName = pid + state + city + specialty;
		String sortDone = (String)session.getAttribute("sort");
		boolean filterResults = false;
		if(searchString!=null &&  !searchString.equals("") && !searchString.startsWith("debug"))
		{
			filterResults = true;
			
		}
		//File f = new File(tempFilePath + "/" + "b10878b7-a254-4670-ba6e-7529d98c742e");
		File f = new File(tempFilePath + "/" + fileName);
		if(!f.exists())
		{
			//First run of the query
			if(pidPresent(pid))
			{
				ImpalaClient hc = new ImpalaClient();
				try {
					Provider p = hc.getProviderById(pid);
					list.add(p);
				}
				catch(SQLException exception)
				{
					logger.error(exception);
				}
			}
			else if(statePresent(state) && !cityPresent(city) && !specialtyPresent(specialty))
			{
				ImpalaClient hc = new ImpalaClient();
				try {
					list = hc.getProvidersByState(state);
				}
				catch(SQLException exception)
				{
					logger.error(exception);
				}
			}
			else if(statePresent(state) && !cityPresent(city) && specialtyPresent(specialty))
			{
				ImpalaClient hc = new ImpalaClient();
				try {
					//get provider by state & specialty
					list = hc.getProvidersByStateAndSpecialty(PredicateHelper.getFlatFilteredProviderSpecialtyNodes(ProviderSpecialtyTreeServlet.nodes.getNodes(), specialty), state);
				}
				catch(SQLException exception)
				{
					logger.error(exception);
				}
			}
			else if(!statePresent(state) && !cityPresent(city) && specialtyPresent(specialty))
			{
				ImpalaClient hc = new ImpalaClient();
				try {
					//get provider by specialty
					list = hc.getProvidersBySpecialty(PredicateHelper.getFlatFilteredProviderSpecialtyNodes(ProviderSpecialtyTreeServlet.nodes.getNodes(), specialty));
				}
				catch(SQLException exception)
				{
					logger.error(exception);
				}
			}
			else if(statePresent(state) && cityPresent(city) && specialtyPresent(specialty))
			{
				ImpalaClient hc = new ImpalaClient();
				try {
					//get provider by specialty
					list = hc.getProvidersByStateAndCityAndSpecialty(PredicateHelper.getFlatFilteredProviderSpecialtyNodes(ProviderSpecialtyTreeServlet.nodes.getNodes(), specialty), state, city);
				}
				catch(SQLException exception)
				{
					logger.error(exception);
				}
			}
			else if(statePresent(state) && cityPresent(city) && !specialtyPresent(specialty))
			{
				ImpalaClient hc = new ImpalaClient();
				try {
					//get provider by specialty
					list = hc.getProvidersByStateAndCity(state, city);
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
				File f1 = createTempFile(tempFilePath, fileName);
				logger.info("File path: " + f1.getAbsolutePath());
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(f1));
				output.writeObject(pgr);
				output.close();
			}
			if(filterResults)
			{
				List<Provider> filteredList = Lists.newArrayList(Collections2.filter(list, PredicateHelper.getFilterPredicate(searchString)));
				List<Provider> pagedList = getProviders(startIndex, records, filteredList);
				Neo4jClient nc = new Neo4jClient();
				try {
					nc.getReferralCountByProvider(pagedList);
				} catch (SQLException e) {
					logger.error(e);
				}
				ProviderGridResult pgr1 = new ProviderGridResult(String.valueOf(list.size()), String.valueOf(filteredList.size()), pagedList);
				return writeJSON(pgr1);
			}
			else
			{
				List<Provider> pagedList = getProviders(startIndex, records, list);
				Neo4jClient nc = new Neo4jClient();
				try {
					nc.getReferralCountByProvider(pagedList);
				} catch (SQLException e) {
					logger.error(e);
				}
				ProviderGridResult pgr1 = new ProviderGridResult(String.valueOf(list.size()), String.valueOf(list.size()), pagedList);
				return writeJSON(pgr1);
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
					List<Provider> filteredList = Lists.newArrayList(Collections2.filter(pgr.getAaData(), PredicateHelper.getFilterPredicate(searchString)));
					List<Provider> pagedList = getProviders(startIndex, records, filteredList);
					Neo4jClient nc = new Neo4jClient();
					try {
						nc.getReferralCountByProvider(pagedList);
					} catch (SQLException e) {
						logger.error(e);
					}
					ProviderGridResult pgr1 = new ProviderGridResult(String.valueOf(pgr.getAaData().size()), String.valueOf(filteredList.size()), pagedList);
					return writeJSON(pgr1);
				}
				else
				{
					List<Provider> pagedList = getProviders(startIndex, records, pgr.getAaData());
					Neo4jClient nc = new Neo4jClient();
					try {
						nc.getReferralCountByProvider(pagedList);
					} catch (SQLException e) {
						logger.error(e);
					}
					ProviderGridResult pgr1 = new ProviderGridResult(String.valueOf(pgr.getAaData().size()), String.valueOf(pgr.getAaData().size()), pagedList);
					return writeJSON(pgr1);
				}
			} catch (ClassNotFoundException e) {
				logger.error(e);
				ProviderGridResult pgr = new ProviderGridResult(String.valueOf(list.size()), String.valueOf(list.size()), list);
				return writeJSON(pgr);
			}
			finally
			{
				input.close();
			}
		}
	}

}
