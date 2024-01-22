package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType pieceType) {
        this.teamColor = pieceColor;
        this.pieceType = pieceType;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        switch (pieceType) {
            case PAWN:
                int movementDirection = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1; // if white 1, else -1
                int startingRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7; // if white starts at row 2, else at row 7
                int promotionRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1; // if white promotion at row 8, else at row 1

                int[][] pawnMoves = {{1, 0}, {2, 0}, {1, -1}, {1, 1}}; //possible pawn moves, move up 1 or 2 spaces, take left or right

                for (int[] move : pawnMoves) {
                    int newRow = row + move[0] * movementDirection; // moves up/down dep on direction
                    int newCol = col + move[1]; // stays in same col unless taking a piece

                    if (move[0] == 2 && row != startingRow) {
                        continue; // only move twice in starting row
                    }

                    if (move[1] != 0) {
                        // pawn is capturing a piece
                        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                            ChessPiece targetPiece = board.getPiece(new ChessPosition(newRow, newCol));

                            if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                                if (row + movementDirection == promotionRow) { // if new move is in promotion row, promotion can occur
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, newCol), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, newCol), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, newCol), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, newCol), PieceType.KNIGHT));
                                }
                                // if capture occurs outside of promotion row
                                else
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, newCol), null));
                            }
                        }

                    } else {
                        // pawn moving forward
                        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                            ChessPiece blockingPiece = board.getPiece(new ChessPosition(newRow, newCol));

                            if (blockingPiece != null && move[0] == 1) {
                                if (row + movementDirection == promotionRow) {


                                }
                            }
                        }


                    }
                }


        }

        return moves;
    }
}
