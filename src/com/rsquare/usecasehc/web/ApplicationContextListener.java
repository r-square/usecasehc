package com.rsquare.usecasehc.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

/**
 * Application Lifecycle Listener implementation class ApplicationContextListener
 *
 */
@WebListener
public class ApplicationContextListener implements ServletContextListener {
	
	private static Logger logger = Logger.getLogger(ApplicationContextListener.class);

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	String path = arg0.getServletContext().getRealPath("/");
		File f = new File(path + "../../tmp");
		boolean exists = f.exists();
		if(!exists)
		{
			exists = f.mkdir();
			if(!exists)
			{
				logger.error("Failed to initialize temp directory for ProviderGrid data");
			}
		}
		arg0.getServletContext().setAttribute("tempFilePath", f.getAbsolutePath());
		logger.info("Temp file path is: " + f.getAbsolutePath());
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
