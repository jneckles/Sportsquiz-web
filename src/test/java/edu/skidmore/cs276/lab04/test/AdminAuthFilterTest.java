package edu.skidmore.cs276.lab04.test;

//JUnit 4 Imports
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*; // Only if needed

//Mockito Imports
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

//Jakarta Servlet API Imports
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import edu.skidmore.cs276.lab04.webapps.web.AdminAuthFilter;
//Import Roles if using the interface, otherwise use String literals
import edu.skidmore.cs276.lab04.webapps.web.Roles; // Adjust package

import java.io.IOException;

/**
* Unit tests for AdminAuthFilter (JUnit 4 + Mockito).
*/
@RunWith(MockitoJUnitRunner.class)
public class AdminAuthFilterTest { // Public class

 @Mock
 private HttpServletRequest mockRequest;
 @Mock
 private HttpServletResponse mockResponse;
 @Mock
 private HttpSession mockSession;
 @Mock
 private FilterChain mockChain;

 private AdminAuthFilter filter;

 // Use constants for roles for consistency
 private final String ADMIN_ROLE = Roles.ADMIN; // Or "ADMIN" literal
 private final String USER_ROLE = Roles.USER;   // Or "USER" literal

 @Before // Public setup method
 public void setUp() {
     filter = new AdminAuthFilter();
     // Mock context path for potential redirects
     when(mockRequest.getContextPath()).thenReturn("/Project3");
     // Mock getRequestURI for logging inside filter (optional but good)
     when(mockRequest.getRequestURI()).thenReturn("/Project3/admin/somepage");
 }

 @Test // Public test method
 public void userIsAdmin() throws IOException, ServletException {
     // Arrange: Simulate logged-in Admin user
     when(mockRequest.getSession(false)).thenReturn(mockSession);
     when(mockSession.getAttribute("loggedInUser")).thenReturn("admin");
     when(mockSession.getAttribute("userRole")).thenReturn(ADMIN_ROLE); // Use ADMIN role

     // Act
     filter.doFilter(mockRequest, mockResponse, mockChain);

     // Assert: Chain proceeds, no redirect, no error
     verify(mockChain, times(1)).doFilter(mockRequest, mockResponse);
     verify(mockResponse, never()).sendRedirect(anyString());
     verify(mockResponse, never()).sendError(anyInt(), anyString());
 }

 @Test
 public void userLoggedInNotAdmin() throws IOException, ServletException {
     // Arrange: Simulate logged-in regular user
     when(mockRequest.getSession(false)).thenReturn(mockSession);
     when(mockSession.getAttribute("loggedInUser")).thenReturn("testuser");
     when(mockSession.getAttribute("userRole")).thenReturn(USER_ROLE); // Use USER role

     // Act
     filter.doFilter(mockRequest, mockResponse, mockChain);

     // Assert: Chain does NOT proceed, no redirect, 403 error sent
     verify(mockChain, never()).doFilter(mockRequest, mockResponse);
     verify(mockResponse, never()).sendRedirect(anyString());
     verify(mockResponse, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString()); // Check for 403
 }

 @Test
 public void userLoggedInRoleIsNull() throws IOException, ServletException {
     // Arrange: Simulate logged-in user but role attribute is null
     when(mockRequest.getSession(false)).thenReturn(mockSession);
     when(mockSession.getAttribute("loggedInUser")).thenReturn("testuser");
     when(mockSession.getAttribute("userRole")).thenReturn(null); // Role is null

     // Act
     filter.doFilter(mockRequest, mockResponse, mockChain);

     // Assert: Chain does NOT proceed, no redirect, 403 error sent
     verify(mockChain, never()).doFilter(mockRequest, mockResponse);
     verify(mockResponse, never()).sendRedirect(anyString());
     verify(mockResponse, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString()); // Check for 403
 }

  @Test
 public void userLoggedInRoleIsNotString() throws IOException, ServletException {
     // Arrange: Simulate logged-in user but role attribute is wrong type
     when(mockRequest.getSession(false)).thenReturn(mockSession);
     when(mockSession.getAttribute("loggedInUser")).thenReturn("testuser");
     when(mockSession.getAttribute("userRole")).thenReturn(new Object()); // Role is wrong type

     // Act
     filter.doFilter(mockRequest, mockResponse, mockChain);

     // Assert: Chain does NOT proceed, no redirect, 403 error sent
     verify(mockChain, never()).doFilter(mockRequest, mockResponse);
     verify(mockResponse, never()).sendRedirect(anyString());
     verify(mockResponse, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString()); // Check for 403
 }


 @Test
 public void userNotLoggedInNoSession() throws IOException, ServletException {
     // Arrange: Simulate no session
     when(mockRequest.getSession(false)).thenReturn(null);

     // Define expected redirect URL
     String expectedLoginUrl = "/Project3/auth?action=showLogin&error=Admin+login+required"; // URL defined in filter

     // Act
     filter.doFilter(mockRequest, mockResponse, mockChain);

     // Assert: Chain does NOT proceed, 403 error not sent, redirect to login happened
     verify(mockChain, never()).doFilter(mockRequest, mockResponse);
     verify(mockResponse, times(1)).sendRedirect(expectedLoginUrl);
     verify(mockResponse, never()).sendError(anyInt(), anyString());
 }

  @Test
 public void userNotLoggedInSessionExists() throws IOException, ServletException {
     // Arrange: Simulate session exists, but user attribute is missing
     when(mockRequest.getSession(false)).thenReturn(mockSession);
     when(mockSession.getAttribute("loggedInUser")).thenReturn(null); // No loggedInUser attribute
     // Role attribute doesn't matter if loggedInUser is null

     // Define expected redirect URL
     String expectedLoginUrl = "/Project3/auth?action=showLogin&error=Admin+login+required";

     // Act
     filter.doFilter(mockRequest, mockResponse, mockChain);

     // Assert: Chain does NOT proceed, 403 error not sent, redirect to login happened
     verify(mockChain, never()).doFilter(mockRequest, mockResponse);
     verify(mockResponse, times(1)).sendRedirect(expectedLoginUrl);
     verify(mockResponse, never()).sendError(anyInt(), anyString());
 }

 // Optional: Test init and destroy if they have logic
 @Test
 public void testInitAndDestroy() {
     try {
         filter.init(null); // Pass null or mockConfig if needed
         filter.destroy();
         // No exceptions expected
     } catch (Exception e) {
         fail("init or destroy threw exception: " + e.getMessage());
     }
 }
}
