
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%-- Adjust package to match your SportsQuiz/QuizSummary model location --%>
<%@ page import="edu.skidmore.cs276.lab04.beans.tasklist.SportsQuiz" %>



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
    // Get data passed from AdminControllerServlet
    // Assuming the attribute contains List<SportsQuiz> with basic info
    Object quizListObj = request.getAttribute("quizList");
    List<SportsQuiz> quizList = null; // Use SportsQuiz or QuizSummary based on what service returns
    if (quizListObj instanceof List) {
         // Ensure type safety if possible, suppress warning if confident
         @SuppressWarnings("unchecked")
         List<SportsQuiz> tempList = (List<SportsQuiz>) quizListObj;
         quizList = tempList;
    }

    // Get feedback messages from redirects (passed as parameters)
    Object successMsg = request.getParameter("successMsg");
    Object errorMsg = request.getParameter("errorMsg");

%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard - Quizzes</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="admin-page">

    <div class="container">
    <h1>Admin Dashboard - Manage Quiz Questions</h1>
    <p>
        Welcome, <%= escapeHtml(session.getAttribute("loggedInUser")) %>! |
        <a href="<%= request.getContextPath() %>/quizme?action=select">View Quizzes (User View)</a> |
        <a href="<%= request.getContextPath() %>/authenticate?action=logout">Logout</a>
    </p>

    <%-- Display feedback messages --%>
    <% if (successMsg != null) { %>
        <p style="color:green; border: 1px solid green; padding: 5px;"><%= escapeHtml(successMsg) %></p>
    <% } %>
    <% if (errorMsg != null) { %>
        <p class="error-message" style="color:red; border: 1px solid red; padding: 5px;"><%= escapeHtml(errorMsg) %></p>
    <% } %>

    <h2>Select Quiz to Manage Questions:</h2>
    <table border="1" style="width: 80%; border-collapse: collapse;">
        <thead>
            <tr>
                <th style="text-align: left; padding: 8px;">Category</th>
                <th style="text-align: left; padding: 8px;">Title</th>
                <th style="text-align: left; padding: 8px;">Actions</th>
            </tr>
        </thead>
        <tbody>
            <% if (quizList != null && !quizList.isEmpty()) {
                 for (SportsQuiz quiz : quizList) { // Loop through SportsQuiz (or QuizSummary)
            %>
                 <tr>
                     <td style="padding: 8px;"><%= escapeHtml(quiz.getCategory()) %></td>
                     <td style="padding: 8px;"><%= escapeHtml(quiz.getTitle()) %></td>
                     <td style="padding: 8px;">
                         <%-- Link to view/manage questions for this specific quiz --%>
                         <a href="<%= request.getContextPath() %>/admin?action=listQuestions&quizId=<%= quiz.getId() %>">View/Manage Questions</a>
                         <%-- Add/Edit/Delete Quiz functionality removed as per simplified requirements --%>
                     </td>
                 </tr>
            <%   } // End for loop
               } else { %>
                 <tr><td colspan="3" style="padding: 8px;">No quizzes found in the database.</td></tr>
            <% } // End if else %>
        </tbody>
    </table>
    </div>
</body>
</html>