package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import spark.*;

import service.*;


import java.util.Collection;
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
        var reqGame = new Gson().fromJson(req.body(), GameDAO.class);

        GameDAO game = createGameService.createGame(authToken, reqGame);
        res.type("application.json");
        return new Gson().toJson(Map.of("gameID", game.getGameID()));
    }
    private Object JoinGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (authToken == null || authToken.isEmpty()){
            throw new DataAccessException("Error: unauthorized - missing authToken");
        }
        var joinReq = new Gson().fromJson(req.body(), GameDAO.class);

        boolean success = joinGameService.joinGame(joinReq);

        if(!success){
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }

        res.status(200);
        return "";
    }
    private Object ListGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");

        Collection<GameDAO> games = listGamesService.listGames(authToken);
        res.type("application/json");
        return new Gson().toJson(Map.of("games", games));
    }
    private Object Login(Request req, Response res) throws DataAccessException {
        var loginInfo = new Gson().fromJson(req.body(), UserDAO.class);

        HashMap<String, String> userSession = loginService.loginUser(loginInfo.getUsername(), loginInfo.getPassword());
        res.status(200);
        res.type("application/json");
        return new Gson().toJson(userSession);
    }
    private Object Logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");

        logoutService.logoutUser(authToken);
        res.status(200);
        return "";
    }
    private Object Register(Request req, Response res) throws DataAccessException {
        var newUser = new Gson().fromJson(req.body(), UserDAO.class);

        HashMap<String, String> registeredUser = registerService.createUser(newUser.getUsername(), newUser.getPassword(), newUser.getEmail());
        res.status(200);
        res.type("application/json");
        return new Gson().toJson(registeredUser);
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
