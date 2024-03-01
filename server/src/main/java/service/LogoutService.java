package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;

public class LogoutService {
    public LogoutService(MemoryDataAccess memoryDataAccess) {
    }

    public void logoutUser(String authToken) throws DataAccessException {
        boolean success = MemoryDataAccess.deleteAuthToken(authToken);
        if(!success){
            throw new DataAccessException("Error: unauthorized - invalid authToken");
        }

    }
}
