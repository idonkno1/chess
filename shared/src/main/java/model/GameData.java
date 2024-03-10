package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData update(ChessGame gameState) {return new GameData(gameID, whiteUsername, blackUsername, gameName, gameState);}

}
