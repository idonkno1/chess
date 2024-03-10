package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.UserDAO;
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
    public void loginUser_SuccessfulLogin() throws DataAccessException {
        // Setup - create a user
        String username = "testUser";
        String password = "testPassword";
        memoryDataAccess.createUser(new UserDAO(username, password, "email@example.com"));

        // Execute
        HashMap<String, String> response = loginService.loginUser(username, password);

        // Verify
        assertNotNull(response, "Response should not be null.");
        assertEquals(username, response.get("username"), "Username should match the logged in user.");
        assertNotNull(response.get("authToken"), "Auth token should not be null after successful login.");
    }

    @Test
    public void loginUser_FailsWithIncorrectCredentials() {
        // Setup - create a user
        String username = "testUser";
        String password = "testPassword";
        memoryDataAccess.createUser(new UserDAO(username, password, "email@example.com"));

        // Execute & Verify - incorrect username
        assertThrows(DataAccessException.class, () -> loginService.loginUser("wrongUsername", password), "Should throw an exception for incorrect username.");

        // Execute & Verify - incorrect password
        assertThrows(DataAccessException.class, () -> loginService.loginUser(username, "wrongPassword"), "Should throw an exception for incorrect password.");
    }
}
