package edu.skidmore.cs276.lab04.webapps.web; // Using your specified package

// Imports for models (adjust paths if necessary)
import edu.skidmore.cs276.lab04.beans.tasklist.Question;
import edu.skidmore.cs276.lab04.beans.tasklist.QuizDAO; // DAO Interface
import edu.skidmore.cs276.lab04.beans.tasklist.Score;
import edu.skidmore.cs276.lab04.beans.tasklist.SportsQuiz; // Using SportsQuiz

// Standard Java imports
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors; 

import org.apache.log4j.Logger;


/**
 * Service layer for handling quiz-related business logic.
 * Acts as an intermediary between controllers and DAOs.
 * Updated for simplified admin functionality (manage questions only).
 */
public class QuizService {
    
    private static final Logger LOG = Logger.getLogger(QuizService.class);

    private final QuizDAO quizDAO;

    /**
     * Constructs a QuizService with the specified QuizDAO.
     * Dependency Injection: The DAO implementation is provided externally.
     *
     * @param quizDAO The Data Access Object for quizzes. Must not be null.
     */
    public QuizService(QuizDAO quizDAO) {
        if (quizDAO == null) {
            LOG.error("Attempted to initialize QuizService with a null QuizDAO.");
            throw new IllegalArgumentException("QuizDAO cannot be null");
        }
        this.quizDAO = quizDAO;
        LOG.info("QuizService initialized with DAO: " + quizDAO.getClass().getSimpleName());
    }

