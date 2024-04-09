package ui;

import chess.*;

import java.util.Collection;

public class CreateBoard {
    private static final String[][] visualboard = new String[8][8];
    private static final String DARK_SQUARE = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String LIGHT_SQUARE = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String RED = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String WHITE_SQUARE = EscapeSequences.SET_BG_COLOR_BROWN;

    private static final String DARK_GREEN = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
    private static final String GREEN = EscapeSequences.SET_BG_COLOR_GREEN;
    private static final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;


    public static void initializeBoard(ChessBoard chessBoard) {
        // Initialize empty squares
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(i+1, j+1));
                visualboard[i][j] = pieceToSymbol(piece);
            }
        }
    }

    private static String pieceToSymbol(ChessPiece piece) {
        if (piece == null) return EscapeSequences.EMPTY;
        switch (piece.getPieceType()) {
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING:
                return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            default:
                return EscapeSequences.EMPTY;
        }

    }

    private static String formatSquare(String piece, int i, int j, boolean shouldHighlight, boolean current) {
        boolean isLightSquare = (i + j) % 2 == 0;
        String squareColor = isLightSquare ? DARK_SQUARE: LIGHT_SQUARE;
        if (shouldHighlight) {
            squareColor = isLightSquare ? DARK_GREEN: GREEN;
        }
        if(current){
            squareColor = EscapeSequences.SET_BG_COLOR_YELLOW;
        }
        return squareColor + piece + RESET;
    }

    public static void printBoard(ChessBoard board, String isWhite, Collection<ChessMove> moves) {
        boolean[][] currentSquare = new boolean[8][8];
        boolean[][] highlightSquares = new boolean[8][8];

        if (moves != null) {
            for (ChessMove move : moves) {
                ChessPosition current = move.getStartPosition();
                ChessPosition target = move.getEndPosition();
                highlightSquares[target.getRow() - 1][target.getColumn() - 1] = true;
                currentSquare[current.getRow() - 1][current.getColumn() - 1] = true;
            }
        }

        initializeBoard(board);
        System.out.print(EscapeSequences.ERASE_SCREEN);
        printLabels(isWhite);
        for (int i = 0; i < 8; i++) {
            int row = isWhite.equals("WHITE") ? 7 - i : i;
            // Print rank labels
            System.out.print(WHITE_SQUARE + " " + RED + (row + 1) + " " + RESET);
            for (int j = 0; j < 8; j++) {
                int col = isWhite.equals("WHITE") ? j : 7 - j;
                String piece = visualboard[row][col];
                boolean shouldHighlight = highlightSquares[row][col];
                boolean current = currentSquare[row][col];
                System.out.print(formatSquare(piece, row, col, shouldHighlight, current));
            }
            System.out.println(WHITE_SQUARE + " " + RED + (row + 1) + " " + RESET);
        }
        printLabels(isWhite);
        System.out.println();
    }

    private static void printLabels(String isWhite) {
        String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
        if (isWhite.equals("WHITE")) {
            System.out.print(WHITE_SQUARE + "  ");
            for (String file : files) {
                System.out.print(WHITE_SQUARE + "  " + RED + file + RESET);

            }
            System.out.print(WHITE_SQUARE + "    " + RESET);
            System.out.println();
        } else {
            System.out.print(WHITE_SQUARE + "  " + RESET);
            for (int j = files.length - 1; j >= 0; j--) {
                System.out.print(WHITE_SQUARE + "  " + RED + files[j] + RESET);
            }
            System.out.print(WHITE_SQUARE + "    " + RESET);
            System.out.println();
        }

    }

    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        printBoard(chessBoard,"WHITE", null);
        printBoard(chessBoard,"BLACK", null);

    }
}