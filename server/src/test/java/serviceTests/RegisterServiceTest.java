package serviceTests;

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
    public void createUser_ReturnsUsernameAndAuthToken() throws DataAccessException {
        // Setup - define the user to be registered
        String username = "testUser";
        String password = "testPass";
        String email = "test@example.com";

        // Execute - simulate user registration
        HashMap<String, String> registrationResponse = registerService.createUser(username, password, email);

        // Verify - the response should contain the username and a non-null auth token
        assertNotNull(registrationResponse, "Registration response should not be null");
        assertEquals(username, registrationResponse.get("username"), "The returned username should match the input username");
        assertNotNull(registrationResponse.get("authToken"), "The response should include a non-null auth token");

        // Additional verification - ensure the user is actually created in the data access layer
        assertNotNull(memoryDataAccess.getUser(username), "The user should exist in the database after registration");
    }

}
