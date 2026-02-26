package edu.skidmore.cs276.lab04.webapps.web; // Adjust package

// Imports for jakarta.servlet, models, service, util classes...
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import edu.skidmore.cs276.lab04.beans.tasklist.*; // Models (Question, Score, SportsQuiz, QuizSummary)
import org.apache.log4j.Logger;
import org.mortbay.log.Log;

/**
 * Servlet to handle administrative actions for managing quiz questions.
 * Allows viewing quizzes, viewing questions, adding questions, deleting questions.
 * Protected by AdminAuthFilter.
 */

public class AdminControllerServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(AdminControllerServlet.class);

    private static final long serialVersionUID = 1L;
	private QuizService quizService;
    private static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";
    private static final String CSRF_TOKEN_REQUEST_PARAM = "csrfToken";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        this.quizService = (QuizService) context.getAttribute("quizService");
        if (this.quizService == null) {
            throw new ServletException("QuizService not available in AdminControllerServlet.");
        }
        System.out.println("AdminControllerServlet Initialized."); // Use Logger
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // CSRF Check for all POST actions
        if (!isValidCsrfToken(request.getSession(false), request.getParameter(CSRF_TOKEN_REQUEST_PARAM))) {
        	LOG.info("Invalid Csrf request");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request (CSRF check failed / Session expired).");
            return;
        }
        LOG.info("Request handled well");
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        action = (action == null || action.trim().isEmpty()) ? "dashboard" : action.trim();

        // Base path for admin JSPs (adjust if needed)
        String viewPath = "/WEB-INF/hiddenjsp/dashboard.jsp";
        HttpSession session = request.getSession();

        try {
            // Set CSRF token for pages that might initiate POST requests
            setCsrfToken(request, session);

            switch (action) {
            case "dashboard":
                List<SportsQuiz> quizList = quizService.getAvailableQuizzesBasicInfo(); // Get List<SportsQuiz>
                request.setAttribute("quizList", quizList); // Set the attribute
                viewPath = "/WEB-INF/hiddenjsp/dashboard.jsp";
                forwardTo(request, response, viewPath);
                break;

                 case "listQuestions":
                     // List questions for a specific quiz
                     handleListQuestions(request, response); // Separate method handles forward
                     break;

                 case "showAddQuestion":
                      // Show the form to add a question to a specific quiz
                     handleShowAddQuestion(request, response); // Separate method handles forward
                     break;

                 case "saveQuestion": // POST only
                     if (!"POST".equalsIgnoreCase(request.getMethod())) { response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED); return; }
                     handleSaveQuestion(request, response, session); // Separate method handles forward or redirect
                     break;

                 case "deleteQuestion": // POST recommended
                    if (!"POST".equalsIgnoreCase(request.getMethod())) { response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED); return; }
                    handleDeleteQuestion(request, response); // Separate method handles redirect
                    break;

                default:
                    LOG.info("Admin action not recognized: " + action); // Use Logger
                    response.sendRedirect(request.getContextPath() + "/admin?action=dashboard");
                    break; // Break needed here
            }

        } catch (Exception e) {
            LOG.error("Error in AdminControllerServlet action=" + action + ": " + e.getMessage()); 
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred in the admin section: " + e.getMessage());
            // Forward to a generic error page (ensure path is correct)
            request.getRequestDispatcher("/WEB-INF/views/errors/error_generic.jsp").forward(request, response);
        }
    }

    

    private void handleListQuestions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         String viewPath = "/WEB-INF/hiddenjsp/listQuestions.jsp"; 
         try {
            int quizId = Integer.parseInt(request.getParameter("quizId"));
            request.setAttribute("quizId", quizId); // Pass quizId for add link

            List<Question> questions = quizService.getQuestionsForQuiz(quizId);
            request.setAttribute("questionList", questions);
            forwardTo(request, response, viewPath);
         } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or missing Quiz ID for listing questions.");
         } catch (Exception e) {
             LOG.trace("Error listing questions: " + e.getMessage()); // Use Log later
             request.setAttribute("error", "Could not retrieve questions: " + e.getMessage());
             forwardTo(request, response, "/WEB-INF/hiddenjsp/dashboard.jsp"); // Forward to dashboard on error
         }
    }

    private void handleShowAddQuestion(HttpServletRequest request, HttpServletResponse response)
             throws ServletException, IOException {
        String viewPath = "/WEB-INF/hiddenjsp/addQuestion.jsp"; // Use dedicated add JSP
        try {
             int quizId = Integer.parseInt(request.getParameter("quizId"));
             // Optional: Verify quizId exists via service/DAO if desired
             request.setAttribute("quizId", quizId);
             request.setAttribute("questionData", new Question(quizId)); // Empty for form
             request.setAttribute("formAction", "saveQuestion");
             request.setAttribute("pageTitle", "Add New Question");
             // Set CSRF token again for this form view
             setCsrfToken(request, request.getSession());
             forwardTo(request, response, viewPath);
         } catch (NumberFormatException e) {
             response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or missing Quiz ID for adding question.");
         }
    }


     private void handleSaveQuestion(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
         String quizIdStr = request.getParameter("quizId");
         String questionText = request.getParameter("questionText");
         Map<String, String> options = new LinkedHashMap<>();
         options.put("A", request.getParameter("optionA"));
         options.put("B", request.getParameter("optionB"));
         options.put("C", request.getParameter("optionC"));
         options.put("D", request.getParameter("optionD"));
         String correctKey = request.getParameter("correctAnswerKey");
         String viewPath = "/WEB-INF/hiddenjsp/addQuestion.jsp"; // Forward back here on error
         int quizId = -1;

         try {
        	 LOG.info("DEBUG_SAVE: Received quizId parameter = [" + quizIdStr + "]");
             quizId = Integer.parseInt(quizIdStr);
             LOG.info("DEBUG_SAVE: Parsed quizId = " + quizId);
             // Create Question object (ensure it has setQuizId method or use constructor)
             Question newQuestion = new Question(quizId);
             LOG.info("DEBUG_SAVE: Before setQuizId, newQuestion.getQuizId() = " + newQuestion.getQuizId());
             newQuestion.setQuizId(quizId); 
             LOG.info("DEBUG_SAVE: After setQuizId, newQuestion.getQuizId() = " + newQuestion.getQuizId());
             newQuestion.setText(questionText);
             newQuestion.setOptions(options);
             newQuestion.setCorrectAnswerKey(correctKey);
             LOG.info("DEBUG_SAVE: calling quizService.saveNewQuestion with quizId = " + newQuestion.getQuizId());
             quizService.saveNewQuestion(newQuestion); // Service handles DAO call + validation
             // Redirect back to the list of questions for that quiz
             response.sendRedirect(request.getContextPath() + "/admin?action=listQuestions&quizId=" + quizId + "&successMsg=Question added successfully.");

         } catch (NumberFormatException e) {
             response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Quiz ID provided for saving question.");
         } catch (RuntimeException e) {
             // Handle validation errors from service or DB errors from DAO
             System.err.println("Error saving question: " + e.getMessage()); // Use Logger
             request.setAttribute("error", "Failed to add question: " + e.getMessage());
             // Repopulate form data for redisplay
             request.setAttribute("quizId", quizId != -1 ? quizId : null); // Pass quizId back
             Question questionData = new Question(quizId); // Repopulate from parameters
             if (quizId != -1) questionData.setQuizId(quizId);
             questionData.setText(questionText);
             questionData.setOptions(options);
             questionData.setCorrectAnswerKey(correctKey);
             request.setAttribute("questionData", questionData);
             request.setAttribute("formAction", "saveQuestion");
             request.setAttribute("pageTitle", "Add New Question");
             setCsrfToken(request, session); // Re-set token for the form
             forwardTo(request, response, viewPath); // Forward to show error and retain data
         }
    }

    private void handleDeleteQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
             int questionId = Integer.parseInt(request.getParameter("questionId"));
             String quizId = request.getParameter("quizId"); // Get quizId to redirect back correctly

             if (quizId == null || quizId.trim().isEmpty()){
                 throw new IllegalArgumentException("Missing quizId parameter for redirection after delete.");
             }

             boolean deleted = quizService.deleteQuestionById(questionId);
             String msgParam = deleted ? "successMsg=Question deleted." : "errorMsg=Failed to delete question.";

             // Redirect back to the list for the same quiz
             response.sendRedirect(request.getContextPath() + "/admin?action=listQuestions&quizId=" + quizId + "&" + msgParam);

         } catch (NumberFormatException e) {
              response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Question ID format for deletion.");
         } catch (IllegalArgumentException e) {
              response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
         } catch (RuntimeException e) {
              System.err.println("Error deleting question: " + e.getMessage()); // Use Logger
              String quizId = request.getParameter("quizId"); // Try to get quizId again
              // Redirect back with error (avoiding generic error page if possible)
              response.sendRedirect(request.getContextPath() + "/admin?action=listQuestions&quizId=" + quizId + "&errorMsg=Error deleting question.");
         }
    }


    
    private void setCsrfToken(HttpServletRequest request, HttpSession session) {
        if (session == null) session = request.getSession(true); // Ensure session exists
        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute(CSRF_TOKEN_SESSION_ATTR, csrfToken);
        request.setAttribute(CSRF_TOKEN_REQUEST_PARAM, csrfToken);
    }

    private boolean isValidCsrfToken(HttpSession session, String requestToken) {
        if (session == null) return false;
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        return sessionToken != null && requestToken != null && sessionToken.equals(requestToken);
    }

    private void forwardTo(HttpServletRequest request, HttpServletResponse response, String jspPath)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);
        dispatcher.forward(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AdminControllerServlet Destroyed."); // Use Logger here later
    }
}