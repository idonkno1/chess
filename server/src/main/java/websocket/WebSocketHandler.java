package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import webSocketMessages.userCommands.UserGameCommand;

public class WebSocketHandler {
    private final ConnectionManager conn = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);
        if (conn != null) {
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> joinGame();
                case JOIN_OBSERVER -> observeGame();
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leaveGame();
                case RESIGN -> resignGame();
            }
        }
    }

    private void resignGame() {
    }

    private void leaveGame() {

    }

    private void makeMove() {

    }

    private void observeGame() {

    }

    private void joinGame() {

    }

}
