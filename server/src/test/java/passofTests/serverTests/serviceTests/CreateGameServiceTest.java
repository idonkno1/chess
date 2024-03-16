package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
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
    public void createGame_SuccessfullyCreatesGame() throws DataAccessException {
        // Setup - a valid game data object
        GameData validGame = new GameData(0, "Player1", "Player2", "Chess Game", null);

        // Execute
        GameData createdGame = createGameService.createGame(validGame);

        // Verify
        assertNotNull(createdGame, "Game should be created successfully");
        assertEquals("Chess Game", createdGame.gameName(), "Created game should have the correct name");
    }

    @Test
    public void createGame_ThrowsException_ForInvalidGameData() {
        // Setup - an invalid game data object (null game name)
        GameData invalidGame = new GameData(1, "Player1", "Player2", null, null);

        // Execute & Verify
        assertThrows(DataAccessException.class, () -> createGameService.createGame(invalidGame),
                "Expected DataAccessException for invalid game data");
    }
}
