package edu.skidmore.cs276.lab04.webapps.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;



import org.apache.log4j.Logger;

import edu.skidmore.cs276.lab04.beans.tasklist.JdbcQuizDAO;
import edu.skidmore.cs276.lab04.beans.tasklist.QuizDAO;




/**
 * Application Listener. Initializes the Services and DAOs when the web
 * app starts
 */
public class AppContextListener implements ServletContextListener {
	
	private static Logger LOG = Logger.getLogger(AppContextListener.class);

	/**
	 * Called when the web app is starting. Reads DB config, initializes
	 * DAO with connection parameters, initializes Service, and stores Service in
	 * ServletContext.
	 *
	 * @param sce The ServletContextEvent containing the ServletContext.
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.trace("AppContextListener: Initializing application context"); // Use	
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServletContext context = sce.getServletContext();
		

		String RdbUrl = context.getInitParameter("RdbUrl");
		String RdbUserId = context.getInitParameter("RdbUserId");
		String RdbPassword = context.getInitParameter("RdbPassword");

		// ensure that parameters were found
		if (RdbUrl == null || RdbUserId == null || RdbPassword == null) {
			LOG.error("ERROR: Database configuration parameters (dbUrl, dbUser, dbPassword) not found in web.xml <context-param>!"); 
																																			
			throw new RuntimeException("Database configuration missing in web.xml");
		}
		LOG.trace("Database Config Read: URL=" + RdbUrl + ", User=" + RdbUserId);  
																						 

		// Init DAO Implementation & Pass connection parameters directly
		//  JdbcQuizDAO constructor is updated to accept the parameters.
		QuizDAO quizDAO = new JdbcQuizDAO(RdbUrl, RdbUserId, RdbPassword);

		// Init Service Layers
		QuizService quizService = new QuizService(quizDAO);

		// Store initialized Services/Resources in ServletContext
		context.setAttribute("quizService", quizService);


		LOG.trace("AppContextListener: QuizService initialized and added to context."); 
		LOG.trace("Application context initialization complete."); 
	}

	/**
	 * Called when the web application is shutting down, and does the cleanup.
	 * 
	 *
	 * @param sce The ServletContextEvent.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOG.trace("AppContextListener: Destroying application context");

		

		LOG.trace("Application context cleanup complete.");
	}
}
