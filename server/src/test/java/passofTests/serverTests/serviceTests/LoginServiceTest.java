package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
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
    public void loginUser_ReturnsAuthTokenForValidUser() throws DataAccessException {
        // Setup - create a user to login
        String username = "testUser";

        // Execute
        HashMap<String, String> loginResponse = loginService.loginUser(username);

        // Verify
        assertNotNull(loginResponse, "Login response should not be null");
        assertEquals(username, loginResponse.get("username"), "The returned username should match the input username");
        assertNotNull(loginResponse.get("authToken"), "The response should include a non-null auth token");
    }


}
