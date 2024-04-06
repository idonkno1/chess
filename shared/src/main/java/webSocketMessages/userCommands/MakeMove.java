package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {

    private final ChessMove chessMove;

    public MakeMove(CommandType commandType, String authToken, int gameID, ChessMove chessMove) {
        super(commandType, authToken, gameID);
        this.chessMove = chessMove;
    }

    public ChessMove getChessMove(){
        return chessMove;
    }
}
