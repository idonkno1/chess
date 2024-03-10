package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

public class JoinGameService {
    private final DataAccess dataAccess;
    public JoinGameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public boolean joinGame(GameData GameData, String authToken) throws DataAccessException {
        GameData game = dataAccess.getGame(GameData.gameID());

        if (GameData.whiteUsername() != null){
            throw new DataAccessException("Error: already taken - color is already taken by another player");
        }

        if (GameData.blackUsername() != null){
            throw new DataAccessException("Error: already taken - color is already taken by another player");
        }

        return true;
    }
}
