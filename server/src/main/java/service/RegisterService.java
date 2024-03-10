package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.UserDAO;

import java.util.HashMap;

public class RegisterService {
    private final DataAccess dataAccess;
    public RegisterService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public HashMap<String, String> createUser(String username, String password, String email) throws DataAccessException {
        if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
            throw new DataAccessException("Error: bad request - missing username, password, or email");
        }
        if (dataAccess.getUser(username) != null){
            throw new DataAccessException("Error: already taken");
        }

        UserDAO newUser = new UserDAO(username, password, email);
        dataAccess.createUser(newUser);
        String authToken = String.valueOf(dataAccess.createAuthToken(username));

        HashMap<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("authToken", authToken);
        return response;
    }
}
