package edu.skidmore.cs276.lab04.beans.tasklist;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Represents the score achieved by a user on a specific quiz attempt.
 * Implements Serializable.
 */
public class Score implements Serializable {

	private static final long serialVersionUID = 4L;

	private final int correctAnswers;
	private final int totalQuestions;
	

	public Score(int correctAnswers, int totalQuestions) {
		if (totalQuestions < 0 || correctAnswers < 0 || correctAnswers > totalQuestions) {
			throw new IllegalArgumentException(
					"Invalid score values: correctAnswers=" + correctAnswers + ", totalQuestions=" + totalQuestions);
		}
		this.correctAnswers = correctAnswers;
		this.totalQuestions = totalQuestions;
	}

	
	public int getCorrectAnswers() {
		return correctAnswers;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	/**
	 * Calculates the score as a percentage.
	 * 
	 * @return The score percentage (0.0 to 100.0), or 0.0 if totalQuestions is 0.
	 */
	public double getPercentage() {
		if (totalQuestions == 0) {
			return 0.0;
		}
		// Multiply by 100.0 to ensure floating-point division
		return ((double) correctAnswers / totalQuestions) * 100.0;
	}

	/**
	 * Returns the score percentage formatted as a string (e.g., "75.0%").
	 * 
	 * @return Formatted % string.
	 */
	public String getFormattedPercentage() {
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1); // e.g., 75.0%
		// The percentage value needs to be between 0.0 and 1.0 for
		// NumberFormat.getPercentInstance()
		double decimalFraction = (totalQuestions == 0) ? 0.0 : ((double) correctAnswers / totalQuestions);
		return percentFormat.format(decimalFraction);
	}

	
	@Override
	public String toString() {
		return "Score{" + "correctAnswers=" + correctAnswers + ", totalQuestions=" + totalQuestions + ", percentage="
				+ getPercentage() + "%" + '}';
	}
}
