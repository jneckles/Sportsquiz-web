<%-- /WEB-INF/views/selectQuiz.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %> <%-- Need List, String etc. --%>

<%! // Declaration block for escapeHtml function
    private String escapeHtml(Object input) {
        if (input == null) return ""; String text = input.toString();
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>

<%
    // Get Logged in User Info from Session
    Object userObj = session.getAttribute("loggedInUser");
    Object roleObj = session.getAttribute("userRole"); // Get the role stored by AuthControllerServlet

    String username = (userObj instanceof String) ? (String) userObj : "Guest"; // Default if session issue

    // Check if the user has the ADMIN role
    // String adminRoleName = Roles.ADMIN; // Use if Roles interface is imported
    String adminRoleName = "ADMIN"; // Use string literal if Roles interface not imported/created
    boolean isAdmin = false;
    if (roleObj instanceof String) {
        if (adminRoleName.equals((String) roleObj)) {
            isAdmin = true;
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Select Your Quiz</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%-- Display welcome message using session attribute, escape username --%>
    <h2>Welcome, <%= escapeHtml(session.getAttribute("loggedInUser")) %>! :)</h2>

    <%-- Display error message if set --%>
    <%
        Object error = request.getAttribute("error");
        if (error != null) {
    %>
         <p class="error-message" style="color: red;">
             Error: <%= escapeHtml(error) %>
         </p>
    <%
        }
    %>

    <p>Please choose a quiz category:</p>
    <ul>
        <%-- Loop through categories --%>
        <%
            // Get categories from request attribute, cast, and check for null
            Object categoriesObj = request.getAttribute("categories");
            List<String> categories = null;
            if (categoriesObj instanceof List) {
                 // Unchecked cast, ensure controller always sets List<String>
                 @SuppressWarnings("unchecked")
                 List<String> tempList = (List<String>) categoriesObj;
                 categories = tempList;
            }

            if (categories != null && !categories.isEmpty()) {
                for (String category : categories) {
                    // Construct URL manually for each category link
                    String quizUrl = request.getContextPath() + "/quizme?action=start&category=" + java.net.URLEncoder.encode(category, "UTF-8");
        %>
                    <li>
                        <a href="<%= quizUrl %>">
                            <%= escapeHtml(category) %> Quiz <%-- Escape category name --%>
                        </a>
                    </li>
        <%
                } // end for loop
            } else {
        %>
                <li>No quizzes available at the moment.</li>
        <%
            } // end if categories not null
        %>
    </ul>

    <hr>
    <%-- URL for logout link --%>
    <p><a href="<%= request.getContextPath() %>/authenticate?action=logout">Logout</a>
        <%-- ****** START: Conditional Admin Link ****** --%>
        <%
            if (isAdmin) {
                // If the user has the ADMIN role, show the link to the admin dashboard
                String adminUrl = request.getContextPath() + "/admin?action=dashboard";
        %>
                <%-- Add a separator --%>
                | <a href="<%= adminUrl %>">Admin Dashboard</a>
        <%
            } // End if isAdmin
        %>
        <%-- ****** END: Conditional Admin Link ****** --%>

    </p>
</body>
</html>