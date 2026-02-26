package edu.skidmore.cs276.lab04.webapps.web;

import java.io.IOException;

import org.apache.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



/**
 * A Servlet Filter that checks if a user is authenticated before allowing
 * access to quiz. If the user is not authenticated, it redirects them to the login page.
 */
// Applies this filter to all requests starting with /quiz/
// Ensure that this pattern covers all resources that need loggging in.

public class AuthenticationFilter implements Filter {
	
	private static Logger LOG = Logger.getLogger(AuthenticationFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Initialization code for the filter, if needed.
		LOG.trace("AuthenticationFilter initialized."); 
	}

	/**
	 * The filtering logic. Checks for a loggedin user in the session. Allows
	 * the request to happen if logged in, if not then redirects to login.
	 *
	 * @param request  The ServletRequest object containing the client's request.
	 * @param response The ServletResponse object containing the filter's response.
	 * @param chain    The FilterChain for invoking the next filter or the target
	 *                 resource.
	 * @throws IOException      if an I/O related error occurs during processing.
	 * @throws ServletException if an exception occurs that interferes with the
	 *                          filter's normal operation.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// Gets the current session
		HttpSession session = httpRequest.getSession(false);

		// Checks if the user is logged in & check for "loggedInUser"
		// attribute
		boolean isLoggedIn = (session != null && session.getAttribute("loggedInUser") != null);

		// Get the path for logging
		String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
		

		if (isLoggedIn) {
			chain.doFilter(request, response);
		} else {
			// User is not logged in.
			LOG.trace("AuthenticationFilter: User not logged in. Redirecting to login page for request to " + path); 
																													
			// Construct the login URL using context path.
			String loginUrl = httpRequest.getContextPath() + "/authentication?action=showLogin";
			httpResponse.sendRedirect(loginUrl);
		}
	}

	@Override
	public void destroy() {
		// Cleanup code 
		LOG.trace("AuthenticationFilter destroyed."); 
	}
}
