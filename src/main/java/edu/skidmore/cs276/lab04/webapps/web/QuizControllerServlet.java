package edu.skidmore.cs276.lab04.webapps.web;

import java.io.IOException;
import java.util.Map;
import java.util.UUID; // For CSRF

import org.apache.log4j.Logger;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import edu.skidmore.cs276.lab04.beans.tasklist.Score;
import edu.skidmore.cs276.lab04.beans.tasklist.SportsQuiz;

/**
 * This factory creates and returns a list of SportQuiz objects all the quiz url
 * definitions are central in the class
 */

public class QuizControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(QuizControllerServlet.class);
	private QuizService quizService;
	// Use the same CSRF constants for consistency if desired, or make specific ones
	private static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";
	private static final String CSRF_TOKEN_REQUEST_PARAM = "csrfToken";

	/**
	 * Initializes the servlet and gets the QuizService instance from the
	 * ServletContext.
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config); // Needed for Servlet lifecycle
		ServletContext context = config.getServletContext();
		this.quizService = (QuizService) context.getAttribute("quizService");

		// Fail if the service dependency isn't available
		if (this.quizService == null) {
			LOG.error("CRITICAL ERROR: QuizService not found in ServletContext! AppContextListener might have failed."); 
			throw new ServletException("QuizService is unavailable. Cannot initialize QuizControllerServlet.");
		}
		LOG.trace("QuizControllerServlet Initialized with QuizService.");
	}

	/**
	 *  handler for both GET and POST requests directed to this servlet.
	 * instructs the processing based on the 'action' parameter. Includes CSRF check for
	 * state-changing POST actions.
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		HttpSession session = request.getSession(false); // Get session if exists


		// CSRF Token Validation for state-changing POST requests
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			// Define which POST actions need CSRF check
			if ("submit".equals(action)) {
				if (!isValidCsrfToken(request, session)) {
					LOG.error("CSRF Check Failed for action: " + action); 
					response.sendError(HttpServletResponse.SC_FORBIDDEN,
							"Invalid request form submission (CSRF check failed).");
					return; // Stop processing
				}
				LOG.trace("CSRF check passed for action: " + action); 
			}
		}
		

		String viewPath = "/WEB-INF/hiddenjsp/"; // Base path for JSPs

		try {
			// Ensure action isn't null to avoid NullPointerException in the switch
			if (action == null) {
				LOG.debug("Action parameter is missing, redirecting to quiz selection.");
				// Redirect to a default page for logged-in users
				response.sendRedirect(request.getContextPath() + "/quizme?action=select");
				return;
			}

			switch (action) {
			case "select":
				//  Displays the the quiz selection page
				LOG.trace("Action: select"); 
				request.setAttribute("categories", quizService.getAvailableCategories()); // gets this from DB via Service/DAO
																							
				viewPath += "selectQuiz.jsp";
				break;

			case "start":
				//  Start a specific quiz
				LOG.trace("Action: start"); 
				String category = request.getParameter("category");
				if (category != null && !category.trim().isEmpty()) {
					SportsQuiz quiz = quizService.getQuizByCategory(category); // Gets from DB

					if (quiz != null && !quiz.getQuestions().isEmpty()) {
						// Quiz loaded successfully
						session = request.getSession(true); // Ensure session exists
						session.setAttribute("currentQuiz", quiz);
						
						request.setAttribute("pageCategory", category);
						
						
						//session.setAttribute("currentQuestionIndex", 0); // Start at the first question

						// Prepare data for the first question view
						//Question firstQuestion = quiz.getQuestions().get(0);
						//request.setAttribute("question", firstQuestion);
						//request.setAttribute("questionIndex", 0);
						//request.setAttribute("totalQuestions", quiz.getQuestions().size());

						// Generate and sets the CSRF token for the quiz submission form
						String csrfToken = UUID.randomUUID().toString();
						session.setAttribute(CSRF_TOKEN_SESSION_ATTR, csrfToken);
						request.setAttribute(CSRF_TOKEN_REQUEST_PARAM, csrfToken); // Make available to quizPage.jsp
						System.out.println("Starting quiz '" + category + "'. CSRF token set."); // Use Logger

						viewPath += "quizPage.jsp";
					} else {
						// Quiz not found or empty
						LOG.error("Quiz not found or empty for category: " + category); // Use Logger
						request.setAttribute("error",
								"Sorry, the quiz for '" + category + "' could not be loaded or is empty.");
						request.setAttribute("categories", quizService.getAvailableCategories()); // Reload categories for selection page
																									
						viewPath += "selectQuiz.jsp";
					}
				} else {
					// Category parameter was missing or empty
					LOG.error("Category parameter missing or empty for action 'start'."); 
					request.setAttribute("error", "Please select a valid quiz category.");
					request.setAttribute("categories", quizService.getAvailableCategories());
					viewPath += "selectQuiz.jsp";
				}
				break;

			case "submit":
				// submitted quiz answers & CSRF check done above for POST
				LOG.trace("Action: submit"); 

				if (session == null) {
					
					LOG.error("Error submitting quiz: No active session found."); 
					response.sendRedirect(request.getContextPath() + "/authenticate?action=showLogin");
					return;
				}

				SportsQuiz quizToSubmit = (SportsQuiz) session.getAttribute("currentQuiz");
				if (quizToSubmit != null) {
					// Get submitted answers from request
					Map<String, String[]> submittedAnswers = request.getParameterMap();
					// Calculates score using the service layer
					Score userScore = quizService.calculateScore(quizToSubmit, submittedAnswers);

					// Set attributes for the results page view
					request.setAttribute("score", userScore);
					request.setAttribute("quizTitle", quizToSubmit.getTitle());

					// Clean up quiz state from the session only
					session.removeAttribute("currentQuiz");
					session.removeAttribute("currentQuestionIndex");
					LOG.trace("Quiz submitted. Score calculated. Quiz state removed from session.");
																												

					

					viewPath += "results.jsp";
				} else {
					// No quiz found in session 
					LOG.error("Error submitting quiz: No 'currentQuiz' found in session.");
					request.setAttribute("error",
							"Your quiz session seems to have expired or the quiz data was lost. Please start again.");
					request.setAttribute("categories", quizService.getAvailableCategories());
					viewPath += "selectQuiz.jsp"; // Redirects back to selection
				}
				break;


			default:
				// Unknown action requested
				LOG.trace("Unknown action requested: " + action + ". Redirecting to quiz selection."); 
																												
				// Redirect to a safe default page for logged-in users
				response.sendRedirect(request.getContextPath() + "/quizme?action=select");
				return; // Stop processing after redirect
			}

			// Forward to the selected View JSP
			RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
			dispatcher.forward(request, response);

		} catch (Exception e) {
			// Catch possible exceptions from service layer like possible database errors or
			// other issues.
			LOG.error("Exception occurred processing action '" + action + "' in QuizControllerServlet: "
					+ e.getMessage());
			e.printStackTrace(); 

			
			throw new ServletException("Error during quiz processing: " + e.getMessage(), e);

		}
	}

	/**
	 * Handles GET requests by delegating to the common handler method.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handleRequest(request, response);
	}

	/**
	 * Handles POST requests by delegating to the common handler method.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handleRequest(request, response);
	}

	/**
	 * Helper method to validate the CSRF token from the request against the one in
	 * the session.
	 * 
	 * @param request The HttpServletRequest.
	 * @param session The HttpSession (can be null).
	 * @return true if the tokens match or if the session is null (should be handled
	 *         by caller), false otherwise.
	 */
	private boolean isValidCsrfToken(HttpServletRequest request, HttpSession session) {
		if (session == null) {
			LOG.error("CSRF check cannot be done: Session is null."); 
			return false; 
		}
		String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
		String requestToken = request.getParameter(CSRF_TOKEN_REQUEST_PARAM);

		boolean isValid = sessionToken != null && requestToken != null && sessionToken.equals(requestToken);
		if (!isValid) {
			LOG.error(
					"CSRF Check FAILED: Session Token='" + sessionToken + "', Request Token='" + requestToken + "'"); 
																														
		}
		return isValid;
	}

	@Override
	public void destroy() {
		LOG.trace("QuizControllerServlet Destroyed."); 
		
	}
}