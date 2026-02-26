package edu.skidmore.cs276.lab04.beans.tasklist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * JDBC implementation of the QuizDAO interface. 
 */
public class JdbcQuizDAO implements QuizDAO {
	private static Logger LOG = Logger.getLogger(JdbcQuizDAO.class);

	// Store connection parameters
	private final String RdbUrl;
	private final String RdbUserId;
	private final String RdbPassword;

	/**
	 * Constructs a JdbcQuizDAO with database connection parameters.
	 * 
	 * @param dbUrl      The JDBC URL for the database connection.
	 * @param dbUser     The database username.
	 * @param dbPassword The database password.
	 */
	public JdbcQuizDAO(String RdbUrl, String RdbUserId, String RdbPassword) {
		if (RdbUrl == null || RdbUserId == null || RdbPassword == null) {
			throw new IllegalArgumentException("Database connection parameters (URL, User, Password) cannot be null");
		}
		this.RdbUrl = RdbUrl;
		this.RdbUserId = RdbUserId;
		this.RdbPassword = RdbPassword; // Store password 
		System.out.println("JdbcQuizDAO initialized with direct DB parameters. URL: " + RdbUrl); // Use
																												// Logger
	}

	@Override
	public Optional<SportsQuiz> findByCategory(String category) {
		SportsQuiz quiz = null;
		String quizSql = "SELECT id, title FROM quizzes WHERE category = ?";
		String questionsSql = "SELECT id, text FROM questions WHERE quiz_id = ? ORDER BY id";
		String optionsSql = "SELECT option_key, option_text, is_correct FROM options WHERE question_id = ? ORDER BY option_key";

		// 
		// A new connection is made for each call to the method
		try (Connection conn = DriverManager.getConnection(this.RdbUrl, this.RdbUserId, this.RdbPassword);
				PreparedStatement psQuiz = conn.prepareStatement(quizSql)) {

			psQuiz.setString(1, category);
			LOG.trace("Executing query: " + psQuiz);

			try (ResultSet rsQuiz = psQuiz.executeQuery()) {
				if (rsQuiz.next()) {
					int quizId = rsQuiz.getInt("id");
					String title = rsQuiz.getString("title");
					quiz = new SportsQuiz(quizId, category, title);
					LOG.trace("Found quiz: id=" + quizId + ", category=" + category); 

					// Gets the questions (uses the same connection 'conn')
					try (PreparedStatement psQuestions = conn.prepareStatement(questionsSql)) {
						psQuestions.setInt(1, quizId);
						LOG.trace("Executing query: " + psQuestions); 
						try (ResultSet rsQuestions = psQuestions.executeQuery()) {
							while (rsQuestions.next()) {
								int questionId = rsQuestions.getInt("id");
								String questionText = rsQuestions.getString("text");
								Question question = new Question(questionId, quizId, questionText);
								LOG.trace(" Found question: id=" + questionId);

								// Gets the options (uses the same connection 'conn')
								try (PreparedStatement psOptions = conn.prepareStatement(optionsSql)) {
									psOptions.setInt(1, questionId);
									try (ResultSet rsOptions = psOptions.executeQuery()) {
										Map<String, String> optionsMap = new LinkedHashMap<>();
										String correctKey = null;
										while (rsOptions.next()) {
											String key = rsOptions.getString("option_key");
											String text = rsOptions.getString("option_text");
											boolean isCorrect = rsOptions.getBoolean("is_correct");
											optionsMap.put(key, text);
											if (isCorrect) {
												correctKey = key;
											}
										}
										question.setOptions(optionsMap);
										question.setCorrectAnswerKey(correctKey);
										LOG.trace("  Found " + optionsMap.size() + " options for question "
												+ questionId + ". Correct: " + correctKey);
									} // rsOptions closes
								} // psOptions closes
								quiz.addQuestion(question);
							} // end while questions
						} // rsQuestions closes
					} // psQuestions closes
				} else {
					LOG.trace("No quiz found for category: " + category); // Use Logger
				}
			} // rsQuiz closes
		} catch (SQLException e) {
			LOG.error(
					"Database error finding quiz by category '" + category + "' (DriverManager): " + e.getMessage()); 
																														
			e.printStackTrace();
			throw new RuntimeException("Database error fetching quiz data for category: " + category, e);
		} // conn closes here

		return Optional.ofNullable(quiz);
	}

