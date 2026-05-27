package sql;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

public class SqlAuthDAOTests {

    private SqlAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SqlAuthDAO();
        authDAO.clearAuthData();
    }
    @Test
    void testClear()  throws DataAccessException {
        AuthData testAuth = new AuthData("testingToken", "testingname");
        authDAO.createAuth(testAuth);
        authDAO.clearAuthData();
    }
}
