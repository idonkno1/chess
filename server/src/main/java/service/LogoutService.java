package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

public class LogoutService {
    private final DataAccess dataAccess;
    public LogoutService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public void logoutUser(String authToken) throws DataAccessException {
        if(!dataAccess.isValidAuth(authToken)){
            throw new DataAccessException("Error: unauthorized - invalid or expired authToken");
        }
        dataAccess.deleteAuthToken(authToken);
    }
}