	@Override
	public List<String> findAllCategories() {

		List<String> categories = new ArrayList<>();

		String sql = "SELECT DISTINCT category FROM quizzes ORDER BY category";
		//Get connection using DriverManager in try-with-resources

		try (Connection conn = DriverManager.getConnection(this.RdbUrl, this.RdbUserId, this.RdbPassword);

				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
				LOG.trace("Executing query: " + ps);

		while (rs.next()) {
		categories.add(rs.getString("category"));
		}
		LOG.trace("Found categories: " + categories);
		} catch (SQLException e) {
		LOG.error("Database error finding all categories (DriverManager): " + e.getMessage()); 

		e.printStackTrace();

		throw new RuntimeException("Database error fetching quiz categories", e);

		} // conn closes here

		return categories;
		}
	
	@Override
    public List<SportsQuiz> findAllQuizzesBasicInfo() {
        List<SportsQuiz> quizzes = new ArrayList<>();
        // Select only necessary columns for the admin dashboard list
        String sql = "SELECT id, category, title FROM quizzes ORDER BY category";

        try (Connection conn = DriverManager.getConnection(this.RdbUrl, this.RdbUserId, this.RdbPassword);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

             System.out.println("Executing query: " + ps); // Use Logger
            while (rs.next()) {
                int id = rs.getInt("id");
                String category = rs.getString("category");
                String title = rs.getString("title");
                // Create SportsQuiz object, questions list will be empty/null by default
                // Assuming a constructor like SportsQuiz(id, category, title) exists
                quizzes.add(new SportsQuiz(id, category, title));
            }
             System.out.println("Found " + quizzes.size() + " basic quiz infos."); // Use Logger
        } catch (SQLException e) {
            System.err.println("Database error finding all quiz basic info: " + e.getMessage()); // Use Logger
            e.printStackTrace();
            throw new RuntimeException("Database error fetching basic quiz info", e);
        } // conn closes automatically here
        return quizzes;
    }


    @Override
    public List<Question> findQuestionsByQuizId(int quizId) {
         // Public method establishes connection and calls internal logic
         try (Connection conn = DriverManager.getConnection(this.RdbUrl, this.RdbUserId, this.RdbPassword)) {
             return findQuestionsByQuizIdInternal(conn, quizId);
         } catch (SQLException e) {
            System.err.println("Database error getting connection for findQuestionsByQuizId (quizId " + quizId + "): " + e.getMessage());
            throw new RuntimeException("Database error finding questions", e);
         }
    }

    /**
     * Internal helper to find questions/options using an existing connection.
     * Called by findByCategory and findQuestionsByQuizId.
     */
    private List<Question> findQuestionsByQuizIdInternal(Connection conn, int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String questionsSql = "SELECT id, text FROM questions WHERE quiz_id = ? ORDER BY id";
        String optionsSql = "SELECT option_key, option_text, is_correct FROM options WHERE question_id = ? ORDER BY option_key";

        try (PreparedStatement psQuestions = conn.prepareStatement(questionsSql)) {
            psQuestions.setInt(1, quizId); // Use the quizId passed into the method
            System.out.println("Executing query: " + psQuestions); // Use Logger

            try (ResultSet rsQuestions = psQuestions.executeQuery()) {
                while (rsQuestions.next()) {
                     // Read data from the current row
                     int actualQuestionId = rsQuestions.getInt("id"); // Get the specific question's ID
                     String questionText = rsQuestions.getString("text");
                     System.out.println("DAO: Read questionId from DB: " + actualQuestionId); // Log DB value

                     // *** === CALL THE CORRECT CONSTRUCTOR === ***
                     // Use the constructor: Question(int id, int quizId, String text)
                     Question question = new Question(actualQuestionId, quizId, questionText);
                     // Now the 'id' and 'quizId' fields are correctly set by the constructor.

                     System.out.println("DAO: Created Question object: ID = " + question.getId() + ", QuizID = " + question.getQuizId()); // Log ID from object

                     // Fetch options for this specific question (using actualQuestionId)
                     try (PreparedStatement psOptions = conn.prepareStatement(optionsSql)) {
                         psOptions.setInt(1, actualQuestionId); // Use the question's actual ID
                         try (ResultSet rsOptions = psOptions.executeQuery()) {
                             Map<String, String> optionsMap = new LinkedHashMap<>();
                             String correctKey = null;
                             while (rsOptions.next()) {
                                String key = rsOptions.getString("option_key");
                                String text = rsOptions.getString("option_text");
                                boolean isCorrect = rsOptions.getBoolean("is_correct");
                                optionsMap.put(key, text);
                                if (isCorrect) correctKey = key;
                             }
                             question.setOptions(optionsMap);
                             question.setCorrectAnswerKey(correctKey);
                         } // rsOptions closes
                     } // psOptions closes
                     questions.add(question); // Add fully populated question
                } // end while questions loop
            } // rsQuestions closes
        } // psQuestions closes
        return questions; // Return the list
    }


