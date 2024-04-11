package webSocketMessages.serverMessages;

public class Error extends ServerMessage{
    private final String errorDescription;

    public Error(String errorDescription) {
        super(ServerMessageType.ERROR);
        this.errorDescription = errorDescription;
    }

    public String getErrorDescription(){return errorDescription;}
}
