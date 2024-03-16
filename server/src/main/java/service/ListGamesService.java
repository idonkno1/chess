package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public class ListGamesService {
    private final DataAccess dataAccess;
    public ListGamesService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public Collection<GameData> listGames() throws DataAccessException {
        return dataAccess.listGames();
    }
}
