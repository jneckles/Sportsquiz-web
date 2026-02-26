package edu.skidmore.cs276.lab04.test;

import edu.skidmore.cs276.lab04.beans.tasklist.Question;
import edu.skidmore.cs276.lab04.beans.tasklist.SportsQuiz;
import edu.skidmore.cs276.lab04.webapps.web.QuizService;
import edu.skidmore.cs276.lab04.beans.tasklist.QuizDAO;
import edu.skidmore.cs276.lab04.beans.tasklist.Score;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;



/**
 * Unit tests for the QuizService class
 *  Mockito to mock the QuizDAO dependency.
 * 
 */
@RunWith(MockitoJUnitRunner.class) // Use Mockito's runner
public class QuizServiceTest { 

    @Mock // Create mock DAO
    private QuizDAO mockQuizDAO; // Use the QuizDAO interface type

    @InjectMocks // Inject mock DAO into the actual QuizService instance
    private QuizService quizService; 

    // Sample data for Using SportsQuiz 
    private SportsQuiz sampleSportsQuizWithQuestions;
    private List<String> sampleCategories;

    @Before 
    public void setUp() {
        // Setup sample data used across tests

        // SportsQuiz with questions (for score testing etc.)
        sampleSportsQuizWithQuestions = new SportsQuiz(1, "science", "General Science");
        Question q1 = new Question(101, "What is H2O?");
        q1.setOptions(Map.of("A", "Water", "B", "Hydrogen Peroxide"));
        q1.setCorrectAnswerKey("A");
        Question q2 = new Question(102, "Planet closest to Sun?");
        q2.setOptions(Map.of("A", "Venus", "B", "Mercury", "C", "Earth"));
        q2.setCorrectAnswerKey("B");
        sampleSportsQuizWithQuestions.addQuestion(q1);
        sampleSportsQuizWithQuestions.addQuestion(q2);

        // List of all the category strings 
        sampleCategories = Arrays.asList("science", "history", "sports");
    }

    //Tests for getAvailableCategories

    @Test
    public void getAvailableCategories_DaoReturnsData_ShouldReturnUnmodifiableList() {
        // Configures mock DAO to return sample data
        when(mockQuizDAO.findAllCategories()).thenReturn(new ArrayList<>(sampleCategories)); // The DAO returns List<String>

        //Calls the service method
        List<String> result = quizService.getAvailableCategories();

        //  Then Check the results
        assertNotNull("Result list should not be null", result);
        assertEquals("List size should match sample", 3, result.size());
        assertEquals("List content should match sample", sampleCategories, result);

        // Then Verifies that the DAO method was called exactly once
        verify(mockQuizDAO, times(1)).findAllCategories();

        // Assert that the returned list is unmodifiable
        try {
            result.add("new category");
            fail("Expected UnsupportedOperationException for unmodifiable list");
        } catch (UnsupportedOperationException e) {
            
        }
    }

    @Test
    public void getAvailableCategories_DaoReturnsEmpty_ShouldReturnEmptyList() {
        when(mockQuizDAO.findAllCategories()).thenReturn(Collections.emptyList());
        List<String> result = quizService.getAvailableCategories();
        assertNotNull(result);
        assertTrue("Results list should be empty", result.isEmpty());
        verify(mockQuizDAO, times(1)).findAllCategories();
    }

    @Test
    public void getAvailableCategories_DaoThrowsException_ShouldReturnEmptyList() {
        // Configure mock DAO to throw an exception
        when(mockQuizDAO.findAllCategories()).thenThrow(new RuntimeException("DAO Layer Error"));

        // Then Call the service method
        List<String> result = quizService.getAvailableCategories();

        //Service should catch exception and return empty list
        assertNotNull(result);
        assertTrue("Resultss list should be empty on DAO exception", result.isEmpty());
        verify(mockQuizDAO, times(1)).findAllCategories();
        
    }


    // Tests for getQuizByCategory 

    @Test
    public void getQuizByCategory_ValidCategoryFound_ShouldReturnSportsQuiz() {
        String category = "science";
        // Mock DAO to return Optional<SportsQuiz>
        when(mockQuizDAO.findByCategory(category)).thenReturn(Optional.of(sampleSportsQuizWithQuestions));

        // Call service method that returns SportsQuiz or null
        SportsQuiz result = quizService.getQuizByCategory(category);

       
        assertNotNull("Result should not be null when category found", result);
        assertEquals("Quiz ID should match", sampleSportsQuizWithQuestions.getId(), result.getId());
        assertEquals("Quiz title should match", sampleSportsQuizWithQuestions.getTitle(), result.getTitle());
        assertEquals("Quiz category should match", sampleSportsQuizWithQuestions.getCategory(), result.getCategory());
        assertEquals("Question count should match", sampleSportsQuizWithQuestions.getQuestions().size(), result.getQuestions().size());
        verify(mockQuizDAO, times(1)).findByCategory(category);
    }

