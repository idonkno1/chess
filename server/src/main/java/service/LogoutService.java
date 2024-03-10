package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

public class LogoutService {
    private final DataAccess dataAccess;
    public LogoutService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public void logoutUser(String authToken) throws DataAccessException {
        dataAccess.deleteAuthToken(authToken);
    }
}
