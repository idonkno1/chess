package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.UserDAO;

import java.util.HashMap;

public class LoginService {
    private final DataAccess dataAccess;
    public LoginService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public HashMap<String, String> loginUser(String username, String password) throws DataAccessException {
        UserDAO user = dataAccess.getUser(username);

        if (user == null || !user.getPassword().equals(password)){
            throw new DataAccessException("Error: unauthorized - invalid username or password");
        }
        String authToken = String.valueOf(dataAccess.createAuthToken(username));

        HashMap<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("authToken", authToken);
        return response;

    }
}
