package model;

import chess.ChessGame;

public class GameDAO {
    private final int gameID;
    private final String whiteUsername;
    private final String blackUsername;
    private final String gameName;

    private ChessGame game;

    public GameDAO(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public int getGameID() {return gameID;}

    public String getWhiteUsername() {return whiteUsername;}

    public String getBlackUsername() {return blackUsername;}

    public String getGameName() {return gameName;}

    public ChessGame getGame() {return game;}

    public void setGame(ChessGame game){this.game = game;}
}
