package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameDAO;

public class JoinGameService {
    private final DataAccess dataAccess;
    public JoinGameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public boolean joinGame(String authToken, GameDAO gameDAO) throws DataAccessException {
        if (!dataAccess.isValidAuth(authToken)){
            throw new DataAccessException("Error: unauthorized - invalid or expired authToken");
        }
        GameDAO game = dataAccess.getGame(gameDAO.getGameID());
        if (game == null){
            throw new DataAccessException("Error: bad request - game does not exist");
        }

        if (gameDAO.getWhiteUsername() != null){
            throw new DataAccessException("Error: already taken - color is already taken by another player");
        }

        if (gameDAO.getBlackUsername() != null){
            throw new DataAccessException("Error: already taken - color is already taken by another player");
        }

        return true;
    }
}
