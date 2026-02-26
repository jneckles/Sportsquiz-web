<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ page import="jakarta.servlet.http.*, java.util.*" %> 

<%! // Declaration block for escapeHtml function
    private String escapeHtml(Object input) { if (input == null) return ""; String text = input.toString(); return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;"); }
%>
<%
    // Access ErrorData implicitly available via pageContext in error pages
    Object requestUri = pageContext.getErrorData().getRequestURI();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Error 404 - Not Found</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <h1>Oops! Page Not Found (404)</h1>
    <p>Sorry, the resource you requested could not be found.</p>
    <p>Requested URI: <code><%= escapeHtml(requestUri) %></code></p>
    <hr>
    <p><a href="<%= request.getContextPath() %>/authenticate?action=logout">Logout</a></p>
</body>
</html>