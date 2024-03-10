package dataAccess;

import chess.ChessGame;
import model.AuthDAO;
import model.GameDAO;
import model.UserDAO;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;


public class MemoryDataAccess implements DataAccess{

    private static int nextGameID = 1;

    private static final HashMap<String, UserDAO> users = new HashMap<>();
    private static final HashMap<Integer, GameDAO> games = new HashMap<>();

    private static final HashMap<String, AuthDAO> authTokens = new HashMap<>();

    public boolean isValidAuth(String authToken) {return authTokens.containsKey(authToken);}

    public UserDAO createUser(UserDAO userDAO){
        UserDAO user = new UserDAO(userDAO.getUsername(), userDAO.getPassword(), userDAO.getEmail());
        users.put(user.getUsername(), user);
        return user;
    }

    public UserDAO getUser(String username) {return users.get(username);}

    public GameDAO createGame(GameDAO gameDAO){
        GameDAO game = new GameDAO(nextGameID++, gameDAO.getWhiteUsername(), gameDAO.getBlackUsername(), gameDAO.getGameName(), gameDAO.getGame());
        games.put(game.getGameID(), gameDAO);
        return game;
    }
    public Collection<GameDAO> listGames() {return games.values();}

    public void updateGame(int gameId, ChessGame gameState) {
        GameDAO game = games.get(gameId);
        game.setGame(gameState);
        games.put(gameId, game);
    }
    public GameDAO getGame(int gameID) {return games.get(gameID);}

    public AuthDAO createAuthToken(String username){
        String authToken = UUID.randomUUID().toString();
        AuthDAO auth = new AuthDAO(authToken, username);
        authTokens.put(auth.getAuthToken(), auth);
        return auth;
    }
    public AuthDAO getAuthToken(AuthDAO authToken){return authTokens.get(authToken.getAuthToken());}

    public void deleteAuthToken(String authToken) {
        authTokens.remove(authToken);
    }

    public void clearDAO(){
        users.clear();
        games.clear();
        authTokens.clear();
    }


}
