package sql;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlAuthDAOTests {

    private SqlAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SqlAuthDAO();
        authDAO.clearAuthData();
    }
    @Test
    void positiveTestCreateAuth()  throws DataAccessException{
        //passes
        AuthData testAuth = new AuthData("authTestToken", "Sam");
        authDAO.createAuth(testAuth);
        authDAO.getAuth("testingAuthname");

    }

    @Test
    void positiveTestGetAuth()  throws DataAccessException{
        //passes
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        AuthData testAuth = new AuthData("authTestToken", "Sam");
        authDAO.createAuth(testAuth);
        authDAO.getAuth("testingAuthname2");

    }
    @Test
    void positiveDeleteAuth()  throws DataAccessException{
        //passes
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        AuthData testAuth = new AuthData("authTestToken", "Sam");
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(testAuth);

    }
    @Test
    void negativeDeleteAuth()  throws DataAccessException{
        //fails because it should be deleted
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        AuthData testAuth = new AuthData("authTestToken", "Sam");
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(testAuth);
        assertThrows(IllegalArgumentException.class, () -> {

            authDAO.getAuth("testingAuthname2");
    });

    }

    @Test
    void negativeTestCreateAuth()  throws DataAccessException{
        //already exists
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        AuthData testAuth = new AuthData("authTestToken", "Sam");
        authDAO.createAuth(testAuth);
        AuthData testAuth2 = new AuthData("authTestToken", "Sam");
        assertThrows(IllegalArgumentException.class, () -> {
            authDAO.createAuth(testAuth2);
        });
    }
    @Test
    void negativeTestGetAuth()  throws DataAccessException{
        //passes
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        AuthData testAuth = new AuthData("authTestToken", "Sam");
        authDAO.createAuth(testAuth);
        assertThrows(IllegalArgumentException.class, () -> {
            authDAO.getAuth("wrong authToken name");        });
    }



    @Test
    void testClear()  throws DataAccessException {
        AuthData testAuth = new AuthData("testingToken", "testingname");
        authDAO.createAuth(testAuth);
        authDAO.clearAuthData();
    }
}
