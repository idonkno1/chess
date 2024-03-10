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
        // Setup - create a user and generate an auth token
        String username = "testUser";
        AuthData authToken = memoryDataAccess.createAuthToken(username);
        assertNotNull(memoryDataAccess.getAuthToken(authToken.authToken()), "Auth token should exist before logout.");

        // Execute - attempt to logout the user
        logoutService.logoutUser(authToken.authToken());

        // Verify - the auth token should no longer exist
        assertNull(memoryDataAccess.getAuthToken(authToken.authToken()), "Auth token should be removed after logout.");
    }

    @Test
    public void logoutUser_WithNonexistentTokenDoesThrowException() {
        // Setup - a non-existing auth token
        String nonExistentAuthToken = "nonExistentToken";

        // Execute & Verify - attempting to logout with a non-existing token should throw an exception
        assertThrows(DataAccessException.class,() -> logoutService.logoutUser(nonExistentAuthToken), "Logging out with a non-existent token should throw an exception.");
    }
}
