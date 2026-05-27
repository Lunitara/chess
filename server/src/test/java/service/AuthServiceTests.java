package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;

public class AuthServiceTests {
    @Test
    void testClear()  throws DataAccessException {
        AuthService authService = new AuthService(new UserMemoryDAO(),new GameMemoryDAO(), new AuthMemoryDAO());
        authService.clearAuthData();
        
    }
}
