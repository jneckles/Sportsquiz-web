<%-- /WEB-INF/views/errors/error_generic.jsp --%>
<%-- Can be used for 500 errors and as a fallback for java.lang.Throwable --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ page import="jakarta.servlet.http.*, java.util.*, java.io.PrintWriter" %> <%-- Need ErrorData implicitly, PrintWriter for stack trace --%>

<%!
    // Basic HTML escaping utility function
    private String escapeHtml(Object input) {
        if (input == null) return "";
        String text = input.toString();
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
%>

<%
    // Access ErrorData implicitly available via pageContext in error pages
    jakarta.servlet.ErrorData errorData = pageContext.getErrorData();

    // Extract relevant information (handle potential nulls)
    Integer statusCode = errorData.getStatusCode();
    String requestUri = errorData.getRequestURI();
    Throwable throwable = errorData.getThrowable();
    String exceptionMessage = (throwable != null) ? throwable.getMessage() : "N/A";
    String exceptionType = (throwable != null) ? throwable.getClass().getName() : "N/A";

    // Get custom error message if set by a servlet before forwarding/throwing
    Object customErrorMessage = request.getAttribute("errorMessage");

    // Get logged-in user if available
    Object loggedInUser = session != null ? session.getAttribute("loggedInUser") : null;

    // Flag to show detailed debug info (set to false for production)
    boolean showDebugInfo = "localhost".equals(request.getServerName()) || "127.0.0.1".equals(request.getServerName());

%>

<!DOCTYPE html>
<html>
<head>
    <title>Application Error</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
     <style>
        body { padding: 20px; }
        .error-container { border: 1px solid #ffc107; background-color: #fff3cd; color: #856404; padding: 15px; border-radius: 5px; }
        code { background-color: #e9ecef; padding: 2px 4px; border-radius: 3px; }
        pre { background-color: #f8f9fa; border: 1px solid #dee2e6; padding: 10px; overflow-x: auto; }
    </style>
</head>
<body>
    <div class="error-container">
        <h1>Application Error</h1>
        <p>Sorry, the application encountered an unexpected problem while processing your request.</p>
        <p>Please try again later, or contact support if the problem persists.</p>

        <hr>
        <p><b>Details:</b></p>
        <ul>
            <li>Status Code: <code><%= statusCode != null ? statusCode : "N/A" %></code></li>
            <li>Requested URI: <code><%= escapeHtml(requestUri) %></code></li>
            <% if (customErrorMessage != null) { %>
                 <li>Message: <code><%= escapeHtml(customErrorMessage) %></code></li>
            <% } else { %>
                 <li>Error Message: <code><%= escapeHtml(exceptionMessage) %></code></li>
            <% } %>
        </ul>

        <%-- Optional: Show more details during development --%>
        <% if (showDebugInfo && throwable != null) { %>
            <hr/>
            <h3>Debug Information (Development Only)</h3>
            <p>Exception Type: <code><%= escapeHtml(exceptionType) %></code></p>
            <p>Stack Trace:</p>
            <pre><% throwable.printStackTrace(new PrintWriter(out)); %></pre>
        <% } %>

    </div>
    <hr>
     <p>
        <% if (loggedInUser != null) { %>
            <a href="<%= request.getContextPath() %>/quiz?action=select">Go to Quiz Selection</a> |
            <a href="<%= request.getContextPath() %>/auth?action=logout">Logout</a>
        <% } else { %>
            <a href="<%= request.getContextPath() %>/auth?action=showLogin">Go to Login Page</a>
        <% } %>
    </p>
</body>
</html>
