package serviceTests;

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
    public void logoutUser_SuccessfullyDeletesAuthToken() throws DataAccessException {
        // Setup - create an auth token
        String username = "testUser";
        AuthData auth = memoryDataAccess.createAuthToken(username);
        String authToken = auth.authToken();

        // Execute
        logoutService.logoutUser(authToken);

        // Verify - the auth token should be deleted
        assertNull(memoryDataAccess.getAuthToken(authToken), "Auth token should be null after logout");
    }

    @Test
    public void logoutUser_ThrowsException_IfAuthTokenIsEmpty() {
        // Execute & Verify
        assertThrows(DataAccessException.class, () -> logoutService.logoutUser(""), "Expected DataAccessException for empty authToken");
    }

    @Test
    public void logoutUser_ThrowsException_IfAuthTokenDoesNotExist() {
        // Setup - a non-existent auth token
        String nonExistentToken = "nonExistentToken";

        // Execute & Verify
        assertThrows(DataAccessException.class, () -> logoutService.logoutUser(nonExistentToken), "Expected DataAccessException for non-existent authToken");
    }
    @Test
    public void logoutUser_ThrowsException_OnSecondLogoutAttempt() throws DataAccessException {
        // Setup - create an auth token and logout once successfully
        String username = "testUser";
        AuthData auth = memoryDataAccess.createAuthToken(username);
        String authToken = auth.authToken();
        logoutService.logoutUser(authToken); // First logout attempt

        // Execute & Verify - second logout attempt should throw an exception
        assertThrows(DataAccessException.class, () -> logoutService.logoutUser(authToken), "Expected DataAccessException on second logout attempt with the same authToken");
    }
}
