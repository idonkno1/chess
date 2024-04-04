package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;

public interface DataAccess {
    boolean isValidAuth(String authToken) throws DataAccessException, SQLException;

    UserData createUser(UserData userData);

    UserData getUser(String username);

    GameData createGame(String gameName);

    Collection<GameData> listGames();

    GameData getGame(int gameId);

    AuthData createAuthToken(String username);

    AuthData getAuthToken(String authToken);

    void deleteAuthToken(String authToken);

    void clearDAO() throws DataAccessException;

    ChessGame updateGame(GameData updatedGame);
}
