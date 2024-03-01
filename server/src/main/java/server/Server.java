package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.GameDAO;
import model.UserDAO;
import spark.Request;
import spark.Response;
import spark.Spark;
import service.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private dataAccess.MemoryDataAccess MemoryDataAccess;
    private final ClearService clearService = new ClearService(MemoryDataAccess);
    private final CreateGameService createGameService = new CreateGameService(MemoryDataAccess);
    private final JoinGameService joinGameService = new JoinGameService(MemoryDataAccess);
    private final ListGamesService listGamesService = new ListGamesService(MemoryDataAccess);
    private final LoginService loginService = new LoginService(MemoryDataAccess);
    private final LogoutService logoutService = new LogoutService(MemoryDataAccess);
    private final RegisterService registerService = new RegisterService(MemoryDataAccess);


    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::Clear); //clear
        Spark.post("/user", this::Register); //register
        Spark.post("/session", this::Login); //login
        Spark.post("/session", this::Logout); //logout
        Spark.get("/game", this::ListGames); //list games
        Spark.post("/game", this::CreateGame); //create game
        Spark.put("/game", this::JoinGame); //join game


        Spark.awaitInitialization();
        return Spark.port();
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

    private Object CreateGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (authToken == null || authToken.isEmpty()){
            throw new DataAccessException("Error: unauthorized - missing authToken");
        }

        var reqGame = new Gson().fromJson(req.body(), GameDAO.class);
        String gameName = reqGame.getGameName();
        if (gameName == null || gameName.isEmpty()){
            throw new DataAccessException("Error: bad request - gameName is required");
        }

        GameDAO game = createGameService.createGame(authToken, reqGame);

        res.type("application.json");
        return new Gson().toJson(Map.of("gameID", game.getGameID()));
    }

    private Object ListGames(Request req, Response res) throws DataAccessException {

        String authToken = req.headers("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("Error: unauthorized - missing or invalid authToken");
        }

        Collection<GameDAO> games = listGamesService.listGames(authToken);

        res.type("application/json");
        return new Gson().toJson(Map.of("games", games));
    }

    private Object Logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (authToken == null || authToken.isEmpty()){
            throw new DataAccessException("Error: unauthorized - missing authToken");
        }

        logoutService.logoutUser(authToken);
        res.status(200);
        return "";
    }

    private Object Login(Request req, Response res) throws DataAccessException {
        var loginInfo = new Gson().fromJson(req.body(), UserDAO.class);
        if(loginInfo == null || loginInfo.getUsername().isEmpty() || loginInfo.getPassword().isEmpty()){
            throw new DataAccessException("Error: bad request - invalid username or password");
        }
        HashMap<String, String> userSession = loginService.loginUser(loginInfo.getUsername(), loginInfo.getPassword());
        res.status(200);
        res.type("application/json");
        return new Gson().toJson(userSession);
    }

    private Object Register(Request req, Response res) throws DataAccessException {
        var newUser = new Gson().fromJson(req.body(), UserDAO.class);
        if(newUser == null){
            throw new DataAccessException("Error: bad request - invalid input");
        }
        HashMap<String, String> registeredUser = registerService.createUser(newUser.getUsername(), newUser.getPassword(), newUser.getEmail());
        res.status(200);
        res.type("application/json");
        return new Gson().toJson(registeredUser);
    }

    private Object Clear(Request req, Response res) throws DataAccessException {
        clearService.clearDatabase();
        res.status(200);
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }



}
