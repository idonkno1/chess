package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

public class CreateGameService {

    private final DataAccess dataAccess;
    public CreateGameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public GameData createGame(GameData game) throws DataAccessException {
        var gameName = game.gameName();

        if (gameName == null){
            throw new DataAccessException("Error: bad request");
        }

        return dataAccess.createGame(gameName);
    }
}