    @Test
    public void getQuizByCategoryNotFound() {
        String category = "nonexistent";
        when(mockQuizDAO.findByCategory(category)).thenReturn(Optional.empty()); // DAO returns empty Optional

        SportsQuiz result = quizService.getQuizByCategory(category);

        assertNull("Result should be null when category not found", result);
        verify(mockQuizDAO, times(1)).findByCategory(category);
    }

    @Test
    public void getQuizByCategoryNullCategory() {
        SportsQuiz result = quizService.getQuizByCategory(null);
        assertNull("Result should be null for null category", result);
        verify(mockQuizDAO, never()).findByCategory(anyString()); // DAO method should not be called
    }

     @Test
    public void getQuizByCategoryEmptyCategory() {
        SportsQuiz result = quizService.getQuizByCategory(" ");
        assertNull("Result should be null for empty category", result);
        verify(mockQuizDAO, never()).findByCategory(anyString());
    }


    @Test
    public void getQuizByCategoryDaoException() {
        String category = "science";
        when(mockQuizDAO.findByCategory(category)).thenThrow(new RuntimeException("DAO DB Error"));

        SportsQuiz result = quizService.getQuizByCategory(category);

        assertNull("Result should be null when DAO throws exception", result);
        verify(mockQuizDAO, times(1)).findByCategory(category);
    }


    // Tests for isValidCategory 

    @Test
    public void isValidCategory_CategoryExists_ShouldReturnTrue() {
         when(mockQuizDAO.findAllCategories()).thenReturn(new ArrayList<>(sampleCategories));
         assertTrue("Category 'science' should be valid", quizService.isValidCategory("science"));
         verify(mockQuizDAO, times(1)).findAllCategories();
    }

    @Test
    public void isValidCategory_CategoryDoesNotExist_ShouldReturnFalse() {
         when(mockQuizDAO.findAllCategories()).thenReturn(new ArrayList<>(sampleCategories));
         assertFalse("Category 'math' should not be valid", quizService.isValidCategory("math"));
         verify(mockQuizDAO, times(1)).findAllCategories();
    }

    @Test
    public void isValidCategory_NullCategory_ShouldReturnFalse() {
         assertFalse("Null category should not be valid", quizService.isValidCategory(null));
         // Service method checks for null before calling DAO, so DAO shouldn't be called
         verify(mockQuizDAO, never()).findAllCategories();
    }

    @Test
    public void isValidCategory_EmptyCategory_ShouldReturnFalse() {
         assertFalse("Empty category should not be valid", quizService.isValidCategory("  "));
         verify(mockQuizDAO, never()).findAllCategories();
    }

    @Test
    public void isValidCategory_DaoThrowsException_ShouldReturnFalse() {
        // If DAO throws exception, service returns empty list, so contains returns false
         when(mockQuizDAO.findAllCategories()).thenThrow(new RuntimeException("DAO DB Error"));
         assertFalse("Should return false if DAO throws exception", quizService.isValidCategory("science"));
         verify(mockQuizDAO, times(1)).findAllCategories();
    }


    // Tests for calculateScore 

    @Test
    public void calculateScore_AllCorrect_ShouldReturnCorrectScore() {
        Map<String, String[]> answers = new HashMap<>();
        answers.put("q_101", new String[]{"A"}); // Correct
        answers.put("q_102", new String[]{"B"}); // Correct
        Score score = quizService.calculateScore(sampleSportsQuizWithQuestions, answers); // Pass SportsQuiz
        assertEquals("Correct count mismatch", 2, score.getCorrectAnswers());
        assertEquals("Total count mismatch", 2, score.getTotalQuestions());
    }

    @Test
    public void calculateScoreSomeCorrect() {
        Map<String, String[]> answers = new HashMap<>();
        answers.put("q_101", new String[]{"A"}); // Correct
        answers.put("q_102", new String[]{"C"}); // Incorrect
        Score score = quizService.calculateScore(sampleSportsQuizWithQuestions, answers);
        assertEquals(1, score.getCorrectAnswers());
        assertEquals(2, score.getTotalQuestions());
    }

     @Test
    public void calculateScoreMissingAnswer() {
        Map<String, String[]> answers = new HashMap<>();
        answers.put("q_101", new String[]{"A"}); // Correct
        Score score = quizService.calculateScore(sampleSportsQuizWithQuestions, answers);
        assertEquals(1, score.getCorrectAnswers());
        assertEquals(2, score.getTotalQuestions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScoreNullQuiz() {
         quizService.calculateScore(null, new HashMap<>()); // Pass null SportsQuiz
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_NullAnswers_ShouldThrowException() {
         quizService.calculateScore(sampleSportsQuizWithQuestions, null);
    }

    @Test
    public void calculateScoreQuizNoQuestions() {
        SportsQuiz emptyQuiz = new SportsQuiz(99, "empty", "Empty Quiz"); // Use SportsQuiz
        Map<String, String[]> answers = new HashMap<>();
        Score score = quizService.calculateScore(emptyQuiz, answers);
        assertEquals(0, score.getCorrectAnswers());
        assertEquals(0, score.getTotalQuestions());
    }
}