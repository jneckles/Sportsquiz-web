package edu.skidmore.cs276.lab04.beans.tasklist;

import java.io.Serializable;
import java.util.Objects;

/**
 * Basic representation of a User. only holds the username used for
 * login id.
 */
public class User implements Serializable {

	private static final long serialVersionUID = 5L;

	private String username;
	

	public User(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}
		this.username = username.trim();
	}

	public User() {
	}

	//  Getters 
	public String getUsername() {
		return username;
	}

	//  Setters
	public void setUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}
		this.username = username.trim();
	}

	
	@Override
	public String toString() {
		return "User{" + "username='" + username + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User user = (User) o;
		return Objects.equals(username, user.username); // Equality based on username
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
}
