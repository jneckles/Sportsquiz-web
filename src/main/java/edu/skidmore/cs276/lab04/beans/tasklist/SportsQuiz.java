package edu.skidmore.cs276.lab04.beans.tasklist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SportsQuiz represents a quiz for a specific sport
 */
public class SportsQuiz implements Serializable {

	private static final long serialVersionUID = 1L; // serialVersionUID for Serializable

	private int id;
	private String category;
	private String title;
	private List<Question> questions;

	// Constructor used by DAO when getting from DB
	public SportsQuiz(int id, String category, String title) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.questions = new ArrayList<>(); // Initialize empty list
	}

	
	public SportsQuiz() {
		this.questions = new ArrayList<>();
	}

	
	public int getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}

	public String getTitle() {
		return title;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	
	public void setId(int id) {
		this.id = id;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = (questions != null) ? new ArrayList<>(questions) : new ArrayList<>();
	}

	

	/**
	 * Adds a question to this quiz.
	 * 
	 * @param question The Question object to add.
	 */
	public void addQuestion(Question question) {
		if (question != null) {
			this.questions.add(question);
		}
	}

	/**
	 * Creates a deep copy of this Quiz object. Useful for preventing shared mutable
	 * state issues, especially with in-memory DAOs.
	 * 
	 * @return A new Quiz instance with copied data.
	 */
	public SportsQuiz deepCopy() {
		SportsQuiz copy = new SportsQuiz(this.id, this.category, this.title);
		// Ensure questions are also deep copied
		if (this.questions != null) {
			copy.questions = this.questions.stream().map(Question::deepCopy) // Requires Question to have deepCopy
					.collect(Collectors.toList());
		}
		return copy;
	}

	
	@Override
	public String toString() {
		return "Quiz{" + "id=" + id + ", category='" + category + '\'' + ", title='" + title + '\''
				+ ", questionsCount=" + (questions != null ? questions.size() : 0) + '}';
	}

	
}