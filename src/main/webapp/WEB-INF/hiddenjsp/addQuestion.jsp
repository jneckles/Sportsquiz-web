<%-- /WEB-INF/views/admin/addQuestion.jsp --%>
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
    // Helper to safely get option text from a map, used for repopulating form on error
    private String getOptionFromMap(Map<String, String> map, String key) {
        return (map != null && map.get(key) != null) ? escapeHtml(map.get(key)) : "";
    }
%>

<%
    // Get data passed from AdminControllerServlet
    Integer quizId = (Integer) request.getAttribute("quizId");
    Question questionData = (Question) request.getAttribute("questionData"); // For repopulating form on error
    // If adding, questionData might be null or empty, create new if needed
    if (questionData == null) questionData = new Question(quizId);

    // Ensure required attributes are present
    if (quizId == null || quizId <= 0) {
         response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid Quiz ID for adding question.");
         return;
    }

    // Form action and page title usually set by controller
    String formAction = (String) request.getAttribute("formAction"); // Should be "saveQuestion"
    String pageTitle = (String) request.getAttribute("pageTitle"); // Should be "Add New Question"
    if (formAction == null) formAction = "saveQuestion"; // Default fallback
    if (pageTitle == null) pageTitle = "Add New Question";

    Object error = request.getAttribute("error"); // Get error message if forwarded back
    Object csrfToken = request.getAttribute("csrfToken"); // Get CSRF token

    String correctKey = questionData.getCorrectAnswerKey(); // Get correct key if repopulating

%>
<!DOCTYPE html>
<html>
<head>
    <title><%= escapeHtml(pageTitle) %></title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        form div { margin-bottom: 10px; }
        label { display: inline-block; min-width: 100px; }
        textarea { vertical-align: top; }
        fieldset { margin-top: 15px; margin-bottom: 15px; padding: 10px; border: 1px solid #ccc;}
        legend { font-weight: bold; }
        input[type="radio"] { margin-left: 10px; margin-right: 3px;}
    </style>
</head>
<body class="admin-page">

    <div class="container">
    <h1><%= escapeHtml(pageTitle) %> (for Quiz ID: <%= quizId %>)</h1>

    <% if (error != null) { %>
        <p class="error-message" style="color:red;"><%= escapeHtml(error) %></p>
    <% } %>

    <form method="POST" action="<%= request.getContextPath() %>/admin">
        <input type="hidden" name="action" value="<%= escapeHtml(formAction) %>">
        <input type="hidden" name="csrfToken" value="<%= escapeHtml(csrfToken) %>">
        <input type="hidden" name="quizId" value="<%= quizId %>"> <%-- Submit quizId back --%>

        <div>
            <label for="questionText">Question Text:</label><br>
            <textarea id="questionText" name="questionText" rows="3" cols="70" required><%= escapeHtml(questionData.getText()) %></textarea>
        </div>

        <fieldset><legend>Options (A, B, C, D)</legend>
             <div><label for="optionA">A:</label> <input type="text" id="optionA" name="optionA" value="<%= getOptionFromMap(questionData.getOptions(), "A") %>" size="60" required></div>
             <div><label for="optionB">B:</label> <input type="text" id="optionB" name="optionB" value="<%= getOptionFromMap(questionData.getOptions(), "B") %>" size="60" required></div>
             <div><label for="optionC">C:</label> <input type="text" id="optionC" name="optionC" value="<%= getOptionFromMap(questionData.getOptions(), "C") %>" size="60" required></div>
             <div><label for="optionD">D:</label> <input type="text" id="optionD" name="optionD" value="<%= getOptionFromMap(questionData.getOptions(), "D") %>" size="60" required></div>
        </fieldset>

        <div>
            <label>Correct Answer:</label>
            <%-- Radio buttons to select the correct key --%>
            <% String checkedA = "A".equals(correctKey) ? "checked" : ""; %>
            <% String checkedB = "B".equals(correctKey) ? "checked" : ""; %>
            <% String checkedC = "C".equals(correctKey) ? "checked" : ""; %>
            <% String checkedD = "D".equals(correctKey) ? "checked" : ""; %>
            <input type="radio" name="correctAnswerKey" value="A" id="correctA" <%= checkedA %> required><label for="correctA">A</label>
            <input type="radio" name="correctAnswerKey" value="B" id="correctB" <%= checkedB %> required><label for="correctB">B</label>
            <input type="radio" name="correctAnswerKey" value="C" id="correctC" <%= checkedC %> required><label for="correctC">C</label>
            <input type="radio" name="correctAnswerKey" value="D" id="correctD" <%= checkedD %> required><label for="correctD">D</label>
        </div>

        <div style="margin-top: 20px;">
            <button type="submit">Save New Question</button>
            <a href="<%= request.getContextPath() %>/admin?action=listQuestions&quizId=<%= quizId %>">Cancel</a>
        </div>
    </form>
    </div>
</body>
</html>