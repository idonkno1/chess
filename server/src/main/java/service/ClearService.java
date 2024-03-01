package service;

import dataAccess.*;

public class ClearService {
    private static MemoryDataAccess memoryDataAccess;

    public ClearService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public static void clearDatabase() throws DataAccessException {
        try{
            memoryDataAccess.clearDAO();
        }catch (Exception e){
            throw new DataAccessException("Error clearing database");
        }
    }
}
