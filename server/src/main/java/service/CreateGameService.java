package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

public class CreateGameService {

    private final DataAccess dataAccess;
    public CreateGameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public GameData createGame(String gameName) throws DataAccessException {

        if (gameName == null || gameName.isEmpty()){
            throw new DataAccessException("Error: bad request");
        }

        return dataAccess.createGame(gameName);
    }
}
