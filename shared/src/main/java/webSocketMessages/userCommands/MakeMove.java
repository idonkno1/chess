package webSocketMessages.userCommands;

public class MakeMove extends UserGameCommand {

    private final String currentSquare;

    private final String nextSquare;

    public MakeMove(CommandType commandType, String authToken, int gameID, String currentSquare, String nextSquare) {
        super(commandType, authToken, gameID);
        this.currentSquare = currentSquare;
        this.nextSquare = nextSquare;
    }

    public String getCurrentSquare() {return currentSquare;}

    public String getNextSquare() {return nextSquare;}
}
