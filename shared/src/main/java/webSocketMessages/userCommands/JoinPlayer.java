package webSocketMessages.userCommands;

public class JoinPlayer extends UserGameCommand{
    private final String playerColor;

    public JoinPlayer(CommandType commandType, String authToken, int gameID, String playerColor) {
        super(commandType, authToken, gameID);
        this.playerColor = playerColor;
    }

    public String getPlayerColor(){return playerColor;}
}
