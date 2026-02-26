<%-- /WEB-INF/views/admin/listQuestions.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%-- Adjust package to match your Question model location --%>
<%@ page import="edu.skidmore.cs276.lab04.beans.tasklist.Question" %>

<%!
    // Basic HTML escaping utility function
    private String escapeHtml(Object input) {
        if (input == null) return ""; String text = input.toString();
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>

<%
    // Get data passed from AdminControllerServlet
    Object questionListObj = request.getAttribute("questionList");
    List<Question> questionList = null;
    if (questionListObj instanceof List) {
        @SuppressWarnings("unchecked")
        List<Question> tempList = (List<Question>) questionListObj;
        questionList = tempList;
    }

    Object quizIdObj = request.getAttribute("quizId");
    Integer quizId = null;
    if (quizIdObj instanceof Integer) {
        quizId = (Integer) quizIdObj;
    } else if (quizIdObj != null) {
        // Try parsing if it was passed as string maybe? Defensive coding.
        try { quizId = Integer.valueOf(quizIdObj.toString()); } catch (NumberFormatException e) { /* ignore */ }
    }

    // String quizTitle = (String) request.getAttribute("quizTitle"); // Optional: Get title if passed

    // Get feedback messages from redirects
    Object successMsg = request.getParameter("successMsg");
    Object errorMsg = request.getParameter("errorMsg");

    // Get CSRF token needed for delete forms
    Object csrfToken = request.getAttribute("csrfToken");

    // Basic check if quizId is valid
    if (quizId == null || quizId <= 0) {
        // Redirect to dashboard or show error if quizId is missing/invalid
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard&errorMsg=Invalid+Quiz+ID+provided");
        return;
    }

%>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Questions for Quiz <%= quizId %></title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        /* Style for inline form used for delete button */
        form.inline-form {
            display: inline;
            margin: 0;
            padding: 0;
        }
        form.inline-form button {
            padding: 2px 5px; /* Smaller button */
            font-size: 0.9em;
            background-color: #dc3545; /* Red for delete */
        }
         form.inline-form button:hover {
             background-color: #c82333;
         }
    </style>
</head>
<body class="admin-page">

    <div class="container">
    <h1>Manage Questions for Quiz ID: <%= quizId %> <%-- (Title: <%= escapeHtml(quizTitle) %>) --%></h1>
     <p>
        <a href="<%= request.getContextPath() %>/admin?action=dashboard">Back to Quiz List</a> |
        <a href="<%= request.getContextPath() %>/authenticate?action=logout">Logout</a>
    </p>

    <%-- Display feedback messages --%>
    <% if (successMsg != null) { %><p style="color:green;"><%= escapeHtml(successMsg) %></p><% } %>
    <% if (errorMsg != null) { %><p class="error-message"><%= escapeHtml(errorMsg) %></p><% } %>

    <%-- Link to Add Question Form --%>
    <p><a href="<%= request.getContextPath() %>/admin?action=showAddQuestion&quizId=<%= quizId %>">Add New Question to this Quiz</a></p>

    <h2>Existing Questions:</h2>
    <table border="1" style="width: 100%; border-collapse: collapse;">
         <thead>
            <tr>
                <th style="padding: 8px;">ID</th>
                <th style="text-align: left; padding: 8px;">Question Text</th>
                <th style="padding: 8px;">Correct Key</th>
                <th style="padding: 8px;">Actions</th>
            </tr>
         </thead>
         <tbody>
          <% if (questionList != null && !questionList.isEmpty()) {
               for (Question q : questionList) { %>
                <tr>
                    <td style="padding: 8px; text-align: center;"><%= q.getId() %></td>
                    <td style="padding: 8px;"><%= escapeHtml(q.getText()) %></td>
                    <td style="padding: 8px; text-align: center;"><%= escapeHtml(q.getCorrectAnswerKey()) %></td>
                    <td style="padding: 8px; text-align: center;">
                        <%-- Delete Question Form (POST) --%>
                        <form class="inline-form" method="POST" action="<%= request.getContextPath() %>/admin" onsubmit="return confirm('Are you sure you want to delete question ID <%= q.getId() %>?');">
                            <input type="hidden" name="action" value="deleteQuestion">
                            <input type="hidden" name="questionId" value="<%= q.getId() %>">
                            <input type="hidden" name="quizId" value="<%= quizId %>"> <%-- Pass quizId to allow redirect back --%>
                            <input type="hidden" name="csrfToken" value="<%= escapeHtml(csrfToken) %>">
                            <button type="submit">Delete</button>
                        </form>
                         <%-- Edit Link (Removed for simplicity) --%>
                         <%-- <a href="/admin?action=showEditQuestion&questionId=<%= q.getId() %>">Edit</a> --%>
                    </td>
                </tr>
          <%   } // End for loop
             } else { %>
              <tr><td colspan="4" style="padding: 8px;">No questions found for this quiz.</td></tr>
          <% } // End if else %>
          </tbody>
    </table>
    </div>
</body>
</html>