package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public interface AuthDAO {
    //CREATE OBJECTS FROM DATA STORE
     void createAuth(AuthData authData)  throws DataAccessException;
    //READ OBJECTS FROM DATA STORE
     AuthData getAuth(String authToken)  throws DataAccessException;
    //DELETE OBJECTS FROM DATA STORE

     void deleteAuth(AuthData authData)  throws DataAccessException;
     void clearAuthData()  throws DataAccessException;
}
