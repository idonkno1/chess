package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.GameDAO;

import java.util.Collection;

public class ListGamesService {
    public ListGamesService(MemoryDataAccess memoryDataAccess) {
    }

    public static Collection<GameDAO> listGames(String authToken) throws DataAccessException {
        if(MemoryDataAccess.isValidAuth(authToken)){
            throw new DataAccessException("Error: unauthorized - invalid or expired authToken");
        }
        return MemoryDataAccess.listGames();
    }
}
