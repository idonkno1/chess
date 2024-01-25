package chess;

import java.util.*;

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
        PAWN;

    }

    public String getPieceSymbol() {
        switch (pieceType) {
            case PAWN:
                return (teamColor == ChessGame.TeamColor.WHITE) ? "P" : "p";
            case KNIGHT:
                return (teamColor == ChessGame.TeamColor.WHITE) ? "N" : "n";
            case BISHOP:
                return (teamColor == ChessGame.TeamColor.WHITE) ? "B" : "b";
            case ROOK:
                return (teamColor == ChessGame.TeamColor.WHITE) ? "R" : "r";
            case QUEEN:
                return (teamColor == ChessGame.TeamColor.WHITE) ? "Q" : "q";
            case KING:
                return (teamColor == ChessGame.TeamColor.WHITE) ? "K" : "k";
            default:
                return " ";
        }

    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return teamColor;
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
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        switch (pieceType) {
            case PAWN:
                int movementDirection = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1; // if white 1, else -1
                int startingRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7; // if white starts at row 2, else at row 7
                int promotionRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1; // if white promotion at row 8, else at row 1

                int[][] pawnMoves = {{1, 0}, {2, 0}, {1, -1}, {1, 1}}; //possible pawn moves, move up 1 or 2 spaces, take left or right

                for (int[] move : pawnMoves) {
                    int nextRow = currentRow + move[0] * movementDirection; // moves up/down dep on direction
                    int nextCol = currentCol + move[1]; // stays in same col unless taking a piece

                    if (move[0] == 2 && currentRow != startingRow) {
                        continue; // only move twice in starting row
                    }

                    if (move[1] != 0) {
                        // pawn is capturing a piece
                        if (nextRow >= 1 && nextRow <= 8 && nextCol >= 1 && nextCol <= 8) {
                            ChessPiece targetPiece = board.getPiece(new ChessPosition(nextRow, nextCol));

                            if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                                if (currentRow + movementDirection == promotionRow) { // if new move is in promotion row, promotion can occur
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, nextCol), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, nextCol), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, nextCol), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, nextCol), PieceType.KNIGHT));
                                }
                                // if capture occurs outside of promotion row
                                else
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                            }
                        }

                    } else {
                        // pawn moving forward
                        if (nextRow >= 1 && nextRow <= 8 && nextCol >= 1 && nextCol <= 8) {
                            ChessPiece blockingPiece = board.getPiece(new ChessPosition(nextRow, nextCol));

                            if (blockingPiece == null && move[0] == 1) { // move up one space if not blocked
                                if (currentRow + movementDirection == promotionRow) {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, nextCol), PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, nextCol), PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, nextCol), PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, new ChessPosition(promotionRow, nextCol), PieceType.KNIGHT));
                                } else moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                            }
                            // move up two spaces if not blocked
                            if (move[0] == 2 && currentRow == startingRow) {
                                ChessPiece blockingPiece2 = board.getPiece(new ChessPosition(currentRow + movementDirection, currentCol));
                                if (blockingPiece == null && blockingPiece2 == null) {
                                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                                }
                            }
                        }
                    }
                }
                break;

            case KNIGHT:
                int[][] knightMoves = {{2, -1}, {2, 1}, {-2, -1}, {-2, 1}, {1, -2}, {1, 2}, {-1, -2}, {-1, 2}}; // multiple l shapes movements

                oneMove(moves, board, myPosition, knightMoves);

                break;
            case ROOK:
                int[][] rookMoves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
                continuousMoves(moves, board, myPosition, rookMoves);

                break;
            case BISHOP:
                int[][] bishopMoves = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                continuousMoves(moves, board, myPosition, bishopMoves);
                break;

            case QUEEN:
                int[][] queenMoves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                continuousMoves(moves, board, myPosition, queenMoves);
                break;

            case KING:
                int[][] kingMoves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // multiple l shapes movements

                oneMove(moves, board, myPosition, kingMoves);
                break;

            default:
                throw new IllegalArgumentException("Invalid piece type: " + pieceType);
        }

        return moves;
    }

    private void oneMove(HashSet<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int[][] movements) {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        for (int[] move : movements) {
            int nextRow = currentRow + move[0];
            int nextCol = currentCol + move[1];

            if (nextRow >= 1 && nextRow <= 8 && nextCol >= 1 && nextCol <= 8) {
                ChessPiece targetPiece = board.getPiece(new ChessPosition(nextRow, nextCol));

                if (targetPiece == null || targetPiece.getTeamColor() != teamColor) {
                    // either move to empty space or capture enemy piece in said space
                    moves.add((new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null)));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }


    private void continuousMoves(HashSet<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int[][] movements) {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        for (int[] move : movements) {
            int nextRow = currentRow;
            int nextCol = currentCol;

            while (true) {
                nextRow += move[0];
                nextCol += move[1];

                if (nextRow < 1 || nextRow > 8 || nextCol < 1 || nextCol > 8) break; // outside of board boundaries stop

                ChessPiece targetPiece = board.getPiece(new ChessPosition(nextRow, nextCol));

                if (targetPiece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                } else {
                    if (targetPiece.getTeamColor() != teamColor) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                    }
                    break; // cant keep moving if capturing a piece
                }
            }
        }
    }
}
