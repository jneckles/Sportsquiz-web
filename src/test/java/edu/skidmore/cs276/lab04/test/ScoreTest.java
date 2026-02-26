package edu.skidmore.cs276.lab04.test;

import org.junit.Test;

import edu.skidmore.cs276.lab04.beans.tasklist.Score;

import static org.junit.Assert.*;


/**
 * Unit tests for the Score model class
 */
public class ScoreTest { 

    @Test 
    public void createInstance() {
        Score score = new Score(8, 10);
        assertNotNull(score);
        assertEquals("Correct answers mismatch", 8, score.getCorrectAnswers());
        assertEquals("Total questions mismatch", 10, score.getTotalQuestions());

        Score scoreZero = new Score(0, 5);
        assertEquals(0, scoreZero.getCorrectAnswers());
        assertEquals(5, scoreZero.getTotalQuestions());

        Score scorePerfect = new Score(5, 5);
        assertEquals(5, scorePerfect.getCorrectAnswers());
        assertEquals(5, scorePerfect.getTotalQuestions());

        Score scoreZeroTotal = new Score(0, 0);
        assertEquals(0, scoreZeroTotal.getCorrectAnswers());
        assertEquals(0, scoreZeroTotal.getTotalQuestions());
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void constructorCorrectMoreThanTotal() {
        new Score(11, 10); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNegativeCorrect() {
        new Score(-1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNegativeTotal() {
        new Score(5, -1);
    }

    @Test
    public void getPercentageCalculated() {
        Score score1 = new Score(8, 10);
        assertEquals("8/10 percentage incorrect", 80.0, score1.getPercentage(), 0.001);

        Score score2 = new Score(4, 4);
        assertEquals("4/4 percentage incorrect", 100.0, score2.getPercentage(), 0.001);

        Score score3 = new Score(0, 10);
        assertEquals("0/10 percentage incorrect", 0.0, score3.getPercentage(), 0.001);

        Score score4 = new Score(1, 3);
        assertEquals("1/3 percentage incorrect", 33.333, score4.getPercentage(), 0.001);

        Score score5 = new Score(0, 0);
        assertEquals("0/0 percentage incorrect", 0.0, score5.getPercentage(), 0.001);
    }

    @Test
    public void getFormattedPercent() {
         Score score1 = new Score(8, 10); // 80%
         assertTrue("Formatted percentage for 8/10 should contain 80", score1.getFormattedPercentage().contains("80"));
         assertTrue("Formatted percentage should contain %", score1.getFormattedPercentage().contains("%"));

         Score score2 = new Score(1, 3); // 33.3%
         assertTrue("Formatted percentage for 1/3 should contain 33.3", score2.getFormattedPercentage().contains("33.3"));

         Score score3 = new Score(0, 0); // 0%
         assertTrue("Formatted percentage for 0/0 should contain 0", score3.getFormattedPercentage().contains("0"));
    }
}