    @Override
    public int saveQuestionWithOptions(Question question) {
        // Transaction needed: Insert question, get ID, insert options.
        String questionSql = "INSERT INTO questions (quiz_id, text) VALUES (?, ?)";
        String optionSql = "INSERT INTO options (question_id, option_key, option_text, is_correct) VALUES (?, ?, ?, ?)";
        int generatedQuestionId = -1;
        Connection conn = null; // Declare outside try for transaction control

        try {
            // Get connection and disable auto-commit for transaction
            conn = DriverManager.getConnection(this.RdbUrl, this.RdbUserId, this.RdbPassword);
            conn.setAutoCommit(false);
            System.out.println("Transaction started for saveQuestionWithOptions."); // Use Logger

            // 1. Insert the Question and get generated ID
            try (PreparedStatement psQuestion = conn.prepareStatement(questionSql, Statement.RETURN_GENERATED_KEYS)) {
                psQuestion.setInt(1, question.getQuizId()); // Assuming Question has getQuizId()
                psQuestion.setString(2, question.getText());
                int rowsAffected = psQuestion.executeUpdate();
                if (rowsAffected != 1) {
                    throw new SQLException("Creating question failed, no rows affected.");
                }

                try (ResultSet keys = psQuestion.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedQuestionId = keys.getInt(1);
                        System.out.println("Inserted question, generated ID: " + generatedQuestionId); // Use Logger
                    } else {
                        throw new SQLException("Creating question failed, no ID obtained.");
                    }
                }
            } // psQuestion closes

            // 2. Insert the Options using the generatedQuestionId
            if (generatedQuestionId != -1 && question.getOptions() != null && !question.getOptions().isEmpty()) {
                 try (PreparedStatement psOption = conn.prepareStatement(optionSql)) {
                     for (Map.Entry<String, String> entry : question.getOptions().entrySet()) {
                         psOption.setInt(1, generatedQuestionId);
                         psOption.setString(2, entry.getKey()); // Option Key (A, B, C..)
                         psOption.setString(3, entry.getValue()); // Option Text
                         // Determine if this option is the correct one
                         boolean isCorrect = entry.getKey().equals(question.getCorrectAnswerKey());
                         psOption.setBoolean(4, isCorrect);
                         psOption.addBatch(); // Add insert statement to batch
                     }
                     int[] batchResults = psOption.executeBatch(); // Execute batch of option inserts
                     System.out.println("Executed options batch for question ID " + generatedQuestionId + ". Results count: " + batchResults.length); // Use Logger
                     // Optionally check batchResults for errors
                 } // psOption closes
            } else {
                 // Handle case where question has no options (maybe allowed?) or ID failed
                 if(generatedQuestionId == -1) throw new SQLException("Failed to get generated question ID before inserting options.");
                 System.out.println("Question ID " + generatedQuestionId + " saved with no options (or options map was null/empty)."); // Use logger
            }

            conn.commit(); // Commit the transaction
            System.out.println("Transaction committed for saveQuestionWithOptions (QID: " + generatedQuestionId + ")."); // Use Logger

        } catch (SQLException e) {
            System.err.println("Database error saving question with options: " + e.getMessage()); // Use Logger
            e.printStackTrace(); // Log stack trace
            if (conn != null) {
                try {
                    System.err.println("Rolling back transaction due to error.");
                    conn.rollback(); // Rollback on any error during transaction
                } catch (SQLException ex) {
                    System.err.println("Error during transaction rollback: " + ex.getMessage()); // Use Logger
                }
            }
            // Wrap SQLException in RuntimeException to signal failure up the layers
            throw new RuntimeException("Database error saving question with options", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit for the connection if pooled (though less relevant with DriverManager)
                    conn.close(); // **Crucially, close the connection**
                } catch (SQLException e) {
                    System.err.println("Error closing connection/resetting autoCommit after saveQuestionWithOptions: " + e.getMessage()); // Use Logger
                }
            }
        }
        return generatedQuestionId; // Return the new question ID or -1 if commit failed implicitly (though exception likely thrown)
    }


    @Override
    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE id = ?";
        int rowsAffected = 0;

        try (Connection conn = DriverManager.getConnection(this.RdbUrl, this.RdbUserId, this.RdbPassword); // Ensure correct field names
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            rowsAffected = ps.executeUpdate();

            System.out.println("Attempted delete for question ID " + questionId + ". Rows affected: " + rowsAffected); // Use Logger

        } catch (SQLException e) {
             System.err.println("Database error deleting question ID " + questionId + ": " + e.getMessage()); // Use Logger
             e.printStackTrace();
             // Optional: throw new RuntimeException("Database error deleting question", e);
             return false; // Return false on error
        }
        // Return true if one row was deleted, false otherwise (e.g., question didn't exist)
        return rowsAffected > 0;
    }
	
	

}
