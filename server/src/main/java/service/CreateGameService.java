package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameDAO;

public class CreateGameService {

    private final DataAccess dataAccess;
    public CreateGameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public GameDAO createGame(String authToken, GameDAO gameName) throws DataAccessException {
        if (!dataAccess.isValidAuth(authToken)){
            throw new DataAccessException("Error: unauthorized - invalid or expired authToken");
        }

        if (gameName == null || gameName.getGameName().isEmpty()){
            throw new DataAccessException("Error: bad request - gameName is required");
        }

        return dataAccess.createGame(gameName);
    }
}
