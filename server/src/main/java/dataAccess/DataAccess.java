package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    boolean isValidAuth(String authToken) throws DataAccessException;

    UserData createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData getGame(int gameId) throws DataAccessException;

    AuthData createAuthToken(String username) throws DataAccessException;

    AuthData getAuthToken(String authToken) throws DataAccessException;

    void deleteAuthToken(String authToken) throws DataAccessException;

    void clearDAO();

    void updateGame(GameData updatedGame) throws DataAccessException;
}
