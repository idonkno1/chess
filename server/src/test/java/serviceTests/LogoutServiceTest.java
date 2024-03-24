package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.LogoutService;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogoutServiceTest {
    private LogoutService logoutService;

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
    public void logoutUser_SuccessfullyDeletesAuthToken(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - create an auth token
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        logoutService = new LogoutService(memoryDataAccess);

        String username = "testUser";
        AuthData auth = memoryDataAccess.createAuthToken(username);
        String authToken = auth.authToken();

        // Execute
        logoutService.logoutUser(authToken);

        // Verify - the auth token should be deleted
        assertNull(memoryDataAccess.getAuthToken(authToken), "Auth token should be null after logout");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void logoutUser_ThrowsException_IfAuthTokenIsEmpty(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Execute & Verify
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        logoutService = new LogoutService(memoryDataAccess);

        assertThrows(DataAccessException.class, () -> logoutService.logoutUser(""), "Expected DataAccessException for empty authToken");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void logoutUser_ThrowsException_IfAuthTokenDoesNotExist(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - a non-existent auth token
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        logoutService = new LogoutService(memoryDataAccess);

        String nonExistentToken = "nonExistentToken";

        // Execute & Verify
        assertThrows(DataAccessException.class, () -> logoutService.logoutUser(nonExistentToken), "Expected DataAccessException for non-existent authToken");
    }
    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void logoutUser_ThrowsException_OnSecondLogoutAttempt(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - create an auth token and logout once successfully
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        logoutService = new LogoutService(memoryDataAccess);

        String username = "testUser";
        AuthData auth = memoryDataAccess.createAuthToken(username);
        String authToken = auth.authToken();
        logoutService.logoutUser(authToken); // First logout attempt

        // Execute & Verify - second logout attempt should throw an exception
        assertThrows(DataAccessException.class, () -> logoutService.logoutUser(authToken), "Expected DataAccessException on second logout attempt with the same authToken");
    }
}
