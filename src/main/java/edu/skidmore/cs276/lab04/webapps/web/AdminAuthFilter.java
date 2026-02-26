package edu.skidmore.cs276.lab04.webapps.web; // Use your filter package

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to ensure only users with the ADMIN role can access URLs under /admin/*.
 */

public class AdminAuthFilter implements Filter {

    // No need for ADMIN_USERNAME constant anymore

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AdminAuthFilter initialized."); // Use Logger
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean loggedIn = false;
        boolean isAdmin = false;
        String username = null;

        if (session != null) {
            Object userObj = session.getAttribute("loggedInUser");
            Object roleObj = session.getAttribute("userRole"); // Get the role attribute

            if (userObj instanceof String) {
                loggedIn = true; // User is logged in if username exists
                username = (String) userObj;
                if (roleObj instanceof String) {
                    // Check if the stored role is ADMIN
                    if (Roles.ADMIN.equals((String) roleObj)) {
                        isAdmin = true;
                    }
                }
            }
        }

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (isAdmin) {
            // User has ADMIN role, allow access
            System.out.println("AdminAuthFilter: ADMIN user '" + username + "' accessing " + path); // Use Logger
            chain.doFilter(request, response);
        } else if (loggedIn) {
            // User is logged in but does NOT have ADMIN role
            System.out.println("AdminAuthFilter: Forbidden. Non-admin user '" + username + "' attempted to access " + path); // Use Logger
            // Send 403 Forbidden error
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You do not have administrator privileges.");
        } else {
            // User is not logged in at all
            System.out.println("AdminAuthFilter: Not logged in. Redirecting to login page for request to " + path); // Use Logger
            // Redirect to login page, maybe add message indicating admin area was target
            String loginUrl = httpRequest.getContextPath() + "/authenticate?action=showLogin&error=Admin+login+required";
            httpResponse.sendRedirect(loginUrl);
        }
    }

    @Override
    public void destroy() {
         System.out.println("AdminAuthFilter destroyed."); // Use Logger
    }
}