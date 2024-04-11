package ui.server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {serverUrl = url;}

    public void clearData() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public AuthData register(UserData userData) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData login(LoginReqData loginInfo) throws ResponseException{
        var path = "/session";
        return this.makeRequest("POST", path, loginInfo, AuthData.class, null);
    }

    public void logout(String authToken) throws ResponseException{
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null, authToken);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        var path = "/game";

        record ListGame(GameData[] games){}

        var response = this.makeRequest("GET", path, null, ListGame.class, authToken);
        return response.games();
    }

    public int createGame(GameData gameName, String authToken) throws ResponseException{
        var path = "/game";
        return this.makeRequest("POST", path, gameName, GameData.class, authToken).gameID();
    }

    public ChessGame joinGame(JoinReqData joinReqData, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, joinReqData, ChessGame.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
