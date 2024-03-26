package ui;



public class CreateBoard {
    private static final String[][] board = new String[8][8];
    private static final String DARK_SQUARE = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String LIGHT_SQUARE = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String RED = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String WHITE_SQUARE = EscapeSequences.SET_BG_COLOR_BROWN;
    private static final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;


    public static void initializeBoard() {
        // Initialize empty squares
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.EMPTY;
            }
        }

        // Initialize black pieces
        board[0] = new String[]{EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK};
        for (int j = 0; j < 8; j++) {
            board[1][j] = EscapeSequences.BLACK_PAWN;
        }

        // Initialize white pieces
        board[7] = new String[]{EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK};
        for (int j = 0; j < 8; j++) {
            board[6][j] = EscapeSequences.WHITE_PAWN;
        }
    }

    private static String formatSquare(String piece, int i, int j) {
        boolean isLightSquare = (i + j) % 2 == 0;
        String squareColor = isLightSquare ? LIGHT_SQUARE : DARK_SQUARE;
        return squareColor + piece + RESET;
    }

    public static void printBoard(String isWhite) {
        initializeBoard();
        System.out.print(EscapeSequences.ERASE_SCREEN);
        printLabels(isWhite);
        for (int i = 0; i < 8; i++) {
            int row = isWhite.equals("WHITE") ? 7 - i : i;
            // Print rank labels
            System.out.print(WHITE_SQUARE + " " + RED + (row + 1) + " " + RESET);
            for (int j = 0; j < 8; j++) {
                int col = isWhite.equals("WHITE") ? j : 7 - j;
                String piece = board[row][col];
                System.out.print(formatSquare(piece, row, col));
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
        printBoard("WHITE");
        printBoard("BLACK");

    }
}