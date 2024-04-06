package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import ui.server.ResponseException;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI); // not connecting

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>(){
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    serverMessageHandler.notify(notification);
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
        var command = new UserGameCommand(UserGameCommand.CommandType.JOIN_OBSERVER, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void joinPlayer(String authToken, int gameID, String playerColor) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.JOIN_PLAYER, authToken, gameID);
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

    public void makeMove(String authToken, int gameID, ChessMove chessMove) throws IOException {
        var command = new MakeMove(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, chessMove);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void redrawBoard(String authToken, int gameID) {

    }
}
