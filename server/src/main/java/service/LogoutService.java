package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

public class LogoutService {
    private final DataAccess dataAccess;
    public LogoutService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public void logoutUser(String authToken) throws DataAccessException {
        boolean success = dataAccess.deleteAuthToken(authToken);
        if(!success){
            throw new DataAccessException("Error: unauthorized - invalid authToken");
        }

    }
}
