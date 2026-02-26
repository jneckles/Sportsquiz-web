<%-- /WEB-INF/views/results.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="edu.skidmore.cs276.lab04.beans.tasklist.Score" %> <%-- Adjust package for Score model --%>
<%@ page import="java.text.NumberFormat" %> <%-- Import for formatting percentage --%>
<%@ page import="java.util.*" %>

<%! // Declaration block for escapeHtml function
    private String escapeHtml(Object input) {
        if (input == null) return ""; String text = input.toString();
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>

<%
    // Retrieve data from request attributes
    Object scoreObj = request.getAttribute("score");
    Object quizTitleObj = request.getAttribute("quizTitle");

    Score score = null;
    if (scoreObj instanceof Score) {
        score = (Score) scoreObj;
    }

    String quizTitle = (quizTitleObj != null) ? quizTitleObj.toString() : "Quiz";

    // Get logged-in user from session
    Object userObj = session.getAttribute("loggedInUser");

    // Format percentage
    String formattedPercentage = "N/A";
    if (score != null) {
        // Use method from Score bean 
        formattedPercentage = score.getFormattedPercentage();
        // OR calculate manually here
        //double percentage = score.getPercentage(); // Assumes getPercentage returns 0-100 value
        //NumberFormat percentFormat = NumberFormat.getPercentInstance();
        //percentFormat.setMaximumFractionDigits(1);
        //formattedPercentage = percentFormat.format(percentage / 100.0); // Format expects 0.0-1.0
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Quiz Results</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <h2>Quiz Results: <%= escapeHtml(quizTitle) %></h2>

    <% if (score != null) { %>
        <p>Congratulations, <%= escapeHtml(userObj) %>!</p>
        <p>You answered correctly: <strong><%= score.getCorrectAnswers() %></strong></p>
        <p>Total questions: <strong><%= score.getTotalQuestions() %></strong></p>
        <p>Your score:
            <strong><%= formattedPercentage %></strong>
        </p>
    <% } else { %>
        <p>Sorry, your score could not be calculated.</p>
    <% } %>

    <hr>
    <p><a href="<%= request.getContextPath() %>/quizme?action=select">Take Another Quiz</a></p>
    <p><a href="<%= request.getContextPath() %>/authenticate?action=logout">Logout</a></p>

</body>
</html>