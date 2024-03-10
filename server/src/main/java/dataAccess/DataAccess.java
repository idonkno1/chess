package dataAccess;

import chess.ChessGame;
import model.AuthDAO;
import model.GameDAO;
import model.UserDAO;

import java.util.Collection;

public interface DataAccess {
    boolean isValidAuth(String authToken) throws DataAccessException;

    void createUser(UserDAO userDAO) throws DataAccessException;

    UserDAO getUser(String username) throws DataAccessException;

    GameDAO createGame(GameDAO gameDAO) throws DataAccessException;

    Collection<GameDAO> listGames() throws DataAccessException;

    void updateGame(int gameId, ChessGame gameState) throws DataAccessException;

    GameDAO getGame(int gameId) throws DataAccessException;

    AuthDAO createAuthToken(String username) throws DataAccessException;

    AuthDAO getAuthToken(String authToken) throws DataAccessException;

    boolean deleteAuthToken(String authToken) throws DataAccessException;

    void clearDAO();
}
