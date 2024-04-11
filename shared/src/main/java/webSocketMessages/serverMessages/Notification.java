package webSocketMessages.serverMessages;

public class Notification extends ServerMessage{
    private final String notification;

    public Notification(String notification) {
        super(ServerMessageType.NOTIFICATION);
        this.notification = notification;
    }

    public String getNotification() {return notification;}
}
