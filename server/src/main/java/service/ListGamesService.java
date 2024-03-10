package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class ListGamesService {
    private final DataAccess dataAccess;
    public ListGamesService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public ArrayList<Object> listGames(String authToken) throws DataAccessException {
        var games = dataAccess.listGames();
        var gs = new ArrayList<>();
        for (GameData game : games) {
            var gameMap = new HashMap<String, Object>();
            gameMap.put("gameId", game.gameID());
            gameMap.put("whiteUsername", game.whiteUsername());
            gameMap.put("blackUsername", game.blackUsername());
            gameMap.put("gameName", game.gameName());

            gs.add(gameMap);

        }

        return gs;
    }
}
