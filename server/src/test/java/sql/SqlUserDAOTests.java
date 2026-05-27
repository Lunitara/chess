package sql;

import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlUserDAOTests {
    private SqlUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SqlUserDAO();
        userDAO.clearUserData();
    }
    @Test
    void positiveTestRegister()  throws DataAccessException{
        //passes
        String hashedPassword = BCrypt.hashpw("llama", BCrypt.gensalt());
        UserData testUser = new UserData("testingUsername", hashedPassword, "testingEmail");
        userDAO.createUser(testUser);
        userDAO.getUser("testingUsername");

    }

    @Test
    void positiveTestLogin()  throws DataAccessException{
        //passes
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        UserData testUser = new UserData("testingUsername2", hashedPassword, "testingEmail");
        userDAO.createUser(testUser);
        userDAO.getUser("testingUsername2");

    }

    @Test
    void negativeTestLogin()  throws DataAccessException{
        //put in wrong password
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        UserData testUser = new UserData("testingUsername2", hashedPassword, "testingEmail");
        userDAO.createUser(testUser);
        assertThrows(IllegalArgumentException.class, () -> {
                userDAO.getUser("wrongPassword");
         });

    }

    @Test
    void negativeTestRegister()  throws DataAccessException{
        //fails because password is blank
        assertThrows(IllegalArgumentException.class, () -> {
            userDAO.createUser(new UserData("Carl", "", "mon@gmail.com"));
        });
    }
    @Test
    void testClear()  throws DataAccessException {
        UserData testUser = new UserData("testingUsername", "testingPassword", "testingEmail");
        userDAO.createUser(testUser);
        userDAO.clearUserData();
    }
}
