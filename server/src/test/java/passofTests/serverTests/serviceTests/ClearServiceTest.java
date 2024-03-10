package passofTests.serverTests.serviceTests;

import dataAccess.*;
import model.AuthDAO;
import model.UserDAO;
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
    public void clearDatabase_ClearsDataSuccessfully() throws DataAccessException {
        // Setup - add some data
        memoryDataAccess.createUser(new UserDAO("username", "password", "email@example.com"));
        AuthDAO authToken =  memoryDataAccess.createAuthToken("username");


        // Pre-assertions to verify the setup
        assertNotNull(memoryDataAccess.getAuthToken(authToken), "Auth token should not be null before clearing.");

        // Execute
        clearService.clearDatabase();

        // Verify that data has been cleared
        assertNull(memoryDataAccess.getAuthToken(authToken), "Auth token should be null after clearing.");
    }

}
