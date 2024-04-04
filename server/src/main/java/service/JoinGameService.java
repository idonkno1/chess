package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.JoinReqData;

public class JoinGameService {
    private final DataAccess dataAccess;
    public JoinGameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public ChessGame joinGame(JoinReqData joinReq, String authToken) throws DataAccessException {
        GameData game = dataAccess.getGame(joinReq.gameID());
        AuthData token = dataAccess.getAuthToken(authToken);
        var username = token.username();


        if (("WHITE".equals(joinReq.playerColor()) && game.whiteUsername() != null) ||
                ("BLACK".equals(joinReq.playerColor()) && game.blackUsername() != null)) {
            throw new DataAccessException("Error: already taken");
        }

        GameData updatedGame;
        if ("WHITE".equals(joinReq.playerColor())) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            return dataAccess.updateGame(updatedGame);

        } else if ("BLACK".equals(joinReq.playerColor())) {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            return dataAccess.updateGame(updatedGame);
        }
        updatedGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        return dataAccess.updateGame(updatedGame);
    }
}
