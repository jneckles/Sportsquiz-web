%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ page import="jakarta.servlet.http.*, java.util.*" %> <%-- Need ErrorData access implicitly --%>

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
	<title>Error 401 - Unauthorized</title>
	<%-- Link to context path --%>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
	<h1>Authentication Required (401)</h1>
	<p>
		Sorry, but you must be logged in to access the requested resource.
		Please login with valid credentials.
	</p>
	<p>
		<%-- Display requested resource path --%>
		Resource attempted: <code><%= escapeHtml(requestUri) %></code>
	</p>
	<hr>
	<%-- Give a link back to the login --%>
	<p>
		<a href="<%= request.getContextPath() %>/authenticate?action=showLogin">Go to the Login Page</a>
		
</body>