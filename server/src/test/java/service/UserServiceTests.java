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


    @Test
    void positiveTestRegister() {
        //passes
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
    }

    @Test
    void positiveTestLogin() {
        //passes
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
        userService.login(new UserService.LoginRequest("Carl", "llama"));

    }

    @Test
    void positiveTestLogout() {
        //passes
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
    }

    @Test
    void negativeTestRegister() {
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
        //blank register spot
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(new UserData("Carl", "", "mon@gmail.com"));
        });
    }

    @Test
    void negativeTestLogin() {
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
        userService.login(new UserService.LoginRequest("Carl", "llama"));
        //not logged in
        assertThrows(IllegalArgumentException.class, () -> {
            userService.logout(authToken);
        });
        //cannot log out twice so fails
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login(new UserService.LoginRequest("Carl", "wrongPassword"));
        });
    }

    @Test
    void negativeTestLogout() {
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
        //cannot log out twice so fails
        assertThrows(IllegalArgumentException.class, () -> {
            userService.logout(authToken);
        });
        userService.login(new UserService.LoginRequest("Carl", "llama"));
        //wrong auth token but tries to log out
        assertThrows(IllegalArgumentException.class, () -> {
            userService.logout("wrongAuthToken");
        });
    }
    @Test
    void testClear() {
        UserService userService = new UserService( new GameDAO(),new UserDAO(),new AuthDAO());
        userService.clearUserData();

    }
}
