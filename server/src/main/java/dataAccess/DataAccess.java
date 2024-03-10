package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    boolean isValidAuth(String authToken) throws DataAccessException;

    UserData createUser(UserData UserData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void updateGame(int gameId, ChessGame gameState) throws DataAccessException;

    GameData getGame(int gameId) throws DataAccessException;

    AuthData createAuthToken(String username) throws DataAccessException;

    AuthData getAuthToken(String authToken) throws DataAccessException;

    void deleteAuthToken(String authToken) throws DataAccessException;

    void clearDAO();
}
