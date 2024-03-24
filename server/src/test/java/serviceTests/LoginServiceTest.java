package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.LoginService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private LoginService loginService;

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
    public void loginUser_Successfully(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - create a user
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        loginService = new LoginService(memoryDataAccess);

        String username = "testUser";
        String password = "testPass";
        memoryDataAccess.createUser(new UserData(username, password, "test@example.com"));

        // Execute
        HashMap<String, String> result = loginService.loginUser(username, password);

        // Verify
        assertNotNull(result, "Login result should not be null");
        assertEquals(username, result.get("username"), "Logged in username should match");
        assertNotNull(result.get("authToken"), "Auth token should not be null");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void loginUser_Fails_WithIncorrectCredentials(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - create a user
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        loginService = new LoginService(memoryDataAccess);

        String username = "testUser";
        String password = "testPass";
        memoryDataAccess.createUser(new UserData(username, password, "test@example.com"));

        // Verify - Incorrect password
        assertThrows(DataAccessException.class, () -> loginService.loginUser(username, "wrongPassword"), "Should throw exception for wrong password");

        // Verify - Incorrect username
        assertThrows(DataAccessException.class, () -> loginService.loginUser("wrongUser", password), "Should throw exception for wrong username");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void loginUser_Fails_WithEmptyCredentials(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Verify - Empty username
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        loginService = new LoginService(memoryDataAccess);

        assertThrows(DataAccessException.class, () -> loginService.loginUser("", "anyPassword"), "Should throw exception for empty username");

        // Verify - Empty password
        assertThrows(DataAccessException.class, () -> loginService.loginUser("anyUser", ""), "Should throw exception for empty password");
    }


}
