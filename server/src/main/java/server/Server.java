package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.GameData;
import model.JoinReqData;
import model.UserData;
import service.*;
import spark.Request;
import spark.Response;
import spark.Spark;

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

        Spark.delete("/db", this::clear); //clear
        Spark.post("/user", this::register); //register
        Spark.post("/session", this::login); //login
        Spark.delete("/session", this::logout); //logout
        Spark.get("/game", this::listGames); //list games
        Spark.post("/game", this::createGame); //create game
        Spark.put("/game", this::joinGame); //join game

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        clearService.clearDatabase();
        res.status(200);
        return "";
    }
    private Object createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (!dataAccess.isValidAuth(authToken)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }
        var gameName = new Gson().fromJson(req.body(), GameData.class);
        try {
            GameData game = createGameService.createGame(gameName);
            res.status(200);
            res.type("application.json");
            return new Gson().toJson(Map.of("gameID", game.gameID()));
        }catch (Exception e){
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }
    }
    private Object joinGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (!dataAccess.isValidAuth(authToken)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }

        var joinReq = new Gson().fromJson(req.body(), JoinReqData.class);

        if (dataAccess.getGame(joinReq.gameID()) == null) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }
        try{
            joinGameService.joinGame(joinReq, authToken);
            res.status(200);
            return new Gson().toJson(Map.of("success", true));

        } catch (Exception e) {
            res.status(403);
            return new Gson().toJson(Map.of("message", "Error: already taken"));
        }

    }
    private Object listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if(!dataAccess.isValidAuth(authToken)){
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }

        var games = listGamesService.listGames();
        res.type("application/json");
        return new Gson().toJson(Map.of("games", games));
    }
    private Object login(Request req, Response res) {

        var loginInfo = new Gson().fromJson(req.body(), JsonElement.class);

        JsonObject obj = loginInfo.getAsJsonObject();
        String username = obj.get("username").getAsString();
        String password = obj.get("password").getAsString();

        try{
            HashMap<String, String> userSession = loginService.loginUser(username, password);
            res.status(200);
            res.type("application/json");
            return new Gson().toJson(userSession);

        } catch (Exception e) {
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }
    }
    private Object logout(Request req, Response res) {
        String authToken = req.headers("Authorization");

        try{
            logoutService.logoutUser(authToken);
            res.status(200);
            return new Gson().toJson(Map.of("success", true));

        } catch (Exception e) {
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }
    }
    private Object register(Request req, Response res) throws DataAccessException {
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
