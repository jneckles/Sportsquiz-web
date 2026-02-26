package edu.skidmore.cs276.lab04.test;

import org.junit.Before;
import org.junit.Test;

import edu.skidmore.cs276.lab04.beans.tasklist.Question;

import static org.junit.Assert.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Unit tests for the Question model class 
 */
public class QuestionTest { 

    private Question originalQuestion;

    @Before 
    public void setUp() {
        originalQuestion = new Question(101, "Original Question Text?");
        Map<String, String> options = new LinkedHashMap<>();
        options.put("A", "Option A");
        options.put("B", "Option B");
        originalQuestion.setOptions(options);
        originalQuestion.setCorrectAnswerKey("B");
    }

    @Test 
    public void addsOption() {
        Question q = new Question(1, "Test");
        q.addOption("X", "Option X");
        assertEquals("Options map size incorrect", 1, q.getOptions().size());
        assertEquals("Option text mismatch", "Option X", q.getOptions().get("X"));
    }

    @Test
    public void addOptionNullKeyOrVal() {
        Question q = new Question(1, "Test");
        q.addOption(null, "Option X");
        q.addOption("Y", null);
        assertTrue("Options map should be empty", q.getOptions().isEmpty());
    }

    @Test
    public void deepCopyIndependentCopy() {
        Question copiedQuestion = originalQuestion.deepCopy();

        assertNotSame("Copied object should not be the same instance.", originalQuestion, copiedQuestion);
        assertEquals("IDs should be equal.", originalQuestion.getId(), copiedQuestion.getId());
        assertEquals("Text should be equal.", originalQuestion.getText(), copiedQuestion.getText());
        assertEquals("Correct answer key should be equal.", originalQuestion.getCorrectAnswerKey(), copiedQuestion.getCorrectAnswerKey());
        assertEquals("Options map content should be equal.", originalQuestion.getOptions(), copiedQuestion.getOptions());
        assertNotSame("Options map instance should be different.", originalQuestion.getOptions(), copiedQuestion.getOptions());

        copiedQuestion.setText("Modified Text");
        copiedQuestion.setCorrectAnswerKey("A");
        copiedQuestion.getOptions().put("C", "Option C");

        assertEquals("Original text should remain unchanged.", "Original Question Text?", originalQuestion.getText());
        assertEquals("Original correct key should remain unchanged.", "B", originalQuestion.getCorrectAnswerKey());
        assertEquals("Original options map size should remain unchanged.", 2, originalQuestion.getOptions().size());
        assertFalse("Original options map should not contain key 'C'.", originalQuestion.getOptions().containsKey("C"));
    }

     @Test
    public void deepCopyNullOptions() {
        Question q = new Question(2, "No options");
        q.setOptions(null);
        Question copy = q.deepCopy();

        assertNotNull("Copy should not be null", copy);
        assertNotNull("Options map in copy should not be null after copy", copy.getOptions());
        assertTrue("Options map in copy should be empty", copy.getOptions().isEmpty());
    }
}
