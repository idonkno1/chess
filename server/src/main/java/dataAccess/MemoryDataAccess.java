package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;


public class MemoryDataAccess implements DataAccess{

    private int nextGameID = 1;

    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();

    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    public boolean isValidAuth(String authToken) {return authTokens.containsKey(authToken);}

    public UserData createUser(UserData userData){
        UserData user = new UserData(userData.username(), userData.password(), userData.email());
        users.put(user.username(), user);
        return user;
    }
    public UserData getUser(String username) {return users.get(username);}

    public GameData createGame(String gameName){
        GameData game = new GameData(nextGameID++, null, null, gameName, new ChessGame());
        games.put(game.gameID(), game);
        return game;
    }
    public ArrayList<GameData> listGames() {
        var gameList = new ArrayList<GameData>();
        for (var i = 1; i < nextGameID; i++){
            var game = games.get(i);
            if(game != null){
                gameList.add(game);
            }
        }
        return gameList;
    }

    public void updateGame(int gameId, ChessGame gameState) {
        GameData game = games.get(gameId);
        game = game.update(gameState);
        games.put(gameId, game);
    }
    public GameData getGame(int gameID) {return games.get(gameID);}

    public AuthData createAuthToken(String username){
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        authTokens.put(auth.authToken(), auth);
        return auth;
    }
    public AuthData getAuthToken(String authToken){return authTokens.get(authToken);}

    public void deleteAuthToken(String authToken) {
        authTokens.remove(authToken);
    }

    public void clearDAO(){
        users.clear();
        games.clear();
        authTokens.clear();
    }


}
