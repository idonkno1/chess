package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

public class LogoutService {
    private final DataAccess dataAccess;
    public LogoutService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public void logoutUser(String authToken) throws DataAccessException {
        if (authToken.isEmpty() || authToken == null){
            throw new DataAccessException("Error: Invalid authToken");
        }

        var tokenTest = dataAccess.getAuthToken(authToken);
        if (tokenTest == null){
            throw new DataAccessException("Error: Invalid authToken");
        }
        dataAccess.deleteAuthToken(authToken);
    }
}
