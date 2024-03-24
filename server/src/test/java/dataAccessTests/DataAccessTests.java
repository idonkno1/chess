package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLException;
import java.util.Collection;

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
    void isValidAuth_ValidToken_ReturnsTrue(Class<? extends DataAccess> dataAccessClass) throws DataAccessException, SQLException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        String username = "user";
        AuthData authData = dataAccess.createAuthToken(username);
        assertTrue(dataAccess.isValidAuth(authData.authToken()));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    void isValidAuth_InvalidToken_ReturnsFalse(Class<? extends DataAccess> dataAccessClass) throws DataAccessException, SQLException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        assertFalse(dataAccess.isValidAuth("nonexistentToken"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    void createUser_ValidData_CreatesUserSuccessfully(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);


        UserData userData = new UserData("user", "pass", "email@example.com");
        UserData createdUser = dataAccess.createUser(userData);
        assertEquals(userData, createdUser);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    void deleteAuthToken_NonExistingToken_CompletesWithoutError(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        assertDoesNotThrow(() -> dataAccess.deleteAuthToken("nonexistentToken"));
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
    @ValueSource(classes = {MySqlDataAccess.class})
    void createGame_NameTooLong_ThrowsException(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        // Generate a game name longer than the maximum allowed length
        String longGameName = "G".repeat(300);

        // Expect an exception due to exceeding the maximum length
        assertThrows(RuntimeException.class, () -> dataAccess.createGame(longGameName),
                "Expected creating a game with a name too long to throw an exception.");
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

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    void updateGame_ValidGame_UpdatesSuccessfully(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);
        // Create a game to update
        String initialGameName = "Initial Game";
        GameData game = dataAccess.createGame(initialGameName);
        assertNotNull(game, "Precondition failed: game should be created successfully.");

        // Update the game
        ChessGame updatedGameState = new ChessGame(); // Assuming some state changes
        GameData updatedGame = new GameData(game.gameID(), "player1", "player2", "Updated Game", updatedGameState);
        assertDoesNotThrow(() -> dataAccess.updateGame(updatedGame), "Game should update without throwing an exception.");

        // Fetch the updated game and verify changes
        GameData fetchedGame = dataAccess.getGame(game.gameID());
        assertNotNull(fetchedGame, "Updated game should exist.");
        assertEquals("player1", fetchedGame.whiteUsername(), "White username should be updated.");
        assertEquals("player2", fetchedGame.blackUsername(), "Black username should be updated.");
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class})
    void updateGame_NonexistentGame_DoesNotThrowException(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dataAccessClass);

        ChessGame gameToUpdateState = new ChessGame(); // Assuming some state
        GameData gameToUpdate = new GameData(99999, "nonexistentPlayer1", "nonexistentPlayer2", "Nonexistent Game", gameToUpdateState);

        assertDoesNotThrow(() -> dataAccess.updateGame(gameToUpdate), "Updating a nonexistent game should not throw an exception.");

        Collection<GameData> allGames = dataAccess.listGames();
        assertTrue(allGames.stream().noneMatch(g -> "Nonexistent Game".equals(g.gameName())), "No games should be updated to the nonexistent game name.");
    }
}

