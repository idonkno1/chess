package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private ClearService clearService;

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
    public void clearDatabase_ClearsAllDataSuccessfully(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        clearService = new ClearService(memoryDataAccess);

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
