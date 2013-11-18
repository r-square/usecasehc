package com.rsquare.usecasehc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.ProviderSpecialtyNode;

public class PredicateHelper {
	
	public static Predicate<Provider> getFilterPredicate(final String searchString)
	{
		return new Predicate<Provider>() {
			  public boolean apply(Provider p) {
			    return (p.getName().toLowerCase().contains(searchString.toLowerCase()) || p.getNpi().toLowerCase().contains(searchString.toLowerCase()) || 
			    		p.getSpecialty().toLowerCase().contains(searchString.toLowerCase()) || p.getOrganization().toLowerCase().contains(searchString.toLowerCase()));
			  }
			};
	}
	
	public static Map<String, ProviderSpecialtyNode> getFlatFilteredProviderSpecialtyNodes(List<ProviderSpecialtyNode> nodes, String id)
	{
		Map<String, ProviderSpecialtyNode> queryNodes = new HashMap<String, ProviderSpecialtyNode>();
		List<ProviderSpecialtyNode> filteredNodes = Lists.newArrayList(Collections2.filter(nodes, getProviderSpecialtyNodePredicate(id)));
		if(filteredNodes!=null && filteredNodes.size() > 0)
		{
			//add all the children of each node
			List<ProviderSpecialtyNode> childNodes = new ArrayList<ProviderSpecialtyNode>();
			for(ProviderSpecialtyNode provider: filteredNodes)
			{
				if(provider.getTaxonomy_id()!=null && !provider.getTaxonomy_id().equals(""))
				{
					queryNodes.put(provider.getTaxonomy_id(), provider);
				}
				getChildProviderSpecialtyNodes(childNodes, provider);
				childNodes.remove(provider);
			}

			for(ProviderSpecialtyNode provider: childNodes)
			{
				if(provider.getTaxonomy_id()!=null && !provider.getTaxonomy_id().equals(""))
				{
					queryNodes.put(provider.getTaxonomy_id(), provider);
				}
			}
		}
		else
		{
			for(ProviderSpecialtyNode provider: nodes)
			{
				if(provider.hasChildren())
				{
					queryNodes = getFlatFilteredProviderSpecialtyNodes(provider.getChildren(), id);
					if(queryNodes!=null && queryNodes.size() > 0) break;
				}
			}
		}
		return queryNodes;
	}
	
	private static Predicate<ProviderSpecialtyNode> getProviderSpecialtyNodePredicate(final String id)
	{
		//iterate the children as well to find the exact node.
		return new Predicate<ProviderSpecialtyNode>() {
			  public boolean apply(ProviderSpecialtyNode p) {
			    return (id.equalsIgnoreCase(p.getId()));
			  }
			};
	}
	
	private static void getChildProviderSpecialtyNodes(List<ProviderSpecialtyNode> childNodes, ProviderSpecialtyNode provider)
	{
		if(provider.hasChildren())
		{
			for(ProviderSpecialtyNode child: provider.getChildren())
			{
				getChildProviderSpecialtyNodes(childNodes, child);
			}
			
		}
		childNodes.add(provider);
	}

}
