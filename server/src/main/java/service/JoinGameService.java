package service;

import dataAccess.DataAccess;
import model.GameDAO;

public class JoinGameService {
    private final DataAccess dataAccess;
    public JoinGameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public boolean joinGame(GameDAO game) {
        return false;
    }
}
