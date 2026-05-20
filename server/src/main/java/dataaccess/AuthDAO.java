package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class AuthDAO {
    //CREATE OBJECTS FROM DATA STORE
    ArrayList<AuthData> allAuths = new ArrayList<>();
    public void createAuth(AuthData authData) {
        allAuths.add(authData);
    }
    //READ OBJECTS FROM DATA STORE
    public AuthData getAuth(String authToken) {
        for (AuthData allAuth : allAuths) {
            if (Objects.equals(allAuth.authToken(), authToken)) {
                return allAuth;
            }
        }
        return null;
    }
    //DELETE OBJECTS FROM DATA STORE

    public void deleteAuth(AuthData authData) {
        allAuths.remove(authData);
    }
    public void clearAuthData() {
        allAuths.clear();
    }
}
