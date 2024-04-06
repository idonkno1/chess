package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.UserGameCommand;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);
        if (connections != null) {
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> joinGame();
                case JOIN_OBSERVER -> observeGame();
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leaveGame();
                case RESIGN -> resignGame();
            }
        }
    }

    public void resignGame() {

    }

    public void leaveGame() {

    }

    public void makeMove() {

    }

    public void observeGame() {

    }

    public void joinGame() {

    }

}
