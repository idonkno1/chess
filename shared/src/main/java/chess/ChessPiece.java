package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType) {
        this.teamColor = teamColor;
        this.pieceType = pieceType;

    }

    public ChessPiece(ChessPiece originalPiece, ChessGame.TeamColor teamColor, PieceType pieceType) {

        this.teamColor = teamColor;
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
        var currentRow = myPosition.getRow();
        var currentCol = myPosition.getColumn();

        switch (pieceType) {
            case KING:
                int[][] kingMoves = {{1,-1}, {1,1}, {-1,-1}, {-1,1}, {1,0}, {-1, 0}, {0,1}, {0,-1}};

                oneMove(moves, board, myPosition, kingMoves);

                break;

            case QUEEN:
                int[][] queenMoves = {{1,-1}, {1,1}, {-1,-1}, {-1,1}, {1,0}, {-1, 0}, {0,1}, {0,-1}};

                multiMove(moves, board, myPosition, queenMoves);

                break;

            case BISHOP:
                int[][] bishopMoves = {{1,-1}, {1,1}, {-1,-1}, {-1,1}};

                multiMove(moves, board, myPosition, bishopMoves);

                break;

            case KNIGHT:
                int[][] knightMoves = {{1,-2}, {1,2}, {-1,-2}, {-1,2}, {2,-1}, {2, 1}, {-2,-1}, {-2,1}};

                oneMove(moves, board, myPosition, knightMoves);

                break;

            case ROOK:
                int[][] rookMoves = {{1,0}, {-1, 0}, {0,1}, {0,-1}};

                multiMove(moves, board, myPosition, rookMoves);

                break;

            case PAWN:
                int[][] pawnMoves = {{1,0}, {2, 0}, {1,-1}, {1,1}};

                var moveDirection = (teamColor == ChessGame.TeamColor.WHITE) ? 1: -1;
                var startingRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2:7;
                var promotionRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8:1;

                for(int[] move: pawnMoves) {
                    int nextRow = currentRow + move[0] * moveDirection;
                    int nextCol = currentCol + move[1];

                    if(move[0] == 2 && currentRow != startingRow) continue;

                    if(move[1] != 0){
                        if (1 <= nextRow && nextRow <= 8 && 1 <= nextCol && nextCol <= 8) {
                            ChessPiece target = board.getPiece(new ChessPosition(nextRow, nextCol));

                            if(target != null && target.getTeamColor() != teamColor){
                                if(nextRow == promotionRow){
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), PieceType.KNIGHT));
                                }
                                else {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                                }
                            }

                        }

                    }
                    else {
                        if (1 <= nextRow && nextRow <= 8 && 1 <= nextCol && nextCol <= 8) {
                            ChessPiece blocking = board.getPiece(new ChessPosition(nextRow, nextCol));

                            if(blocking == null && move[0] == 1){
                                if(nextRow == promotionRow){
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), PieceType.KNIGHT));
                                }
                                else {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                                }
                            }
                            if(move[0] == 2 && currentRow == startingRow){
                                ChessPiece blocking2 = board.getPiece(new ChessPosition(currentRow + moveDirection, nextCol));

                                if (blocking == null && blocking2 == null){
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                                }
                            }

                        }
                    }

                }

                break;

        }

        return moves;
    }

    private void multiMove(HashSet<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int[][] movements) {
        var currentRow = myPosition.getRow();
        var currentCol = myPosition.getColumn();

        for(int[] move: movements) {
            int nextRow = currentRow;
            int nextCol = currentCol;

            while (true){
                nextRow += move[0];
                nextCol += move[1];

                if (1 > nextRow || nextRow > 8 || 1 > nextCol || nextCol > 8) break;

                ChessPiece target = board.getPiece(new ChessPosition(nextRow, nextCol));

                if(target == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                }
                else {
                    if (target.getTeamColor() != teamColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                    }
                    break;
                }

            }
        }
    }

    private void oneMove(HashSet<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int[][] movements) {
        var currentRow = myPosition.getRow();
        var currentCol = myPosition.getColumn();

        for(int[] move: movements){
            int nextRow = currentRow + move[0];
            int nextCol = currentCol + move[1];

            if (1 <= nextRow && nextRow <= 8 && 1 <= nextCol && nextCol <= 8){
                ChessPiece target = board.getPiece(new ChessPosition(nextRow, nextCol));

                if(target == null || target.getTeamColor() != teamColor){
                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));

                }
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;
        return teamColor == piece.teamColor && pieceType == piece.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }
}
