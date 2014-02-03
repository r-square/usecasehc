package com.rsquare.usecasehc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rsquare.usecasehc.controller.helper.AbstractProviderGridControllerHelper;
import com.rsquare.usecasehc.controller.helper.ProviderGridImpalaControllerHelper;

@Controller
public class ProviderGridController {

	protected static Logger logger = Logger.getLogger(ProviderGridController.class);
	private static final String template = "Hello, %s!";

    @RequestMapping("/greeting")
    public @ResponseBody String greeting(
            @RequestParam(value="name", required=false, defaultValue="World") String name, HttpServletRequest request) {
    	System.out.println("found request " + request.getServletContext().getRealPath("/"));
        return String.format(template, name);
    }
    
    @RequestMapping("/restImpalaProviderData")
    public @ResponseBody String getImpalaProviderData(
            @RequestParam(value="pid", required=false) String pid,
            @RequestParam(value="state", required=false) String state,
            @RequestParam(value="specialty", required=false) String specialty,
            @RequestParam(value="city", required=false) String city,
            @RequestParam(value="iDisplayStart", required=false) String iDisplayStart,
            @RequestParam(value="iDisplayLength", required=false) String iDisplayLength,
            @RequestParam(value="sSearch", required=false) String searchString,
            @RequestParam(value="iSortCol_0", required=false) String iSortCol_0,
            @RequestParam(value="sSortDir_0", required=false) String sortDir,
            HttpServletRequest request, HttpSession session)
    {
    	AbstractProviderGridControllerHelper helper = new ProviderGridImpalaControllerHelper();
    	try {
			return helper.processRequest((String)request.getServletContext().getAttribute("tempFilePath"), pid, state, specialty, city, iDisplayStart, iDisplayLength, 
					searchString, iSortCol_0, sortDir, session);
		} catch (IOException e) {
			logger.error(e);
			return "Encountered Problem processing results. Please check logs for details";
		}
    }
    
}
