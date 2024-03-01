package dataAccess;

import chess.ChessGame;
import model.AuthDAO;
import model.GameDAO;
import model.UserDAO;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;


public class MemoryDataAccess {

    private static int nextGameID = 1;

    private static final HashMap<String, UserDAO> users = new HashMap<>();
    private static final HashMap<Integer, GameDAO> games = new HashMap<>();

    private static final HashMap<String, AuthDAO> authTokens = new HashMap<>();

    public static boolean isValidAuth(String authToken) {return authTokens.containsKey(authToken);}

    public static void createUser(UserDAO userDAO) throws DataAccessException {
        if (users.containsKey(userDAO.getUsername())) {
            throw new DataAccessException("User already exists.");
        }
        UserDAO user = new UserDAO(userDAO.getUsername(), userDAO.getPassword(), userDAO.getEmail());
        users.put(user.getUsername(), user);
    }

    public static UserDAO getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User does not exist.");
        }
        return users.get(username);}

    public static GameDAO createGame(GameDAO gameDAO) throws DataAccessException {
        if (games.containsKey(nextGameID)) {
            throw new DataAccessException("Game ID already exists.");
        }

        GameDAO game = new GameDAO(nextGameID++, gameDAO.getWhiteUsername(), gameDAO.getBlackUsername(), gameDAO.getGameName(), gameDAO.getGame());
        games.put(game.getGameID(), gameDAO);
        return game;
    }
    public static Collection<GameDAO> listGames() {return games.values();}

    public static void updateGame(int gameId, ChessGame gameState) throws DataAccessException {
        if (!games.containsKey(gameId)) {
            throw new DataAccessException("Game with ID " + gameId + " does not exist.");
        }
        GameDAO game = games.get(gameId);
        game.setGame(gameState);
        games.put(gameId, game);
    }
    public GameDAO getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game does not exist.");
        }
        return games.get(gameID);}

    public static AuthDAO createAuthToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthDAO auth = new AuthDAO(authToken, username);
        authTokens.put(auth.getAuthToken(), auth);
        return auth;
    }
    public AuthDAO getAuthToken(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Auth token does not exist.");
        }
        return authTokens.get(authToken);}

    public static boolean deleteAuthToken(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            return false;
        }
        authTokens.remove(authToken);
        return true;
    }

    public void clearDAO(){
        users.clear();
        games.clear();
        authTokens.clear();
    }


}
