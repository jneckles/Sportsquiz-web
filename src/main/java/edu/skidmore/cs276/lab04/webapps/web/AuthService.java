package edu.skidmore.cs276.lab04.webapps.web;

import org.apache.log4j.Logger;

/**
 * Provides methods for validating user
 * credentials based on the password rules.
 */
public class AuthService {
	
	private static Logger LOG = Logger.getLogger(AuthService.class);

	// constructor to prevent instantiation of utility class
	private AuthService() {
	}

	/**
	 * Validates the user's credentials based on the rule: password should be the
	 * username reversed plus the number '2'.
	 *
	 * @param username        The username entered by the user.
	 * @param enteredPassword The password entered by the user.
	 * @return true if the credentials are valid according to the rule, false
	 *         otherwise.
	 */
	public static boolean validateCredentials(String username, String enteredPassword) {
		// null/empty checks
		if (username == null || username.trim().isEmpty() || enteredPassword == null) {
			LOG.trace("AuthService: Validation failed - null or empty input."); 
			return false;
		}

		// Trims the username before reversing
		String trimmedUsername = username.trim();

		// Calculate the expected password based on the rule
		String expectedPassword = new StringBuilder(trimmedUsername).reverse().toString() + "2";

		// Compare the entered password with the expected password
		boolean isValid = enteredPassword.equals(expectedPassword);

		if (!isValid) {
			LOG.trace("AuthService: Validation failed for user '" + trimmedUsername + "'. Expected: '"
					+ expectedPassword + "', Got: '" + enteredPassword + "'"); 
		}

		return isValid;
	}


}
