package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.UserDAO;

import java.util.HashMap;

public class LoginService {
    public LoginService(MemoryDataAccess memoryDataAccess) {
    }

    public static HashMap<String, String> loginUser(String username, String password) throws DataAccessException {
        UserDAO user = MemoryDataAccess.getUser(username);

        if (user == null || !user.getPassword().equals(password)){
            throw new DataAccessException("Error: unauthorized - invalid username or password");
        }
        String authToken = String.valueOf(MemoryDataAccess.createAuthToken(username));

        HashMap<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("authToken", authToken);
        return response;

    }
}
