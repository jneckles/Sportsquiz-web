<%-- /WEB-INF/views/quizPage.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="edu.skidmore.cs276.lab04.beans.tasklist.*" %> <%-- Adjust package for your models --%>
<%@ page import="java.util.*" %>

<%! // Declaration block for escapeHtml function
    private String escapeHtml(Object input) {
        if (input == null) return ""; String text = input.toString();
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>

<%
    // Get Quiz object directly from Session
    Object quizObj = session.getAttribute("currentQuiz");
    SportsQuiz currentQuiz = null; // Use correct class name (Quiz or SportsQuiz)
    if (quizObj instanceof SportsQuiz) { // Use correct class name
       currentQuiz = (SportsQuiz) quizObj; // Use correct class name
    }

    // Retrieve CSRF token from request (still passed via request from controller)
    Object csrfToken = request.getAttribute("csrfToken");

    Object categoryObj = request.getAttribute("pageCategory");
    String pageCategoryClass = "default-quiz"; // Fallback class
    if (categoryObj instanceof String && !((String)categoryObj).trim().isEmpty()) {
        pageCategoryClass = escapeHtml(categoryObj); // Use category name as class, escape it
    }

    // Check if essential data is missing
    if (currentQuiz == null || currentQuiz.getQuestions() == null || currentQuiz.getQuestions().isEmpty()) {
        // Handle errors
        response.sendRedirect(request.getContextPath() + "/quizme?action=select&error=QuizDataMissingOrEmpty");
        return; // Stop processing JSP
    }

    String quizTitle = currentQuiz.getTitle();
    List<Question> questions = currentQuiz.getQuestions();
    int totalQuestions = questions.size();
    int questionCounter = 0; // For numbering questions

%>

<!DOCTYPE html>
<html>
<head>
    <title>Quiz - <%= escapeHtml(quizTitle) %></title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="<%= pageCategoryClass %>">

    <div class="container">

    <h1><%= escapeHtml(quizTitle) %></h1>
    <p>Please answer all <%= totalQuestions %> questions.</p>

    <form method="POST" action="<%= request.getContextPath() %>/quizme">
        <input type="hidden" name="action" value="submit">
        <input type="hidden" name="csrfToken" value="<%= escapeHtml(csrfToken) %>">

        <%-- Loop through ALL questions in the quiz --%>
        <%
            for (Question question : questions) {
                questionCounter++;
                int questionId = question.getId();
                String questionText = question.getText();
                Map<String, String> options = question.getOptions();
        %>
                <fieldset style="margin-bottom: 20px; border: 1px solid #ccc; padding: 10px;">
                    <legend>Question <%= questionCounter %> of <%= totalQuestions %></legend>
                    <p><%= escapeHtml(questionText) %></p>

                    <%-- Loop through options for the current question --%>
                    <%
                        if (options != null && !options.isEmpty()) {
                            for (Map.Entry<String, String> optionEntry : options.entrySet()) {
                                String key = optionEntry.getKey();
                                String value = optionEntry.getValue();
                                String optionInputId = "opt_" + questionId + "_" + key;
                    %>
                                <div>
                                    <input type="radio"
                                           name="q_<%= questionId %>" <%-- Name based on question ID --%>
                                           value="<%= escapeHtml(key) %>"
                                           id="<%= optionInputId %>"
                                           required> <%-- Mark each question as required --%>
                                    <label for="<%= optionInputId %>">
                                        <%= escapeHtml(key) %>. <%= escapeHtml(value) %>
                                    </label>
                                </div>
                    <%
                            } // end options loop
                        } else {
                    %>
                        <p>No options available for this question.</p>
                    <%
                        } // end if options not null
                    %>
                </fieldset>
        <%
            } // end questions loop
        %>

        <%-- Submit button appears only ONCE after all questions --%>
        <div style="margin-top: 20px;">
            <button type="submit">Submit All Answers</button>
        </div>

    </form>
    </div>
</body>
</html>