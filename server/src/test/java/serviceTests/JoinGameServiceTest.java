package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.JoinGameService;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private JoinGameService joinGameService;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
        joinGameService = new JoinGameService(memoryDataAccess);
    }

    @AfterEach
    public void tearDown() {
        // Clear the database after each test to ensure a clean state
        memoryDataAccess.clearDAO();
    }
    @Test
    public void joinGame_SuccessfullyAsWhite() throws DataAccessException {
        // Setup
        var authToken = setupAuthToken("player1");
        var gameData = setupGame("game"); // No players initially
        JoinReqData joinReq = new JoinReqData("WHITE", 0);

        // Execute
        joinGameService.joinGame(joinReq, authToken.authToken());

        // Verify
        GameData game = memoryDataAccess.getGame(gameData.gameID());
        assertEquals("player1", game.whiteUsername(), "Player should successfully join as White");
        assertNull(game.blackUsername(), "Black player should still be null");
    }

    @Test
    public void joinGame_SuccessfullyAsBlack() throws DataAccessException {
        // Setup
        var authToken = setupAuthToken("player2");
        var gameData = setupGame("game"); // No players initially
        JoinReqData joinReq = new JoinReqData( "BLACK", 0);

        // Execute
        joinGameService.joinGame(joinReq, authToken.authToken());

        // Verify
        GameData game = memoryDataAccess.getGame(gameData.gameID());
        assertEquals("player2", game.blackUsername(), "Player should successfully join as Black");
        assertNull(game.whiteUsername(), "White player should still be null");
    }

    @Test
    public void joinGame_FailsWhenColorAlreadyTaken() throws DataAccessException {
        // Setup
        var authToken1 = setupAuthToken("player1");
        var authToken2 = setupAuthToken("player2");
        var gameData = setupGame("game");

        JoinReqData joinReq1 = new JoinReqData("WHITE", 0);
        JoinReqData joinReq2 = new JoinReqData("WHITE", 0);

        joinGameService.joinGame(joinReq1, authToken1.authToken());

        // Execute & Verify
        assertThrows(DataAccessException.class, () -> joinGameService.joinGame(joinReq2, authToken2.authToken()), "Should throw exception when trying to join a taken position");
    }

    private AuthData setupAuthToken(String username) {
        return memoryDataAccess.createAuthToken(username);
    }

    private GameData setupGame(String gameName) {
        return memoryDataAccess.createGame(gameName);
    }

}
