package com.rsquare.usecasehc.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.rsquare.usecasehc.model.Location;
import com.rsquare.usecasehc.model.Provider;
import com.rsquare.usecasehc.model.Taxonomy;
import com.rsquare.usecasehc.neo4j.Neo4jClient;
import com.rsquare.usecasehc.util.PredicateHelper;

@Controller
public class NLPController {
	
	protected static Logger logger = Logger.getLogger(NLPController.class);
	
	private static List<Location> locations;
	private static List<Taxonomy> taxonomies;
	
	@RequestMapping("/getLocations")
    public @ResponseBody List<Location> getLocations(
            @RequestParam(value="seed", required=false, defaultValue="") String key) {
		logger.info("Received input search string: " + key);
		if(locations==null)
		{
			Neo4jClient nc = new Neo4jClient();
			try {
				locations = nc.getLocations();
			} catch (SQLException e) {
				logger.error(e);
				locations = new ArrayList<Location>();
			}
		}
		
		List<Location> filteredList = Lists.newArrayList(Collections2.filter(locations, PredicateHelper.getLocationPredicate(key)));
		return filteredList;
    }
	
	@RequestMapping("/getTaxonomies")
    public @ResponseBody List<Taxonomy> getTaxonomies(
            @RequestParam(value="seed", required=false, defaultValue="") String key) {
		logger.info("Received input search string: " + key);
		if(taxonomies==null)
		{
			Neo4jClient nc = new Neo4jClient();
			try {
				taxonomies = nc.getTaxonomies();
			} catch (SQLException e) {
				logger.error(e);
				taxonomies = new ArrayList<Taxonomy>();
			}
		}
		
		List<Taxonomy> filteredList = Lists.newArrayList(Collections2.filter(taxonomies, PredicateHelper.getTaxonomyPredicate(key)));
		return filteredList;
    }

}
