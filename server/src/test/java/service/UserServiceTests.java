package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class UserServiceTests {
    public UserService.RegisterResult register(UserData user) {
        String authToken = AuthData.generateToken();
        UserData existingUser = users.getUser(user.username());
        if (existingUser != null) {
            throw new IllegalArgumentException("Already Taken Exception");
        }
        users.createUser(user);
        AuthData authData = new AuthData(authToken,user.username());
        auths.createAuth(authData);

        return new UserService.RegisterResult(user.username(), authToken);
    }


    public UserService.LoginResult login(UserService.LoginRequest loginRequest) {
        String authToken = AuthData.generateToken();
        UserData existingUser = users.getUser(loginRequest.username());
        if (existingUser != null) {
            if (Objects.equals(existingUser.password(), loginRequest.password())) {
                AuthData authData = new AuthData( authToken, existingUser.username());
                auths.createAuth(authData);
                return new UserService.LoginResult(existingUser.username(), authToken);
            }
            else {
                throw new IllegalArgumentException("Passwords don't match");
            }
        }
        else {
            throw new IllegalArgumentException("Error null user");
        }

    }

    public void logout(String authToken) {
        AuthData authData = auths.getAuth(authToken);
        if (authData == null) {
            throw new IllegalArgumentException("Error not logged in");
        }
        else {
            auths.deleteAuth(authData);
        }
    }

    @Test
    void testClear() {
        UserService userService = new UserService( new GameDAO(),new UserDAO(),new AuthDAO());
        userService.clearUserData();

    }
}
