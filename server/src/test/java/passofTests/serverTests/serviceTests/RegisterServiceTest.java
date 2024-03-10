package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RegisterService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class RegisterServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private RegisterService registerService;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
        registerService = new RegisterService(memoryDataAccess);
    }
    @AfterEach
    public void tearDown() {
        // Clear the database after each test
        memoryDataAccess.clearDAO();
    }

    @Test
    public void createUser_Success() throws DataAccessException {
        HashMap<String, String> response = registerService.createUser("newUser", "password", "email@example.com");
        assertNotNull(response.get("authToken"), "Auth token should not be null.");
        assertEquals("newUser", response.get("username"), "Username should match.");
    }

    @Test
    public void createUser_FailsWithMissingDetails() {
        assertThrows(DataAccessException.class, () -> registerService.createUser("", "password", "email@example.com"),
                "Error: bad request - missing username, password, or email");
        assertThrows(DataAccessException.class, () -> registerService.createUser("newUser", "", "email@example.com"),
                "Error: bad request - missing username, password, or email");
        assertThrows(DataAccessException.class, () -> registerService.createUser("newUser", "password", ""),
                "Error: bad request - missing username, password, or email");
    }

    @Test
    public void createUser_FailsWithDuplicateUsername() throws DataAccessException {
        registerService.createUser("newUser", "password", "email@example.com"); // First registration should succeed
        // Attempt to register the same username again
        assertThrows(DataAccessException.class, () -> registerService.createUser("newUser", "newPassword", "newEmail@example.com"),
                "Error: already taken");
    }

}
