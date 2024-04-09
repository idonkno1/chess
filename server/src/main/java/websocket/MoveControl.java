package websocket;

import chess.*;

public class MoveControl {

    private final ChessGame board;
    private final String currentSquare;
    private final String nextSquare;

    MoveControl(ChessGame board, String currentSquare, String nextSquare){
        this.board = board;
        this.currentSquare = currentSquare;
        this.nextSquare = nextSquare;
    }

    public boolean moveControl(){
        var start = stringToPosition(currentSquare);
        var end = stringToPosition(nextSquare);
        var move = new ChessMove(start, end, null);

        var moves = board.validMoves(start);
        return moves.contains(move);
    }

    public ChessGame gameControl(){
        var start = stringToPosition(currentSquare);
        var end = stringToPosition(nextSquare);
        var move = new ChessMove(start, end, null);
        try {
            board.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }

        return board;
    }



    public static ChessPosition stringToPosition(String location){
        var col = location.substring(0, 1).toLowerCase();
        int row = Integer.parseInt(location.substring(1));

        var colInt = switch (col) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new IllegalStateException("Unexpected value: " + col);
        };
        return new ChessPosition(row, colInt);
    }
}
