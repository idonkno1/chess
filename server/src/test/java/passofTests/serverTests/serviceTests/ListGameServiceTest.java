package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthDAO;
import model.GameDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ListGamesService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ListGameServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private ListGamesService listGamesService;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
        listGamesService = new ListGamesService(memoryDataAccess);
    }

    @AfterEach
    public void tearDown() {
        // Clear the database after each test to ensure a clean state
        memoryDataAccess.clearDAO();
    }
    @Test
    public void listGames_SuccessWithValidAuthToken() throws DataAccessException {
        // Setup - create an auth token and some games
        String username = "testUser";
        AuthDAO authToken = memoryDataAccess.createAuthToken(username);
        memoryDataAccess.createGame(new GameDAO(1, "whitePlayer", "blackPlayer", "Test Game 1", null));
        memoryDataAccess.createGame(new GameDAO(2, "whitePlayer2", "blackPlayer2", "Test Game 2", null));

        // Fix the logic to correctly check if the auth token is valid
        Collection<GameDAO> games = listGamesService.listGames(authToken.getAuthToken());

        // Verify
        assertNotNull(games, "Games collection should not be null.");
        assertEquals(2, games.size(), "There should be two games listed.");
    }

    @Test
    public void listGames_FailsWithInvalidAuthToken() {
        // Setup - an invalid auth token
        String invalidAuthToken = "invalidToken";

        // Execute & Verify
        assertThrows(DataAccessException.class, () -> listGamesService.listGames(invalidAuthToken), "Should throw an exception for invalid or expired authToken.");
    }
}
