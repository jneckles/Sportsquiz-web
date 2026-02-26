package edu.skidmore.cs276.lab04.test; 

// JUnit 4 Imports
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*; // Only if needed, verify is primary

// Mockito Imports
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner; // Use runner to initialize mocks

import edu.skidmore.cs276.lab04.webapps.web.AuthenticationFilter;

import static org.mockito.Mockito.*; // Static methods like when, verify

// Jakarta Servlet API Imports (Needed for mocking)
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig; // Needed for init/destroy signature if implemented fully
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Unit tests for AuthenticationFilter (JUnit 4 + Mockito).
 */
@RunWith(MockitoJUnitRunner.class) // Initialize mocks declared with @Mock
public class AuthenticationFilterTest { // Public class

    @Mock // Mock objects provided by the container
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpSession mockSession;
    @Mock
    private FilterChain mockChain;
    // @Mock FilterConfig mockConfig; // If testing init

    private AuthenticationFilter filter; // The instance we are testing

    @Before // Public setup method
    public void setUp() {
        // Create a new instance of the filter before each test
        filter = new AuthenticationFilter();
        // Optional: Call init if it has logic, requires mockConfig
        // try { filter.init(mockConfig); } catch (ServletException e) { fail("init failed"); }

        // Common mock setup: Need context path for redirects
        when(mockRequest.getContextPath()).thenReturn("/Project3"); // Simulate context path
    }

    @Test // Public test method
    public void doFilter_UserLoggedIn_ShouldProceed() throws IOException, ServletException {
        // Arrange: Simulate a logged-in user session
        when(mockRequest.getSession(false)).thenReturn(mockSession); // Session exists
        when(mockSession.getAttribute("loggedInUser")).thenReturn("testuser"); // User attribute is present

        // Act: Run the filter's doFilter method
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert: Verify that the request was allowed to continue down the chain
        verify(mockChain, times(1)).doFilter(mockRequest, mockResponse);
        // Verify that no redirect happened
        verify(mockResponse, never()).sendRedirect(anyString());
        verify(mockResponse, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void doFilter_UserNotLoggedIn_NoSession_ShouldRedirectToLogin() throws IOException, ServletException {
        // Arrange: Simulate no session existing
        when(mockRequest.getSession(false)).thenReturn(null); // No session

        // Define expected redirect URL
        String expectedLoginUrl = "/Project3/authenticate?action=showLogin&error=Login+required";

        // Act: Run the filter
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert: Verify that the chain was NOT called and redirect happened
        verify(mockChain, never()).doFilter(mockRequest, mockResponse);
        verify(mockResponse, times(1)).sendRedirect(expectedLoginUrl); // Check redirect URL
        verify(mockResponse, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void doFilter_UserNotLoggedIn_SessionExistsButNoAttribute_ShouldRedirectToLogin() throws IOException, ServletException {
        // Arrange: Simulate session exists, but user attribute is missing
        when(mockRequest.getSession(false)).thenReturn(mockSession); // Session exists
        when(mockSession.getAttribute("loggedInUser")).thenReturn(null); // User attribute is null

        // Define expected redirect URL
        String expectedLoginUrl = "/Project3/authenticate?action=showLogin&error=Login+required";

        // Act: Run the filter
        filter.doFilter(mockRequest, mockResponse, mockChain);

        // Assert: Verify chain was NOT called and redirect happened
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
            // No exceptions expected for simple init/destroy
        } catch (Exception e) {
            fail("init or destroy threw exception: " + e.getMessage());
        }
    }
}