<%-- /WEB-INF/views/login.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>

<%! // Declaration block for escapeHtml function
    private String escapeHtml(Object input) {
        if (input == null) return ""; String text = input.toString();
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Quiz Login</title>
    <%-- Manual URL construction for CSS --%>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="login-page">

    <div class="login-container">

    <h2>Sports Quiz Login!</h2>

    <%-- Display login error message --%>
    <%
        Object loginError = request.getAttribute("loginError");
        if (loginError != null) {
    %>
            <p class="error-message" style="color: red;">
                <%-- Manually escape the error message --%>
                Error: <%= escapeHtml(loginError) %>
            </p>
    <%
        }
    %>

    <%-- URL construction for form action --%>
    <form method="POST" action="<%= request.getContextPath() %>/authenticate">
        <input type="hidden" name="action" value="login">

        <%-- CSRF Token: Value retrieved from request attribute and escaped --%>
        <% Object csrfToken = request.getAttribute("csrfToken"); %>
        <input type="hidden" name="csrfToken" value="<%= escapeHtml(csrfToken) %>">

        <div>
            <label for="username">Username:</label>
            <%-- Retain username on error, escape it --%>
            <input type="text" id="username" name="username" value="<%= escapeHtml(request.getParameter("username")) %>" required>
        </div>

        <div>
             <label for="password">Password:</label>
             <input type="password" id="password" name="password" required>
        </div>
        <div>
             <p><small>(Password is your username reversed, followed by the number 2. E.g., 'test' -> 'tset2')</small></p>
        </div>

        <div>
            <button type="submit">Login</button>
        </div>
    </form>
    </div>
</body>
</html>