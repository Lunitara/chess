package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.Test;

public class AuthServiceTests {
    @Test
    void testClear() {
        AuthService authService = new AuthService(new UserDAO(),new GameDAO(), new AuthDAO());
        authService.clearAuthData();
        
    }
}
