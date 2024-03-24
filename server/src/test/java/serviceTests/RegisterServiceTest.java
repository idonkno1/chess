package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.RegisterService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class RegisterServiceTest {
    private RegisterService registerService;

    private DataAccess getDataAccess(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess;
        if (dataAccessClass.equals(MemoryDataAccess.class)) {
            dataAccess = new MemoryDataAccess();
        } else {
            dataAccess = new MySqlDataAccess();
        }
        dataAccess.clearDAO();
        return dataAccess;
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void createUser_ReturnsUsernameAndAuthToken(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - define the user to be registered
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        registerService = new RegisterService(memoryDataAccess);

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
