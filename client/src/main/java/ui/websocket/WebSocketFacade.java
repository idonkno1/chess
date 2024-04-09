package ui.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ui.server.ResponseException;
import webSocketMessages.ServerMessageDeserializer;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;

    private static String playerCol = null;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer())
            .create();

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>(){
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

                    // Handle the message based on its type
                    if (serverMessage instanceof NotificationMessage) {
                        serverMessageHandler.notify((NotificationMessage) serverMessage, playerCol);
                    }else if (serverMessage instanceof ErrorMessage) {
                        serverMessageHandler.notify((ErrorMessage) serverMessage, playerCol);
                    }else if (serverMessage instanceof LoadGameMessage) {
                        serverMessageHandler.notify((LoadGameMessage) serverMessage, playerCol);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void joinObserver(String authToken, int gameID) throws IOException {
        playerCol = "WHITE";
        var command = new UserGameCommand(UserGameCommand.CommandType.JOIN_OBSERVER, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void joinPlayer(String authToken, int gameID, String playerColor) throws IOException {
        playerCol = playerColor;
        var command = new JoinPlayer(JoinPlayer.CommandType.JOIN_PLAYER, authToken, gameID, playerColor);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void highlightMove(String authToken, int gameID, String piece) {
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void makeMove(String authToken, int gameID,String currentSquare, String nextSquare) throws IOException {
        var command = new MakeMove(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, currentSquare, nextSquare);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void redrawBoard(String authToken, int gameID) {

    }

}
