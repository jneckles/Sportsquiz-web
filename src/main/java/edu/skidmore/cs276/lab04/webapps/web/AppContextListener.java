package edu.skidmore.cs276.lab04.webapps.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
		LOG.trace("AppContextListener: Initializing application context");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("MySQL JDBC driver is not available", e);
		}
		ServletContext context = sce.getServletContext();
		Properties fileConfig = loadFileConfig();

		String RdbUrl = resolveConfig("SPORTSQUIZ_DB_URL", "db.url", "RdbUrl", context, fileConfig, false);
		String RdbUserId = resolveConfig("SPORTSQUIZ_DB_USER", "db.user", "RdbUserId", context, fileConfig, false);
		String RdbPassword = resolveConfig("SPORTSQUIZ_DB_PASSWORD", "db.password", "RdbPassword", context, fileConfig, true);
		boolean initializeDatabase = Boolean.parseBoolean(
				resolveConfig("SPORTSQUIZ_DB_INITIALIZE", "db.initialize", null, context, fileConfig, true, "true"));

		if (RdbUrl == null || RdbUserId == null) {
			LOG.error("Database configuration is missing. Set SPORTSQUIZ_DB_URL and SPORTSQUIZ_DB_USER or edit src/main/config/app.properties.");
			throw new RuntimeException("Database configuration missing for Sports Quiz");
		}
		LOG.trace("Database Config Read: URL=" + RdbUrl + ", User=" + RdbUserId);

		if (initializeDatabase) {
			DatabaseBootstrapper.initializeIfNeeded(RdbUrl, RdbUserId, RdbPassword);
		}

		QuizDAO quizDAO = new JdbcQuizDAO(RdbUrl, RdbUserId, RdbPassword);
		QuizService quizService = new QuizService(quizDAO);
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

	private Properties loadFileConfig() {
		Properties properties = new Properties();
		try (InputStream input = AppContextListener.class.getClassLoader().getResourceAsStream("app.properties")) {
			if (input != null) {
				properties.load(input);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to load app.properties", e);
		}
		return properties;
	}

	private String resolveConfig(String envName, String fileKey, String contextKey, ServletContext context,
			Properties fileConfig, boolean allowBlank) {
		return resolveConfig(envName, fileKey, contextKey, context, fileConfig, allowBlank, null);
	}

	private String resolveConfig(String envName, String fileKey, String contextKey, ServletContext context,
			Properties fileConfig, boolean allowBlank, String defaultValue) {
		String value = System.getenv(envName);
		if (value == null) {
			value = fileConfig.getProperty(fileKey);
		}
		if (value == null && contextKey != null) {
			value = context.getInitParameter(contextKey);
		}
		if (value == null) {
			return defaultValue;
		}

		String trimmedValue = value.trim();
		if (trimmedValue.isEmpty() && !allowBlank) {
			return defaultValue;
		}
		if (trimmedValue.isEmpty()) {
			return "";
		}
		return trimmedValue;
	}
}
