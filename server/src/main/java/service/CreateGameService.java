package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.GameDAO;

public class CreateGameService {
    public CreateGameService(MemoryDataAccess memoryDataAccess) {
    }

    public static GameDAO createGame(String authToken, GameDAO gameName) throws DataAccessException {
        if (MemoryDataAccess.isValidAuth(authToken)){
            throw new DataAccessException("Error: unauthorized - invalid or expired authToken");
        }

        if (gameName == null || gameName.getGameName().isEmpty()){
            throw new DataAccessException("Error: bad request - gameName is required");
        }

        return MemoryDataAccess.createGame(gameName);
    }
}
