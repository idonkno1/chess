package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameDAO;

import java.util.Collection;

public class ListGamesService {
    private final DataAccess dataAccess;
    public ListGamesService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public Collection<GameDAO> listGames(String authToken) throws DataAccessException {
        if(dataAccess.isValidAuth(authToken)){
            throw new DataAccessException("Error: unauthorized - invalid or expired authToken");
        }
        return dataAccess.listGames();
    }
}
