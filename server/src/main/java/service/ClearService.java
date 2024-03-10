package service;

import dataAccess.*;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public void clearDatabase() throws DataAccessException {
        try{
            dataAccess.clearDAO();
        }catch (Exception e){
            throw new DataAccessException("Error clearing database");
        }
    }
}
