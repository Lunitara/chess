package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTests {
    UserService userService;
    String authToken;

    @BeforeEach
    public void setUp() {
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();
        UserDAO userDAO = new UserDAO();
        userService = new UserService(gameDAO, userDAO,authDAO);
        UserService.RegisterResult registerResult = userService.register(new UserData("Carl", "llama", "mon@gmail.com"));
        authToken = registerResult.authToken();

    }
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


    @Test
    void testLogout() {
        //passes
        userService.register(new UserData("Carl", "llama", "mon@gmail.com"));
        userService.logout(authToken);
        //cannot log out twice so fails
        assertThrows(IllegalArgumentException.class, () -> {
            userService.logout(authToken);
        });
    }
    @Test
    void testClear() {
        UserService userService = new UserService( new GameDAO(),new UserDAO(),new AuthDAO());
        userService.clearUserData();

    }
}
