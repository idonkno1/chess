package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData updateWhiteUsername(String newWhiteUsername) {
        return new GameData(this.gameID, newWhiteUsername, this.blackUsername, this.gameName, this.game);
    }

    public GameData updateBlackUsername(String newBlackUsername) {
        return new GameData(this.gameID, this.whiteUsername, newBlackUsername, this.gameName, this.game);
    }

    public GameData updateGameState(ChessGame newGameState) {
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, this.gameName, newGameState);
    }

}
