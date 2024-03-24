package dataAccessTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


public class DataAccessTests {
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
    void createUserAndRetrieveUser(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        UserData user = new UserData("username", "password", "email@example.com");
        dataAccess.createUser(user);
        UserData retrievedUser = dataAccess.getUser(user.username());

        assertEquals(user.username(), retrievedUser.username());
        assertEquals(user.email(), retrievedUser.email());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    void createGameAndListGames(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        String gameName = "Test Game";
        GameData game = dataAccess.createGame(gameName);
        assertTrue(dataAccess.listGames().contains(game));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    void createAndDeleteAuthToken(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        String username = "testUser";
        AuthData authData = dataAccess.createAuthToken(username);
        assertNotNull(dataAccess.getAuthToken(authData.authToken()));

        dataAccess.deleteAuthToken(authData.authToken());
        assertNull(dataAccess.getAuthToken(authData.authToken()));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    void clearDAO(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        dataAccess.createUser(new UserData("username", "password", "email@example.com"));
        dataAccess.createGame("Test Game");
        dataAccess.createAuthToken("username");

        dataAccess.clearDAO();

        assertTrue(dataAccess.listGames().isEmpty());
        assertNull(dataAccess.getUser("username"));
        assertNull(dataAccess.getAuthToken("someAuthToken"));
    }
}

