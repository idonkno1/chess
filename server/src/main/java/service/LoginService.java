package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

import java.util.HashMap;

public class LoginService {
    private final DataAccess dataAccess;
    public LoginService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public HashMap<String, String> loginUser(String username, String password) throws DataAccessException {
        if (username.isEmpty() ){
            throw new DataAccessException("Error: Incorrect username or password");
        }

        var userTest = dataAccess.getUser(username);
        if (userTest == null){
            throw new DataAccessException("Error: Incorrect username or password");
        }

        var userPassword = userTest.password();

        if (password.isEmpty() || !password.equals(userPassword)){
            throw new DataAccessException("Error: Incorrect username or password");
        }

        String authToken = String.valueOf(dataAccess.createAuthToken(username).authToken());

        HashMap<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("authToken", authToken);
        return response;

    }
}
