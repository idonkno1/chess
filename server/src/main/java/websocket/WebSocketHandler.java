package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.sql.SQLException;


@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(UserGameCommand.class, new UserGameCommandDeserializer())
            .create();

    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws SQLException, IOException, DataAccessException {
        UserGameCommand command = gson.fromJson(msg, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                if (command instanceof JoinPlayer joinPlayerCommand) {
                    playGame(joinPlayerCommand.getAuthString(), joinPlayerCommand.getGameID(), joinPlayerCommand.getPlayerColor(), session);
                }
                break;
            case JOIN_OBSERVER:
                observeGame(command.getAuthString(), command.getGameID(), session);
                break;
            case MAKE_MOVE:
                if (command instanceof MakeMove makeMoveCommand) {
                    makeMove(makeMoveCommand.getAuthString(), makeMoveCommand.getGameID(), makeMoveCommand.getCurrentSquare(), makeMoveCommand.getNextSquare());
                }
                break;
            case LEAVE:
                leaveGame(command.getAuthString(), command.getGameID());
                break;
            case RESIGN:
                resignGame(command.getAuthString(), command.getGameID());
                break;
        }
    }

    public void resignGame(String authString, int gameID) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        if (!dataAccess.isValidAuth(authString) || gameData == null) {
            connections.errorToSender(authData.username(), new ErrorMessage("Error authToken/gameID not valid"));
        }
        var message = String.format("%s resigned the game", authData.username());
        var notification = new NotificationMessage(message);

        dataAccess.deleteGame(gameID);
        connections.broadcast("", notification);
    }

    public void leaveGame(String authString, int gameID) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        var whitePlayer = gameData.whiteUsername();
        var blackPlayer = gameData.blackUsername();

        if (!dataAccess.isValidAuth(authString) || gameData == null) {
            connections.errorToSender(authData.username(), new ErrorMessage("Error authToken/gameID not valid"));
        }
        if (authData.username().equals(whitePlayer)){
            gameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            dataAccess.updateGame(gameData);
        } else if (authData.username().equals(blackPlayer)){
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            dataAccess.updateGame(gameData);
        }

        connections.remove(authData.username());
        var message = String.format("%s left the game", authData.username());
        var notification = new NotificationMessage(message);

        connections.broadcast(authData.username(), notification);
    }

    private void makeMove(String authString, int gameID, String currentSquare, String nextSquare) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        if (!dataAccess.isValidAuth(authString) || gameData == null) {connections.errorToSender(authData.username(), new ErrorMessage("Error authToken/gameID not valid"));}

        ChessGame.TeamColor gameCheck = null;
        String opponent = null;
        String currentPlayer = authData.username();
        if (currentPlayer.equals(gameData.whiteUsername())) {
            gameCheck = ChessGame.TeamColor.BLACK;
            opponent = gameData.blackUsername();
        } else if (currentPlayer.equals(gameData.blackUsername())) {
            gameCheck = ChessGame.TeamColor.WHITE;
            opponent = gameData.whiteUsername();
        } else {
            connections.errorToSender(authData.username(), new ErrorMessage("Error unknown player"));
        }

        var board = gameData.game();
        var moveControl = new MoveControl(board, currentSquare, nextSquare);

        if(!moveControl.moveControl()){connections.errorToSender(authData.username(), new ErrorMessage("Error invalid move"));}

        gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), moveControl.gameControl());
        dataAccess.updateGame(gameData);
        
        board = gameData.game();

        var message = String.format("%s made the move %s %s", authData.username(), currentSquare, nextSquare);
        var notification = new NotificationMessage(message);
        connections.broadcast(authData.username(), notification);

        if(board.isInCheck(gameCheck)){
            var checkMessage = String.format("%s checked %s", currentPlayer, opponent);
            var checkNotification = new NotificationMessage(checkMessage);
            connections.broadcast("", checkNotification);

        } else if (board.isInCheckmate(gameCheck)) {
            var checkmateMessage = String.format("%s checkmated %s", currentPlayer, opponent);
            var checkmateNotification = new NotificationMessage(checkmateMessage);
            connections.broadcast("", checkmateNotification);

        } else if (board.isInStalemate(gameCheck)) {
            var stalemateMessage = String.format("%s stalemated %s", currentPlayer, opponent);
            var stalemateNotification = new NotificationMessage(stalemateMessage);
            connections.broadcast("", stalemateNotification);

        }
        var gameNotification = new LoadGameMessage(gameData.game());
        connections.broadcast("", gameNotification);
    }

    public void observeGame(String authString, int gameID, Session session) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        if (!dataAccess.isValidAuth(authString) || gameData == null) {
            connections.errorToSender(authData.username(), new ErrorMessage("Error authToken/gameID not valid"));
        }

        connections.add(authData.username(), session);
        var message = String.format("%s is watching", authData.username());
        var notification = new NotificationMessage(message);
        connections.broadcast(authData.username(), notification);
    }

    public void playGame(String authString, int gameID, String playerColor, Session session) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        if (!dataAccess.isValidAuth(authString) || gameData == null) {
            connections.errorToSender(authData.username(), new ErrorMessage("Error authToken/gameID not valid"));
        }

        connections.add(authData.username(), session);
        var message = String.format("%s is playing as %s", authData.username(), playerColor);
        var notification = new NotificationMessage(message);
        connections.broadcast(authData.username(), notification);
    }
}
