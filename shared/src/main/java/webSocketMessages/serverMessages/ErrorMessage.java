package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage{
    private final String errorDescription;

    public ErrorMessage(String errorDescription) {
        super(ServerMessageType.ERROR);
        this.errorDescription = errorDescription;
    }

    public String getErrorDescription(){return errorDescription;}
}
