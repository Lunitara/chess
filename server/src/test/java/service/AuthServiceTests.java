package service;

import dataaccess.AuthMemoryDAO;
import dataaccess.GameMemoryDAO;
import dataaccess.UserDAO;
import dataaccess.UserMemoryDAO;
import org.junit.jupiter.api.Test;

public class AuthServiceTests {
    @Test
    void testClear() {
        AuthService authService = new AuthService(new UserMemoryDAO(),new GameMemoryDAO(), new AuthMemoryDAO());
        authService.clearAuthData();
        
    }
}
