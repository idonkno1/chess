package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.JoinReqData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.JoinGameService;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {
    private JoinGameService joinGameService;

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
    public void joinGame_SuccessfullyAsWhite(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        joinGameService = new JoinGameService(memoryDataAccess);

        var authToken = setupAuthToken(memoryDataAccess, "player1");
        var gameData = setupGame(memoryDataAccess, "game"); // No players initially
        JoinReqData joinReq = new JoinReqData("WHITE", 1);

        // Execute
        joinGameService.joinGame(joinReq, authToken.authToken());

        // Verify
        GameData game = memoryDataAccess.getGame(gameData.gameID());
        assertEquals("player1", game.whiteUsername(), "Player should successfully join as White");
        assertNull(game.blackUsername(), "Black player should still be null");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void joinGame_SuccessfullyAsBlack(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        joinGameService = new JoinGameService(memoryDataAccess);

        var authToken = setupAuthToken(memoryDataAccess, "player2");
        var gameData = setupGame(memoryDataAccess, "game"); // No players initially
        JoinReqData joinReq = new JoinReqData( "BLACK", 1);

        // Execute
        joinGameService.joinGame(joinReq, authToken.authToken());

        // Verify
        GameData game = memoryDataAccess.getGame(gameData.gameID());
        assertEquals("player2", game.blackUsername(), "Player should successfully join as Black");
        assertNull(game.whiteUsername(), "White player should still be null");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void joinGame_FailsWhenColorAlreadyTaken(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        joinGameService = new JoinGameService(memoryDataAccess);

        var authToken1 = setupAuthToken(memoryDataAccess, "player1");
        var authToken2 = setupAuthToken(memoryDataAccess, "player2");
        var gameData = setupGame(memoryDataAccess, "game");

        JoinReqData joinReq1 = new JoinReqData("WHITE", 1);
        JoinReqData joinReq2 = new JoinReqData("WHITE", 1);

        joinGameService.joinGame(joinReq1, authToken1.authToken());

        // Execute & Verify
        assertThrows(DataAccessException.class, () -> joinGameService.joinGame(joinReq2, authToken2.authToken()), "Should throw exception when trying to join a taken position");
    }

    private AuthData setupAuthToken(DataAccess memoryDataAccess, String username) {
        return memoryDataAccess.createAuthToken(username);
    }

    private GameData setupGame(DataAccess memoryDataAccess, String gameName) {
        return memoryDataAccess.createGame(gameName);
    }

}
