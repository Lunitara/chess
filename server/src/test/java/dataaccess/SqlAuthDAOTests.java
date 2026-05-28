package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertNull;
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
        assertNull( authDAO.getAuth("authTestToken"));


    }

    @Test
    void negativeTestCreateAuth()  throws DataAccessException{
        //already exists
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        AuthData testAuth = new AuthData("authTestToken", "Sam");
        authDAO.createAuth(testAuth);
        AuthData testAuth2 = new AuthData("authTestToken", "Sam");
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(testAuth2);
        });
    }
    @Test
    void negativeTestGetAuth()  throws DataAccessException {
        //fails because does not exist
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        AuthData testAuth = new AuthData("authTestToken", "Sam");
        authDAO.createAuth(testAuth);
        assertNull(authDAO.getAuth("authTestToken5"));
    }



    @Test
    void testClear()  throws DataAccessException {
        AuthData testAuth = new AuthData("testingToken", "testingname");
        authDAO.createAuth(testAuth);
        authDAO.clearAuthData();
    }
}
