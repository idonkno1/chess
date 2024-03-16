package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoginService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
        loginService = new LoginService(memoryDataAccess);
    }

    @AfterEach
    public void tearDown() {
        // Clear the database after each test to ensure a clean state
        memoryDataAccess.clearDAO();
    }
    @Test
    public void loginUser_Successfully() throws DataAccessException {
        // Setup - create a user
        String username = "testUser";
        String password = "testPass";
        memoryDataAccess.createUser(new UserData(username, password, "test@example.com"));

        // Execute
        HashMap<String, String> result = loginService.loginUser(username, password);

        // Verify
        assertNotNull(result, "Login result should not be null");
        assertEquals(username, result.get("username"), "Logged in username should match");
        assertNotNull(result.get("authToken"), "Auth token should not be null");
    }

    @Test
    public void loginUser_Fails_WithIncorrectCredentials() {
        // Setup - create a user
        String username = "testUser";
        String password = "testPass";
        memoryDataAccess.createUser(new UserData(username, password, "test@example.com"));

        // Verify - Incorrect password
        assertThrows(DataAccessException.class, () -> loginService.loginUser(username, "wrongPassword"), "Should throw exception for wrong password");

        // Verify - Incorrect username
        assertThrows(DataAccessException.class, () -> loginService.loginUser("wrongUser", password), "Should throw exception for wrong username");
    }

    @Test
    public void loginUser_Fails_WithEmptyCredentials() {
        // Verify - Empty username
        assertThrows(DataAccessException.class, () -> loginService.loginUser("", "anyPassword"), "Should throw exception for empty username");

        // Verify - Empty password
        assertThrows(DataAccessException.class, () -> loginService.loginUser("anyUser", ""), "Should throw exception for empty password");
    }


}
