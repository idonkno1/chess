package passofTests.serverTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ListGamesService;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void listGames_ReturnsEmptyCollection_IfNoGames() throws DataAccessException {
        // Execute
        ArrayList<Object> games = listGamesService.listGames();

        // Verify
        assertTrue(games.isEmpty(), "Expected an empty collection when no games are added");
    }

    @Test
    public void listGames_ReturnsCorrectGameData_WhenGamesAreAdded() throws DataAccessException {
        // Setup - add some games
        GameData game1 = memoryDataAccess.createGame("game1");
        var gameT1 = prepGame(game1);
        GameData game2 = memoryDataAccess.createGame("game2");
        var gameT2 = prepGame(game1);

        // Execute
        ArrayList<Object> games = listGamesService.listGames();


        // Verify
        assertEquals(2, games.size(), "Expected two games in the collection");
        assertTrue(games.containsAll(java.util.Arrays.asList(gameT1, gameT2)), "The collection should contain the added games");
    }

    public HashMap<String, Object> prepGame(GameData game){
        var gameMap = new HashMap<String, Object>();
        gameMap.put("gameId", game.gameID());
        gameMap.put("whiteUsername", game.whiteUsername());
        gameMap.put("blackUsername", game.blackUsername());
        gameMap.put("gameName", game.gameName());

        return gameMap;
    }

}
