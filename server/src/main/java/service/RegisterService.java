package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.UserDAO;

import java.util.HashMap;

public class RegisterService {

    public RegisterService(MemoryDataAccess memoryDataAccess) {
    }

    public static HashMap<String, String> createUser(String username, String password, String email) throws DataAccessException {
        if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
            throw new DataAccessException("Error: bad request - missing username, password, or email");
        }
        if (MemoryDataAccess.getUser(username) != null){
            throw new DataAccessException("Error: already taken");
        }

        UserDAO newUser = new UserDAO(username, password, email);
        MemoryDataAccess.createUser(newUser);
        String authToken = String.valueOf(MemoryDataAccess.createAuthToken(username));

        HashMap<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("authToken", authToken);
        return response;
    }
}
