package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.UserData;

import java.util.HashMap;

public class RegisterService {
    private final DataAccess dataAccess;
    public RegisterService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public HashMap<String, String> createUser(String username, String password, String email) throws DataAccessException {
        UserData newUser = new UserData(username, password, email);
        dataAccess.createUser(newUser);
        String authToken = String.valueOf(dataAccess.createAuthToken(username).authToken());

        HashMap<String, String> response = new HashMap<>();
        response.put("authToken", authToken);
        response.put("username", username); //switched these two if tests dont pass
        return response;
    }
}
