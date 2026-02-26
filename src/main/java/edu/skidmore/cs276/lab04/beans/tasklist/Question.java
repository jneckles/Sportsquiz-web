package edu.skidmore.cs276.lab04.beans.tasklist; // Adjust package if needed

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects; // Added for potential equals/hashCode

/**
 * Represents a single question in a Quiz. Contains the question text,
 * multiple-choice options, the key identifying the correct answer,
 * and the ID of the parent quiz.
 * 
 */
public class Question implements Serializable {

    private static final long serialVersionUID = 2L; 

    private int id; // ID of the question itself
    private int quizId; // Foreign key to the parent SportsQuiz 
    private String text;
    private Map<String, String> options;
    private String correctAnswerKey;

    /**
     * Constructor often used by DAO when getting from DB.
     * Has the quizId.
     */
    public Question(int id, int quizId, String text) {
        this.id = id;
        this.quizId = quizId; // Initialize quizId
        this.text = text;
        this.options = new LinkedHashMap<>();
    }

    /**
     * Constructor possibly used when creating a new Question object
     * before saving ID is set to 0.
     * 
     */
     public Question(int quizId, String text) {
        this.id = 0; 
        this.quizId = quizId;
        this.text = text;
        this.options = new LinkedHashMap<>();
     }


    /**
     * No-arg constructor. Initializes the options map.
     * set quizId using setQuizId if using this constructor.
     */
    public Question(int quizId) {
        this.options = new LinkedHashMap<>();
        this.quizId = quizId;
        
    }

    
    public int getId() { return id; }
    public int getQuizId() { return quizId; } 
    public String getText() { return text; }
    public Map<String, String> getOptions() { return options; }
    public String getCorrectAnswerKey() { return correctAnswerKey; }

   
    public void setId(int id) { this.id = id; }
    public void setQuizId(int quizId) { this.quizId = quizId; } 
    public void setText(String text) { this.text = text; }
    public void setOptions(Map<String, String> options) {
        this.options = (options != null) ? new LinkedHashMap<>(options) : new LinkedHashMap<>();
    }
    public void setCorrectAnswerKey(String correctAnswerKey) {
        this.correctAnswerKey = correctAnswerKey;
    }

    
    public void addOption(String key, String value) {
        if (key != null && value != null) {
            this.options.put(key, value);
        }
    }

    /**
     * Creates a deep copy of this Question object.
     * Includes copying the quizId.
     * @return A new Question instance with copied data.
     */
    public Question deepCopy() {
        // Use constructor that takes quizId
        Question copy = new Question(this.id, this.quizId, this.text);
        if (this.options != null) {
            copy.options = new LinkedHashMap<>(this.options);
        }
        copy.correctAnswerKey = this.correctAnswerKey;
        return copy;
    }

    @Override
    public String toString() {
        return "Question{" +
               "id=" + id +
               ", quizId=" + quizId + // Include quizId
               ", text='" + text + '\'' +
               ", options=" + options +
               ", correctAnswerKey='" + correctAnswerKey + '\'' +
               '}';
    }

    //Implement equals and hashCode based on ID if needed
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id == question.id; // Equality based on question ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}