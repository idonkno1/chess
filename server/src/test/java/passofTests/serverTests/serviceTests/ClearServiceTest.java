package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private ClearService clearService;

    @BeforeEach
    public void setUp() {
        // Initialize MemoryDataAccess
        memoryDataAccess = new MemoryDataAccess();
        // Initialize ClearService with MemoryDataAccess
        clearService = new ClearService(memoryDataAccess);
    }

    @Test
    public void clearDatabase_ClearsAllDataSuccessfully() throws DataAccessException {
        // Setup
        // Add a user
        memoryDataAccess.createUser(new UserData("username", "password", "email@example.com"));
        // Add an auth token
        AuthData auth = memoryDataAccess.createAuthToken("username");
        // Add a game
        memoryDataAccess.createGame("game");

        // Assertions before clearing to ensure data was added
        assertFalse(memoryDataAccess.listGames().isEmpty(), "Games should not be empty before clearing");
        assertFalse(memoryDataAccess.getAuthToken(String.valueOf(auth.authToken())) == null, "Auth token should exist before clearing");
        assertFalse(memoryDataAccess.getUser("username") == null, "User should exist before clearing");

        // Execute - clear the database
        clearService.clearDatabase();

        // Verify - all data has been cleared
        assertTrue(memoryDataAccess.listGames().isEmpty(), "Games should be empty after clearing");
        assertNull(memoryDataAccess.getAuthToken(String.valueOf(auth.authToken())), "Auth token should not exist after clearing");
        assertNull(memoryDataAccess.getUser("username"), "User should not exist after clearing");
    }

}