    /**
     * Retrieves basic information (ID, category, title) for all available quizzes.
     * Intended for listing quizzes in the admin dashboard without loading full question data.
     *
     * @return An unmodifiable list of SportsQuiz objects (with questions likely not populated),
     * or an empty list on error.
     */
    public List<SportsQuiz> getAvailableQuizzesBasicInfo() {
        try {
           
            // This DAO method should only SELECT id, category, title.
            List<SportsQuiz> quizzes = quizDAO.findAllQuizzesBasicInfo();
            LOG.debug("Retrieved " + quizzes.size() + " basic quiz infos from DAO.");
            return Collections.unmodifiableList(quizzes);
        } catch (RuntimeException e) {
            LOG.error("Error retrieving basic quiz info from DAO: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves a list of all unique quiz category names available.
     * Still useful for validation like isValidCategory.
     *
     * @return An unmodifiable list of category strings, or an empty list if none are found or on error.
     */
    public List<String> getAvailableCategories() {
        try {
            List<String> categories = quizDAO.findAllCategories();
            LOG.debug("Retrieved " + categories.size() + " categories from DAO.");
            return Collections.unmodifiableList(categories); // Return immutable view
        } catch (RuntimeException e) {
            LOG.error("Error retrieving categories from DAO: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves a specific, fully populated quiz (including questions and options)
     * by its category name. Used when a user starts taking a quiz.
     *
     * @param category The category name of the quiz to retrieve.
     * @return The fully populated SportsQuiz object if found, otherwise null.
     */
    public SportsQuiz getQuizByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            LOG.warn("getQuizByCategory called with null or empty category.");
            return null;
        }
        try {
            // DAO method findByCategory should return Optional<SportsQuiz>
            Optional<SportsQuiz> quizOptional = quizDAO.findByCategory(category);
            if (!quizOptional.isPresent()) {
                 LOG.warn("No quiz found for category: " + category);
            }
            return quizOptional.orElse(null); // Return SportsQuiz object or null
        } catch (RuntimeException e) {
            LOG.error("Error retrieving quiz for category '" + category + "' from DAO: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Checks if a given category name corresponds to an existing quiz.
     *
     * @param category The category name to validate.
     * @return true if a quiz exists for the category, false otherwise.
     */
    public boolean isValidCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        // Uses the potentially cached or efficient getAvailableCategories()
        List<String> categories = getAvailableCategories();
        return categories.contains(category);
    }

    /**
     * Retrieves all questions associated with a specific quiz ID.
     * Used by the admin interface to list questions for management.
     *
     * @param quizId The ID of the quiz whose questions are to be retrieved.
     * @return A list of Question objects, or an empty list if none are found or on error.
     */
    public List<Question> getQuestionsForQuiz(int quizId) {
        if (quizId <= 0) {
            LOG.warn("getQuestionsForQuiz called with invalid quizId: " + quizId);
            return Collections.emptyList();
        }
        try {
            // Assumes quizDAO has findQuestionsByQuizId(int) returning List<Question>
            List<Question> questions = quizDAO.findQuestionsByQuizId(quizId);
            LOG.debug("Retrieved " + questions.size() + " questions for quiz ID " + quizId);
            return questions; // Return modifiable list? Or Collections.unmodifiableList(questions);
        } catch (RuntimeException e) {
           LOG.error("Failed to get questions for quiz " + quizId + ": " + e.getMessage(), e);
           return Collections.emptyList();
        }
    }

    /**
     * Saves a new question (including its text, options, correct answer, and associated quiz ID)
     * to the database. Performs basic validation.
     *
     * @param question The Question object to save. Must have quizId, text, options, and correct key set.
     * @throws IllegalArgumentException if the provided question data is invalid.
     * @throws RuntimeException if a database error occurs during saving.
     */
    public void saveNewQuestion(Question question) throws IllegalArgumentException, RuntimeException {
    	LOG.info("DEBUG_SERVICE: Service received question. QuizID = " + question.getQuizId());

        LOG.debug("Attempting to save new question for quiz ID: " + (question != null ? question.getId() : "null"));
        // Basic Validation
        if (question == null) throw new IllegalArgumentException("Question object cannot be null.");
        if (question.getQuizId() <= 0) throw new IllegalArgumentException("Invalid Quiz ID provided in Question object.");
        if (question.getText() == null || question.getText().trim().isEmpty()) throw new IllegalArgumentException("Question text cannot be empty.");
        if (question.getOptions() == null || question.getOptions().size() < 2) throw new IllegalArgumentException("Question must have at least 2 options."); // Example validation
        if (question.getCorrectAnswerKey() == null || !question.getOptions().containsKey(question.getCorrectAnswerKey())) throw new IllegalArgumentException("A valid correct answer key must be provided and exist in options.");
        // Add more validation for option text length, etc. if needed

        try {
            // Assumes quizDAO has saveQuestionWithOptions(Question)
            // This DAO method should handle inserting question and options transactionally.
            quizDAO.saveQuestionWithOptions(question);
            LOG.info("Successfully saved new question (ID likely generated by DB) for quiz ID: " + question.getId());
        } catch (RuntimeException e) {
            // Log specific DAO errors if needed, then rethrow or wrap
            LOG.error("Database error saving new question for quiz ID " + question.getId() + ": " + e.getMessage(), e);
            throw e; // Re-throw RuntimeException (e.g., from DB) to be handled by controller
        }
    }

    /**
     * Deletes a specific question (and its associated options via DB cascade) by its ID.
     *
     * @param questionId The ID of the question to delete.
     * @return true if the question was successfully deleted (or didn't exist), false on error.
     */
    public boolean deleteQuestionById(int questionId) {
         if (questionId <= 0) {
             LOG.warn("Attempted to delete question with invalid ID: " + questionId);
             return false;
         }
         try {
             // Assumes quizDAO has deleteQuestion(int) returning boolean or rows affected
            boolean deleted = quizDAO.deleteQuestion(questionId);
            if (deleted) {
                 LOG.info("Successfully deleted question ID: " + questionId);
            } else {
                 LOG.warn("Failed to delete question ID " + questionId + " (may not have existed or DAO error).");
            }
            return deleted; // Return status from DAO
         } catch (RuntimeException e) {
             LOG.error("Error deleting question ID " + questionId + ": " + e.getMessage(), e);
             return false; // Return false on error
         }
    }


    /**
     * Calculates the score for a completed quiz based on the submitted answers.
     * Accepts SportsQuiz object.
     *
     * @param quiz             The SportsQuiz object that was taken. Must not be null.
     * @param submittedAnswers A Map containing the submitted answers from the HTTP request. Must not be null.
     * @return A Score object representing the result.
     */
    public Score calculateScore(SportsQuiz quiz, Map<String, String[]> submittedAnswers) {
        // Validation
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null for score calculation.");
        }
        if (submittedAnswers == null) {
            
            throw new IllegalArgumentException("Submitted answers cannot be null.");
        }

        List<Question> questions = quiz.getQuestions();
        // Handle null questions list defensively 
        int totalQuestions = (questions == null) ? 0 : questions.size();
        int correctCount = 0;

        if (totalQuestions == 0) {
            LOG.warn("Calculating score for quiz '" + quiz.getTitle() + "' which has no questions.");
            return new Score(0, 0);
        }

        LOG.debug("Calculating score for quiz: " + quiz.getTitle() + " (" + totalQuestions + " questions)");

        for (Question question : questions) {
            if (question == null) continue; // Skip null questions in list 

            String paramName = "q_" + question.getId();
            String[] answerArray = submittedAnswers.get(paramName);
            String submittedKey = (answerArray != null && answerArray.length > 0) ? answerArray[0] : null;
            String correctKey = question.getCorrectAnswerKey();

            if (LOG.isTraceEnabled()) { // Log detailed info only if TRACE is enabled
                 LOG.trace(" QID: " + question.getId() + ", Correct Key: " + correctKey + ", Submitted Key: " + submittedKey);
            }

            if (correctKey != null && correctKey.equals(submittedKey)) {
                correctCount++;
            } else if (submittedKey == null) {
                LOG.trace(" QID: " + question.getId() + " - No answer submitted.");
            }
        }

        LOG.debug("Score Calculation Complete: " + correctCount + "/" + totalQuestions);
        return new Score(correctCount, totalQuestions);
    }
}