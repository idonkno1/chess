package webSocketMessages.serverMessages;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Collection;

public class HighlightMessage extends ServerMessage  {
    private final ChessGame game;
    private final Collection<ChessMove> moves;

    public HighlightMessage(ChessGame game, Collection<ChessMove> moves) {
        super(ServerMessageType.HIGHLIGHT);
        this.game = game;
        this.moves = moves;
    }

    public ChessGame getGame(){return game;}

    public Collection<ChessMove> getMoves() {return moves;}
}
