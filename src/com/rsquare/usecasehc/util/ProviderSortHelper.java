package com.rsquare.usecasehc.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.rsquare.usecasehc.model.Provider;

public class ProviderSortHelper {
	
	private static Logger logger = Logger.getLogger(ProviderSortHelper.class);
	private static final String ASC = "asc";
//	private static final String DESC = "desc";
	
	public static void getSortedListByNpi(List<Provider> list, String order)
	{
		if(ASC.equalsIgnoreCase(order))
		{
			Collections.sort(list, new NpiComparator());
		}
		else
		{
			Collections.sort(list, Collections.reverseOrder(new NpiComparator()));
		}
		
	}
	
	public static void getSortedListByName(List<Provider> list, String order)
	{
		if(ASC.equalsIgnoreCase(order))
		{
			Collections.sort(list, new NameComparator());
		}
		else
		{
			Collections.sort(list, Collections.reverseOrder(new NameComparator()));
		}
		
	}
	
	public static void getSortedListBySpecialty(List<Provider> list, String order)
	{
		if(ASC.equalsIgnoreCase(order))
		{
			Collections.sort(list, new SpecialtyComparator());
		}
		else
		{
			Collections.sort(list, Collections.reverseOrder(new SpecialtyComparator()));
		}
		
	}
	
	public static void getSortedListByCity(List<Provider> list, String order)
	{
		if(ASC.equalsIgnoreCase(order))
		{
			Collections.sort(list, new CityComparator());
		}
		else
		{
			Collections.sort(list, Collections.reverseOrder(new CityComparator()));
		}
		
	}
	
	public static void getSortedListByState(List<Provider> list, String order)
	{
		if(ASC.equalsIgnoreCase(order))
		{
			Collections.sort(list, new StateComparator());
		}
		else
		{
			Collections.sort(list, Collections.reverseOrder(new StateComparator()));
		}
		
	}
	
	private static class NpiComparator implements Comparator<Provider>
	{
		@Override
		public int compare(Provider o1, Provider o2)
		{
			try
			{
				int id1 = Integer.parseInt(o1.getNpi());
				int id2 = Integer.parseInt(o2.getNpi());
				return id1 - id2;
			}
			catch(NumberFormatException nfe)
			{
				logger.info(nfe);
				return 0;
			}
		}
	}
	
	private static class NameComparator implements Comparator<Provider>
	{
		@Override
		public int compare(Provider o1, Provider o2)
		{
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}
	
	private static class SpecialtyComparator implements Comparator<Provider>
	{
		@Override
		public int compare(Provider o1, Provider o2)
		{
			return o1.getSpecialty().compareToIgnoreCase(o2.getSpecialty());
		}
	}
	
	private static class CityComparator implements Comparator<Provider>
	{
		@Override
		public int compare(Provider o1, Provider o2)
		{
			return o1.getCity().compareToIgnoreCase(o2.getCity());
		}
	}
	
	private static class StateComparator implements Comparator<Provider>
	{
		@Override
		public int compare(Provider o1, Provider o2)
		{
			return o1.getState().compareToIgnoreCase(o2.getState());
		}
	}

}
