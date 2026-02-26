package edu.skidmore.cs276.lab04.test;

import org.junit.Test;

import edu.skidmore.cs276.lab04.webapps.web.AuthService;

import static org.junit.Assert.*;

/**
 *  tests for the AuthService static utility class 
 */
public class AuthServiceTest { 

    @Test 
    public void validateCredentialsTrue() { 
        assertTrue("Valid: 'testUser' -> 'resUtset2'", AuthService.validateCredentials("testUser", "resUtset2"));
        assertTrue("Valid: 'a' -> 'a2'", AuthService.validateCredentials("a", "a2"));
        assertTrue("Valid: '123' -> '3212'", AuthService.validateCredentials("123", "3212"));
        assertTrue("Valid: ' space ' -> ' ecaps 2' (trims username)", AuthService.validateCredentials(" space ", " ecaps 2"));
    }

    @Test
    public void invalidPassFalse() {
        assertFalse("Invalid: Password not reversed", AuthService.validateCredentials("testUser", "testUser2"));
        assertFalse("Invalid: Missing '2'", AuthService.validateCredentials("testUser", "resutset")); 
        assertFalse("Invalid: Extra chars", AuthService.validateCredentials("testUser", "resUtset2_extra"));
        assertFalse("Invalid: Completely wrong", AuthService.validateCredentials("testUser", "wrongpassword"));
    }

    @Test
    public void nullUserFalse() {
        assertFalse("Invalid: Null username", AuthService.validateCredentials(null, "password2"));
    }

    @Test
    public void emptyUserFalse() {
        assertFalse("Invalid: Empty username", AuthService.validateCredentials("", "2"));
        assertFalse("Invalid: Blank username (after trim)", AuthService.validateCredentials(" ", " 2"));
    }

    @Test
    public void nullPassFalse() {
        assertFalse("Invalid: Null password", AuthService.validateCredentials("testUser", null));
    }

    @Test
    public void bothNullFalse() {
         assertFalse("Invalid: Both null", AuthService.validateCredentials(null, null));
    }
}
