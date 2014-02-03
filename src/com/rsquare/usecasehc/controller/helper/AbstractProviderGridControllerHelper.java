package com.rsquare.usecasehc.controller.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderGridResult;
import com.rsquare.usecasehc.util.ProviderSortHelper;

public abstract class AbstractProviderGridControllerHelper {
	
	protected static Logger logger = Logger.getLogger(AbstractProviderGridControllerHelper.class);
	
	public abstract String processRequest(String tempFilePath, String pid, String state, String specialty, String city, String iDisplayStart, String iDisplayLength,
			String searchString, String iSortCol_0, String sortDir, HttpSession session)  throws IOException;
	
	protected List<Provider> getProviders(int startIndex, int records, List<Provider>providers)
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
	
	protected File createTempFile(String tempFilePath, String key)
	{
		//String fileName = UUID.randomUUID().toString();
		File f = new File(tempFilePath + "/" + key);
		return f;
	}
	
	protected String writeJSON(ProviderGridResult pgr)
	{
		Gson g = new Gson();
		return g.toJson(pgr);
	}
	
	protected void applySorting(List<Provider> list, int sortCol, String sortDir)
	{
		switch (sortCol)
		{
			case 0:
				ProviderSortHelper.getSortedListByNpi(list, sortDir);
				break;
			case 1:
				ProviderSortHelper.getSortedListByName(list, sortDir);
				break;
			case 2:
				ProviderSortHelper.getSortedListBySpecialty(list, sortDir);
				break;
			case 3:
				ProviderSortHelper.getSortedListByCity(list, sortDir);
				break;
			case 4:
				ProviderSortHelper.getSortedListByState(list, sortDir);
				break;
			
		}
	}
	
	protected boolean pidPresent(String pid)
	{
		if(pid!=null &&  !pid.equals("") && !pid.startsWith("debug"))
		{
			return true;
		}
		return false;
	}
	
	protected boolean statePresent(String state)
	{
		if(state!=null &&  !state.equals("") && !state.equalsIgnoreCase("All States"))
		{
			return true;
		}
		return false;
	}
	
	protected boolean cityPresent(String city)
	{
		if(city!=null && !city.equals(""))
		{
			return true;
		}
		return false;
	}
	
	protected boolean specialtyPresent(String specialty)
	{
		if(specialty!=null && !specialty.equals("") && !specialty.equals("undefined"))
		{
			return true;
		}
		return false;
	}
	
	protected List<Provider> getStubData(String pid)
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

}
