package serviceTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.CreateGameService;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {
    private CreateGameService createGameService;

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
    public void createGame_SuccessfullyCreatesGame(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - a valid game data object
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        createGameService = new CreateGameService(memoryDataAccess);

        GameData validGame = new GameData(0, "Player1", "Player2", "Chess Game", null);

        // Execute
        GameData createdGame = createGameService.createGame(validGame);

        // Verify
        assertNotNull(createdGame, "Game should be created successfully");
        assertEquals("Chess Game", createdGame.gameName(), "Created game should have the correct name");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class, MySqlDataAccess.class})
    public void createGame_ThrowsException_ForInvalidGameData(Class<? extends DataAccess> dataAccessClass) throws DataAccessException {
        // Setup - an invalid game data object (null game name)
        DataAccess memoryDataAccess = getDataAccess(dataAccessClass);
        createGameService = new CreateGameService(memoryDataAccess);
        GameData invalidGame = new GameData(1, "Player1", "Player2", null, null);

        // Execute & Verify
        assertThrows(Exception.class, () -> createGameService.createGame(invalidGame),
                "Expected DataAccessException for invalid game data");
    }
}
