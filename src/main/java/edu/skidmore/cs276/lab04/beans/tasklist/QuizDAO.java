package edu.skidmore.cs276.lab04.beans.tasklist;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Quiz data. Defines the contract for
 * retrieving quiz information from a persistence source.
 */
public interface QuizDAO {

	/**
	 * Finds a fully populated Quiz object (including its questions and options)
	 * based on its category name.
	 *
	 * @param category The unique category name of the quiz to find.
	 * @return An Optional containing the Quiz if found, otherwise an empty
	 *         Optional.
	 */
	Optional<SportsQuiz> findByCategory(String category);

	/**
	 * Retrieves a list of all unique quiz category names available.
	 *
	 * @return A List of Strings representing the available quiz categories,
	 *         typically sorted alphabetically. Returns an empty list if none are
	 *         found.
	 */
	List<String> findAllCategories();

	/**
     * Retrieves basic information (id, category, title) for all available quizzes.
     * Used for listing quizzes on the admin dashboard without loading question details.
     *
     * @return A List of SportsQuiz objects, where each object contains at least
     * the ID, category, and title. The questions list within these objects
     * may be empty or null depending on the implementation. Returns an empty list on error.
     */
    List<SportsQuiz> findAllQuizzesBasicInfo();

    /**
     * Finds all questions (including their options and correct answer key)
     * associated with a specific quiz ID. Used for the admin "list questions" page.
     *
     * @param quizId The ID of the quiz whose questions are to be retrieved.
     * @return A List of fully populated Question objects belonging to the specified quiz.
     * Returns an empty list if the quiz has no questions or on error.
     */
    List<Question> findQuestionsByQuizId(int quizId);

    /**
     * Saves a new question and its associated options to the database.
     * The Question object passed in should contain the quizId it belongs to,
     * the question text, a map or list of options, and the correct answer key.
     * Implementation should handle inserting the question and its options, ideally
     * within a transaction.
     *
     * @param question The fully populated Question object to save (must have quizId set).
     * @return The auto-generated database ID of the newly inserted question, or -1 on failure.
     */
    int saveQuestionWithOptions(Question question);

    /**
     * Deletes a question (and its associated options, typically via database cascade)
     * based on its unique ID.
     *
     * @param questionId The ID of the question to delete.
     * @return true if the question was successfully deleted (or didn't exist), false on error.
     */
    boolean deleteQuestion(int questionId);
}
