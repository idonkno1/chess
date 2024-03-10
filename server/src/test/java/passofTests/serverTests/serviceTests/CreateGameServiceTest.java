package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CreateGameService;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private CreateGameService createGameService;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
        createGameService = new CreateGameService(memoryDataAccess);
    }

    @AfterEach
    public void tearDown() {
        // Clear the database after each test to ensure a clean state
        memoryDataAccess.clearDAO();
    }
    @Test
    public void createGame_SuccessWithValidAuthToken() throws DataAccessException {
        // Setup - create an auth token and a game
        String username = "testUser";
        AuthData authToken = memoryDataAccess.createAuthToken(username);
        String game = "game";

        // Check if the auth token is valid
        GameData createdGame = createGameService.createGame(game);

        // Verify
        assertNotNull(createdGame, "Created game should not be null.");
        assertEquals("Test Game", createdGame.gameName(), "Game name should match.");
    }

    @Test
    public void createGame_FailsWithInvalidAuthToken() {
        // Setup - an invalid auth token and a game
        String invalidAuthToken = "invalidToken";
        String game = "game";
        // Execute & Verify
        assertThrows(DataAccessException.class, () -> createGameService.createGame(game), "Should throw an exception for invalid or expired authToken.");
    }

    @Test
    public void createGame_FailsWithInvalidGameName() throws DataAccessException {
        // Setup - create a valid auth token and an invalid game (empty game name)
        String username = "testUser";
        AuthData authToken = memoryDataAccess.createAuthToken(username);
        String invalidGame = "";
        // Execute & Verify
        assertThrows(DataAccessException.class, () -> createGameService.createGame(invalidGame), "Should throw an exception for bad request - gameName is required.");
    }

}
