package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.GameData;
import model.UserData;
import service.*;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final dataAccess.DataAccess dataAccess = new MemoryDataAccess();
    private final ClearService clearService = new ClearService(dataAccess);
    private final CreateGameService createGameService = new CreateGameService(dataAccess);
    private final JoinGameService joinGameService = new JoinGameService(dataAccess);
    private final ListGamesService listGamesService = new ListGamesService(dataAccess);
    private final LoginService loginService = new LoginService(dataAccess);
    private final LogoutService logoutService = new LogoutService(dataAccess);
    private final RegisterService registerService = new RegisterService(dataAccess);

    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::Clear); //clear
        Spark.post("/user", this::Register); //register
        Spark.post("/session", this::Login); //login
        Spark.delete("/session", this::Logout); //logout
        Spark.get("/game", this::ListGames); //list games
        Spark.post("/game", this::CreateGame); //create game
        Spark.put("/game", this::JoinGame); //join game

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object Clear(Request req, Response res) throws DataAccessException {
        clearService.clearDatabase();
        res.status(200);
        return "";
    }
    private Object CreateGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (!dataAccess.isValidAuth(authToken)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }
        var reqGame = new Gson().fromJson(req.body(), String.class);

        GameData game = createGameService.createGame(reqGame);
        res.type("application.json");
        return new Gson().toJson(Map.of("gameID", game.gameID()));
    }
    private Object JoinGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (!dataAccess.isValidAuth(authToken)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }

        var joinReq = new Gson().fromJson(req.body(), GameData.class);

        if (dataAccess.getGame(joinReq.gameID()) == null) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }
        String whiteUsername = dataAccess.getGame(joinReq.gameID()).whiteUsername();
        String blackUsername = dataAccess.getGame(joinReq.gameID()).blackUsername();


        boolean success = joinGameService.joinGame(joinReq, authToken);

        res.status(200);
        return "";
    }
    private Object ListGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if(!dataAccess.isValidAuth(authToken)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }

        ArrayList<Object> games = listGamesService.listGames();
        res.type("application/json");
        return new Gson().toJson(Map.of("games", games));
    }
    private Object Login(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        String username = dataAccess.getAuthToken(authToken).username();
        String password =  dataAccess.getUser(username).password();

        if (!dataAccess.isValidAuth(authToken)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }
        var loginInfo = new Gson().fromJson(req.body(), UserData.class);
        if (loginInfo == null || !loginInfo.password().equals(password) || !loginInfo.username().equals(username)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }

        HashMap<String, String> userSession = loginService.loginUser(loginInfo.username());
        res.status(200);
        res.type("application/json");
        return new Gson().toJson(userSession);
    }
    private Object Logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if(!dataAccess.isValidAuth(authToken)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }
        logoutService.logoutUser(authToken);
        res.status(200);
        return "";
    }
    private Object Register(Request req, Response res) throws DataAccessException {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        if(newUser.username() == null || newUser.password() == null || newUser.email() == null){
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }

        if (dataAccess.getUser(newUser.username()) != null){
            res.status(403);
            return new Gson().toJson(Map.of("message", "Error: already taken"));        }

        HashMap<String, String> registeredUser = registerService.createUser(newUser.username(), newUser.password(), newUser.email());
        res.status(200);
        res.type("application/json");
        return new Gson().toJson(registeredUser);
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
