<%-- /WEB-INF/views/errors/error_403.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ page import="jakarta.servlet.http.*, java.util.*" %> <%-- Need ErrorData implicitly --%>

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
    // For 403, the most relevant info is usually the resource they tried to access
    Object requestUri = pageContext.getErrorData().getRequestURI();

    // Get logged-in user if available (might be null if error happened before login)
    Object loggedInUser = session != null ? session.getAttribute("loggedInUser") : null;
%>

<!DOCTYPE html>
<html>
<head>
    <title>Error 403 - Access Forbidden</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        body { padding: 20px; }
        .error-container { border: 1px solid #dc3545; background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; }
        code { background-color: #e9ecef; padding: 2px 4px; border-radius: 3px; }
    </style>
</head>
<body>
    <div class="error-container">
        <h1>Access Denied (Error 403)</h1>
        <p>
            Sorry<% if (loggedInUser != null) { %>, <%= escapeHtml(loggedInUser) %><% } %>,
            you do not have permission to access the requested resource.
        </p>
        <p>
            Resource attempted: <code><%= escapeHtml(requestUri) %></code>
        </p>
        <p>
            This usually means you are trying to access an area restricted to administrators
            or you lack the necessary privileges for this action.
        </p>
    </div>
    <hr>
    <p>
        <%-- Provide appropriate links based on whether user is logged in --%>
        <% if (loggedInUser != null) { %>
            <a href="<%= request.getContextPath() %>/quiz?action=select">Go to Quiz Selection</a> |
            <a href="<%= request.getContextPath() %>/auth?action=logout">Logout</a>
        <% } else { %>
            <a href="<%= request.getContextPath() %>/auth?action=showLogin">Go to Login Page</a>
        <% } %>
    </p>
</body>
</html>
