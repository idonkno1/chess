package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.ListGamesService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListGameServiceTest {
    private ListGamesService listGamesService;

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
    public void listGames_ReturnsEmptyCollection_IfNoGames(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Execute
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        listGamesService = new ListGamesService(memoryDataAccess);

        Collection games = listGamesService.listGames();

        // Verify
        assertTrue(games.isEmpty(), "Expected an empty collection when no games are added");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void listGames_ReturnsCorrectGameData_WhenGamesAreAdded(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - add some games
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        listGamesService = new ListGamesService(memoryDataAccess);

        GameData game1 = memoryDataAccess.createGame("game1");

        GameData game2 = memoryDataAccess.createGame("game2");


        // Execute
        Collection<GameData> games = listGamesService.listGames();


        // Verify
        assertEquals(2, games.size(), "Expected two games in the collection");
        assertTrue(games.containsAll(java.util.Arrays.asList(game1, game2)), "The collection should contain the added games");
    }


}
