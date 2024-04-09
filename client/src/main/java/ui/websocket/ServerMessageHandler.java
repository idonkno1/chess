package ui.websocket;

import webSocketMessages.serverMessages.*;

public interface ServerMessageHandler {
    void notify(ServerMessage serverMessage, String playerCol);
}
