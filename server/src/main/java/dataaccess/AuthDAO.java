package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public interface AuthDAO {
    //CREATE OBJECTS FROM DATA STORE
     void createAuth(AuthData authData);
    //READ OBJECTS FROM DATA STORE
     AuthData getAuth(String authToken);
    //DELETE OBJECTS FROM DATA STORE

     void deleteAuth(AuthData authData);
     void clearAuthData();
}
