package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.JoinReqData;
import model.LoginReqData;
import model.UserData;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class Client {
    private final ServerFacade server;

    private String visitorName = null;
    private String authToken = null;
    private String gameName = null;
    private int gameID;

    //private final NotificationHandler notificationHandler;
    //private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;


    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create"-> createGame(params);
                case "list" -> listGame();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String observeGame(String[] params) throws ResponseException {
        assertSignedIn();
        gameID = Integer.parseInt(params[0]);
        var gameJoined = new JoinReqData(null, gameID);
        server.joinGame(gameJoined, authToken);
        CreateBoard.printBoard("WHITE");
        return String.format("You are observing a chess game. Assigned chess ID: %d", gameID);
    }

    private String joinGame(String[] params) throws ResponseException {
        assertSignedIn();
        gameID = Integer.parseInt(params[0]);
        var playerColor = params[1].toUpperCase();
        var gameJoined = new JoinReqData(playerColor, gameID);
        server.joinGame(gameJoined, authToken);
        CreateBoard.printBoard(playerColor);
        return String.format("You are playing a chess game as %s. Assigned chess ID: %d", playerColor, gameID);
    }

    private String listGame() throws ResponseException {
        assertSignedIn();
        var games = server.listGames(authToken);
        var res = new StringBuilder();
        var gson = new Gson();
        for(var game: games){
            res.append(gson.toJson(game)).append("\n");
        }
        return res.toString();
    }

    private String createGame(String[] params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1){
            var game = new GameData(0, null, null, params[0], new ChessGame());
            int gameID = server.createGame(game, authToken);
            gameName = params[0];
            return String.format("You created chess game named %s. Assigned chess ID: %d", gameName, gameID);
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        server.logout(authToken);
        state = State.SIGNEDOUT;
        return String.format("%s signed out", visitorName);

    }

    private String login(String[] params) throws ResponseException {
        if(params.length >= 1 && params.length <=2){
            state = State.SIGNEDIN;
            visitorName = params[0];
            var loginInfo = new LoginReqData(params[0], params[1]);
            authToken = server.login(loginInfo).authToken();
            return String.format("You logged in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    private String register(String[] params) throws ResponseException {
        if(params.length >= 1 && params.length <=3){
            state = State.SIGNEDIN;
            visitorName = params[0];
            var user = new UserData(params[0], params[1], params[2]);
            authToken = server.register(user).authToken();
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");

    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - create an account
                    - login <USERNAME> <PASSWORD> - to play chess
                    - help - possible commands
                    - quit
                    """;
        }
        return """
                - create <gameName>
                - join <gameID> [WHITE|BLACK]
                - observe <gameID>
                - list - lists all games
                - logout
                - quit
                - help
                """;
    }
    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
