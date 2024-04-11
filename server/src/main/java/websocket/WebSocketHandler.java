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
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.HighlightMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
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
    public void onMessage(Session session, String msg) throws SQLException, IOException, DataAccessException{
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
            case HIGHLIGHT:
                if (command instanceof MakeMove makeMoveCommand) {
                    highLight(makeMoveCommand.getAuthString(), makeMoveCommand.getGameID(), makeMoveCommand.getCurrentSquare());
                }
                break;
            case LEAVE:
                leaveGame(command.getAuthString(), command.getGameID());
                break;
            case REDRAW:
                redrawBoard(command.getAuthString(), command.getGameID());
                break;
            case RESIGN:
                resignGame(command.getAuthString(), command.getGameID());
                break;
        }
    }

    private void redrawBoard(String authString, int gameID) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);
        if (!dataAccess.isValidAuth(authString) || gameData.gameID() == gameID) {connections.backToSender(authData.username(), new Error("Error authToken/NID not valid"));}

        if(dataAccess.isValidAuth(authString)) {
            var game = gameData.game();
            var gameNotification = new LoadGame(game);
            connections.backToSender(authData.username(), gameNotification);
        }
    }

    private void highLight(String authString, int gameID, String currentSquare) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        if (!dataAccess.isValidAuth(authString) || gameData.gameID() == gameID) {connections.backToSender(authData.username(), new Error("Error authToken/gameID not valid"));}

        if(dataAccess.isValidAuth(authString)) {
            var game = gameData.game();
            var moveControl = new MoveControl(game, currentSquare, null);

            var moves = moveControl.moves();

            var highlightNotification = new HighlightMessage(game, moves);
            connections.backToSender(authData.username(), highlightNotification);
        }
    }

    public void resignGame(String authString, int gameID) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        if (!dataAccess.isValidAuth(authString) || gameData == null) {
            connections.backToSender(authData.username(), new Error("Error authToken/gameID not valid"));
        }
        else if (!gameData.whiteUsername().equals(authData.username()) && !gameData.blackUsername().equals(authData.username())){
            connections.backToSender(authData.username(), new Error("Error authToken not valid"));
        }

        else if(dataAccess.isValidAuth(authString)) {
            var message = String.format("%s resigned the game", authData.username());
            var notification = new Notification(message);

            dataAccess.deleteGame(gameID);
            connections.broadcast("", notification);
        }
    }

    public void leaveGame(String authString, int gameID) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        var whitePlayer = gameData.whiteUsername();
        var blackPlayer = gameData.blackUsername();

        if (!dataAccess.isValidAuth(authString)  || gameData.gameID() == gameID) {
            connections.backToSender(authData.username(), new Error("Error authToken/gameID not valid"));
        }

        else if (dataAccess.isValidAuth(authString)) {
            if (authData.username().equals(whitePlayer)) {
                var game = gameData.game();
                gameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), game);
                dataAccess.updateGame(gameData);
            } else if (authData.username().equals(blackPlayer)) {
                var game = gameData.game();
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), game);
                dataAccess.updateGame(gameData);
            }

            connections.remove(authData.username());
            var message = String.format("%s left the game", authData.username());
            var notification = new Notification(message);

            connections.broadcast(authData.username(), notification);
        }
    }

    private void makeMove(String authString, int gameID, String currentSquare, String nextSquare) {
        try {
            AuthData authData = dataAccess.getAuthToken(authString);
            GameData gameData = dataAccess.getGame(gameID);

            if (!dataAccess.isValidAuth(authString) || gameData.gameID() == gameID) {
                connections.backToSender(authData.username(), new Error("Error: authToken/gameID not valid"));
            }

            else if (!gameData.whiteUsername().equals(authData.username()) && !gameData.blackUsername().equals(authData.username())){
                connections.backToSender(authData.username(), new Error("Error authToken not valid"));
            }

            if(dataAccess.isValidAuth(authString)) {

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
                    connections.backToSender(authData.username(), new Error("Error unknown player"));
                }

                var game = gameData.game();
                var moveControl = new MoveControl(game, currentSquare, nextSquare);

                if (!moveControl.validMove()) {
                    connections.backToSender(authData.username(), new Error("Error invalid move"));
                }

                if (moveControl.validMove()) {
                    gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), moveControl.gameControl());
                    dataAccess.updateGame(gameData);

                    game = gameData.game();

                    var message = String.format("%s made the move %s %s", authData.username(), currentSquare, nextSquare);
                    var notification = new Notification(message);
                    connections.broadcast(authData.username(), notification);

                    if (game.isInCheckmate(gameCheck)) {
                        var checkMessage = String.format("%s checkmated %s", currentPlayer, opponent);
                        var checkNotification = new Notification(checkMessage);
                        connections.broadcast("", checkNotification);

                        var gameNotification = new LoadGame(game);
                        connections.broadcast("", gameNotification);

                        var gameOverMessage = String.format("Game over! %s won!", currentPlayer);
                        var gameOverNotification = new Notification(gameOverMessage);
                        connections.broadcast("", gameOverNotification);
                        dataAccess.deleteGame(gameID);

                    } else if (game.isInCheck(gameCheck)) {
                        var checkmateMessage = String.format("%s checked %s", currentPlayer, opponent);
                        var checkmateNotification = new Notification(checkmateMessage);
                        connections.broadcast("", checkmateNotification);

                        var gameNotification = new LoadGame(game);
                        connections.broadcast("", gameNotification);

                    } else if (game.isInStalemate(gameCheck)) {
                        var stalemateMessage = String.format("%s stalemated %s", currentPlayer, opponent);
                        var stalemateNotification = new Notification(stalemateMessage);
                        connections.broadcast("", stalemateNotification);

                        var gameNotification = new LoadGame(game);
                        connections.broadcast("", gameNotification);

                        var gameOverMessage = "Game over! It is a tie";
                        var gameOverNotification = new Notification(gameOverMessage);
                        connections.broadcast("", gameOverNotification);
                        dataAccess.deleteGame(gameID);

                    } else {
                        var gameNotification = new LoadGame(game);
                        connections.broadcast("", gameNotification);
                    }
                }
            }
        }catch(SQLException | DataAccessException | IOException e){
            throw  new RuntimeException(e);
        }

    }

    public void observeGame(String authString, int gameID, Session session) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        if (!dataAccess.isValidAuth(authString) || gameData.gameID() == gameID) {
            connections.backToSender(authData.username(), new Error("Error authToken/gameID not valid"));
        }

        if(dataAccess.isValidAuth(authString)) {
            connections.add(authData.username(), session);
            var message = String.format("%s is watching", authData.username());
            var notification = new Notification(message);
            connections.broadcast(authData.username(), notification);
        }
    }

    public void playGame(String authString, int gameID, String playerColor, Session session) throws SQLException, DataAccessException, IOException {
        AuthData authData = dataAccess.getAuthToken(authString);
        GameData gameData = dataAccess.getGame(gameID);

        if (!dataAccess.isValidAuth(authString) || gameData.gameID() == gameID) {
            connections.backToSender(authData.username(), new Error("Error authToken/gameID not valid"));
        }

        else if (gameData.whiteUsername() != null){
            connections.backToSender(authData.username(), new Error("Error there is a white player already"));
        }

        else if (gameData.blackUsername() != null){
            connections.backToSender(authData.username(), new Error("Error there is a black player already"));
        }

        if(dataAccess.isValidAuth(authString)) {
            connections.add(authData.username(), session);
            var message = String.format("%s is playing as %s", authData.username(), playerColor);
            var notification = new Notification(message);
            connections.broadcast(authData.username(), notification);
        }
    }
}
