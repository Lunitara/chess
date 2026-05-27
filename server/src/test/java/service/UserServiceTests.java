package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTests {
    UserService userService;
    String authToken;

    @BeforeEach
    public void setUp()  throws DataAccessException{
        AuthDAO authDAO = new AuthMemoryDAO();
        GameDAO gameDAO = new GameMemoryDAO();
        UserDAO userDAO = new UserMemoryDAO();

        userService = new UserService(gameDAO, userDAO,authDAO);
        UserService.RegisterResult registerResult = userService.register(new UserData("Carl", "llama", "mon@gmail.com"));
        authToken = registerResult.authToken();

    }


    @Test
    void positiveTestRegister()  throws DataAccessException{
        //passes
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
    }

    @Test
    void positiveTestLogin()  throws DataAccessException{
        //passes
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
        userService.login(new UserService.LoginRequest("Carl", "llama"));

    }

    @Test
    void positiveTestLogout() throws DataAccessException {
        //passes
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
    }

    @Test
    void negativeTestRegister()  throws DataAccessException{
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
        //blank register spot
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(new UserData("Carl", "", "mon@gmail.com"));
        });
    }

    @Test
    void negativeTestLogin()  throws DataAccessException{
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
    void negativeTestLogout()  throws DataAccessException{
        userService.register(new UserData("Sam", "llama", "mon@gmail.com"));
        userService.logout(authToken);
        //cannot log out twice so fails
        assertThrows(IllegalArgumentException.class, () -> {
            userService.logout(authToken);
        });
        userService.login(new UserService.LoginRequest("Carl", "llama"));
        //wrong auth token but tries to log out
        assertThrows(IllegalArgumentException.class, () -> {
            userService.logout("wrong authToken");
        });
    }
    @Test
    void testClear() throws DataAccessException {
        UserService userService = new UserService( new GameMemoryDAO(),new UserMemoryDAO(),new AuthMemoryDAO());
        userService.clearUserData();

    }
}
