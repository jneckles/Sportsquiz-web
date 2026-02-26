package edu.skidmore.cs276.lab04.webapps.web;

import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class AuthControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(AuthControllerServlet.class);
	
	// Constants for session attribute and request parameter names for CSRF token
	private static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";
	private static final String CSRF_TOKEN_REQUEST_PARAM = "csrfToken";

	@Override
	public void init() throws ServletException {
		// Init code 
		LOG.trace("AuthControllerServlet Initialized.");
		
	}

	/**
	 * Does the GET requests for showing the login page and processing logout.
	 * Generates CSRF tokens for login page requests.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		HttpSession session = request.getSession(); // Get session to manage CSRF token

		if ("showLogin".equals(action) || action == null) {
			// Action: Display the login form

			// Generates and then it stores the CSRF token for the login form
			String csrfToken = UUID.randomUUID().toString();
			session.setAttribute(CSRF_TOKEN_SESSION_ATTR, csrfToken);
			request.setAttribute(CSRF_TOKEN_REQUEST_PARAM, csrfToken); // Pass token to the JSP

			LOG.trace("GET /authenticate?action=showLogin - Displaying login page.");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/hiddenjsp/login.jsp");
			dispatcher.forward(request, response);

		} else if ("logout".equals(action)) {
			// Process user logout
			LOG.trace("GET /authenticate?action=logout - Processing logout."); 

			session = request.getSession(false); // Get existing session, don't create
			if (session != null) {
				String user = (String) session.getAttribute("loggedInUser");
				session.invalidate(); // Invalidate the session to log the user out
				LOG.trace("Session invalidated for user: " + (user != null ? user : "N/A")); 
			}
			// Redirect back to login page after logout for a new state
			response.sendRedirect(request.getContextPath() + "/authenticate?action=showLogin");

		} else {
			// Unknown GET action, default to showing login page 
			LOG.debug("GET /authenticate - Unknown action: " + action + ". Defaulting to show login."); 
			String csrfToken = UUID.randomUUID().toString();
			session.setAttribute(CSRF_TOKEN_SESSION_ATTR, csrfToken);
			request.setAttribute(CSRF_TOKEN_REQUEST_PARAM, csrfToken);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/hiddenjsp/login.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Handles the POST requests for processing login form submissions. Validates CSRF
	 * token before processing credentials.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		HttpSession session = request.getSession(false); // Get session if exists, don't create yet

		if ("login".equals(action)) {
			// Process login form submission
			LOG.trace("POST /authenticate?action=login - Processing login attempt.");

			// CSRF Token Validation 
			String sessionToken = (session != null) ? (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR) : null;
			String requestToken = request.getParameter(CSRF_TOKEN_REQUEST_PARAM);

			if (sessionToken == null || requestToken == null || !sessionToken.equals(requestToken)) {
				// CSRF check failed then Logs the attempt and deny the request.
				LOG.error("CSRF Check Failed during the login! SessionToken='" + sessionToken + "', RequestToken='"
						+ requestToken + "'"); 
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request (CSRF check failed).");
				return; 
			}
			

			String username = request.getParameter("username");
			String password = request.getParameter("password"); // Password from the form input

			// Use AuthService 
			if (AuthService.validateCredentials(username, password)) {
				// Login is good
				LOG.trace("Login successful for user: " + username); 

				// make sure session exists 
				session = request.getSession(true);
				// Store user identifier in session
				session.setAttribute("loggedInUser", username.trim());
				
				// *** Determine User Role ***
                // Simple check: if username is "admin", role is ADMIN, otherwise USER
                String userRole = "admin".equalsIgnoreCase(username.trim()) ? Roles.ADMIN : Roles.USER;
                System.out.println("User role determined as: " + userRole); // Use Logger

                // *** Store Username AND Role in Session ***
                session.setAttribute("loggedInUser", username.trim());
                session.setAttribute("userRole", userRole); // New session attribute

				// Regenerates the CSRF token after state change
				String newCsrfToken = UUID.randomUUID().toString();
				session.setAttribute(CSRF_TOKEN_SESSION_ATTR, newCsrfToken);
				LOG.trace("New CSRF token generated after successful login."); 

				// Redirect to the main application page (quiz selection) after successful login
				response.sendRedirect(request.getContextPath() + "/quizme?action=select");

			} else {
				// Login failed
				LOG.trace("Login failed for user: " + username); 
				// Set error message for the login page view
				request.setAttribute("loginError", "Invalid username or password.");

				request.setAttribute(CSRF_TOKEN_REQUEST_PARAM, sessionToken);
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/hiddenjsp/login.jsp");
				dispatcher.forward(request, response);
			}

		} else {
			// Unknown POST actions
			LOG.error("Invalid POST action received on /authenticate: " + action);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid form submission action.");
		}
	}

	public void destroy() {
		// Cleanup the resources
		LOG.trace("AuthControllerServlet Destroyed."); 
	}
}
