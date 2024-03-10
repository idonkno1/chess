package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LogoutService;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private LogoutService logoutService;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
        logoutService = new LogoutService(memoryDataAccess);
    }

    @AfterEach
    public void tearDown() {
        // Clear the database after each test to ensure a clean state
        memoryDataAccess.clearDAO();
    }


    @Test
    public void logoutUser_RemovesAuthTokenSuccessfully() throws DataAccessException {
        // Setup - simulate user login by creating an auth token
        String username = "testUser";
        AuthData authData = memoryDataAccess.createAuthToken(username);
        String authToken = authData.authToken();

        // Pre-assertion to verify the auth token exists before logout
        assertNotNull(memoryDataAccess.getAuthToken(authToken), "Auth token should exist before logout");

        // Execute - simulate user logout
        logoutService.logoutUser(authToken);

        // Verify - the auth token should be removed after logout
        assertNull(memoryDataAccess.getAuthToken(authToken), "Auth token should be removed after logout");
    }
}
