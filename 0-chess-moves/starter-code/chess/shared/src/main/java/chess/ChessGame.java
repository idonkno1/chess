package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = teamTurn;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            throw new IllegalArgumentException("No piece at location");
        }
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<ChessMove>();
        for (ChessMove move : validMoves) {
            ChessPiece destinationPiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), piece);

            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
            if (destinationPiece != null) {
                board.addPiece(move.getEndPosition(), destinationPiece);

            }
            board.addPiece(startPosition, piece);

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPiece destinationPiece = board.getPiece(move.getEndPosition());

        if (piece == null || !piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid Move");
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Invalid Move");
        }

        if (destinationPiece.getTeamColor() == piece.getTeamColor()) {
            throw new InvalidMoveException("Invalid Move");
        }

        var tempBoard = new ChessBoard(board);
        tempBoard.addPiece(move.getEndPosition(), piece);

        if (isInCheckOnBoard(piece.getTeamColor(), tempBoard)) {
            throw new InvalidMoveException("Invalid move King in Check");
        }

        board.addPiece(move.getEndPosition(), piece);

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
            if (move.getEndPosition().getRow() == promotionRow) {
                ChessPiece.PieceType promotionType = move.getPromotionPiece();
                if (promotionType == null) {
                    promotionType = ChessPiece.PieceType.QUEEN;
                }
                if (promotionType == ChessPiece.PieceType.KING || promotionType == ChessPiece.PieceType.PAWN) {
                    throw new InvalidMoveException("Invalid Promotion Type");
                }
                board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), promotionType));
            }
        }
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingLocation(teamColor);

        for (var row = 1; row <= 8; row++) {
            for (var col = 1; col <= 8; col++) {
                ChessPosition curPos = new ChessPosition(row, col);
                ChessPiece curPiece = board.getPiece(curPos);

                if (curPiece != null && curPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> potentialMoves = curPiece.pieceMoves(board, curPos);
                    for (ChessMove move : potentialMoves) {
                        if (move.getEndPosition().equals(kingPosition)) return true;
                    }
                }

            }
        }
        return false;
    }

    private ChessPosition findKingLocation(TeamColor teamColor) {
        for (var row = 1; row <= 8; row++) {
            for (var col = 1; col <= 8; col++) {
                ChessPosition curPos = new ChessPosition(row, col);
                ChessPiece curPiece = board.getPiece(curPos);
                if (curPiece.getPieceType() == ChessPiece.PieceType.KING && curPiece.getTeamColor() == teamColor) {
                    return curPos;
                }

            }
        }
        throw new IllegalStateException("King was not found for team " + teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;

        for (var row = 1; row <= 8; row++) {
            for (var col = 1; col <= 8; col++) {
                ChessPosition curPos = new ChessPosition(row, col);
                ChessPiece curPiece = board.getPiece(curPos);

                if (curPiece != null && curPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> potenialMoves = curPiece.pieceMoves(board, curPos);

                    for (ChessMove move : potenialMoves) {
                        var tempBoard = new ChessBoard(board);
                        tempBoard.addPiece(move.getEndPosition(), curPiece);
                        tempBoard.addPiece(move.getStartPosition(), null);

                        ChessGame tempGame = new ChessGame();
                        tempGame.setBoard(tempBoard);
                        tempGame.setTeamTurn(teamColor);
                        if (!tempGame.isInCheck(teamColor)) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;

        for (var row = 1; row <= 8; row++) {
            for (var col = 1; col <= 8; col++) {
                ChessPosition curPos = new ChessPosition(row, col);
                ChessPiece curPiece = board.getPiece(curPos);

                if (curPiece != null && curPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> potenialMoves = curPiece.pieceMoves(board, curPos);

                    for (ChessMove move : potenialMoves) {
                        if (isLegalMove(move)) return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isLegalMove(ChessMove move) {
        var tempBoard = new ChessBoard(board);
        ChessPiece movingPiece = tempBoard.getPiece(move.getStartPosition());
        tempBoard.addPiece(move.getEndPosition(), movingPiece);
        tempBoard.addPiece(move.getStartPosition(), null);

        if (isInCheckOnBoard(movingPiece.getTeamColor(), tempBoard)) return false;

        return true;
    }

    private boolean isInCheckOnBoard(TeamColor teamColor, ChessBoard tempBoard) {
        ChessPosition kingPosition = findKingLocation(teamColor);

        for (var row = 1; row <= 8; row++) {
            for (var col = 1; col <= 8; col++) {
                ChessPosition curPos = new ChessPosition(row, col);
                ChessPiece curPiece = board.getPiece(curPos);

                if (curPiece != null && curPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> potentialMoves = curPiece.pieceMoves(board, curPos);
                    for (ChessMove move : potentialMoves) {
                        if (move.getEndPosition().equals(kingPosition)) return true;
                    }
                }

            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
