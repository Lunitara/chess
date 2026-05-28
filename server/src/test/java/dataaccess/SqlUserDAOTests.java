package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlUserDAOTests {
    private SqlUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SqlUserDAO();
        userDAO.clearUserData();
    }
    @Test
    void positiveTestCreateUser()  throws DataAccessException{
        //passes
        String hashedPassword = BCrypt.hashpw("llama", BCrypt.gensalt());
        UserData testUser = new UserData("testingUsername", hashedPassword, "testingEmail");
        userDAO.createUser(testUser);
        userDAO.getUser("testingUsername");

    }

    @Test
    void positiveTestGetUser()  throws DataAccessException{
        //passes
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        UserData testUser = new UserData("testingUsername2", hashedPassword, "testingEmail");
        userDAO.createUser(testUser);
        userDAO.getUser("testingUsername2");

    }

    @Test
    void negativeTestCreateUser()  throws DataAccessException{
        //already logged in
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        UserData testUser = new UserData("testingUsername2", hashedPassword, "testingEmail");
        userDAO.createUser(testUser);
        UserData testUser2 = new UserData("testingUsername2", hashedPassword, "testingEmail");
        assertThrows(DataAccessException.class, () -> {
        userDAO.createUser(testUser2);
        });
    }
    @Test
    void negativeTestGetUser()  throws DataAccessException{
        //fails
        String hashedPassword = BCrypt.hashpw("apple", BCrypt.gensalt());
        UserData testUser = new UserData("testingUsername2", hashedPassword, "testingEmail");
        userDAO.createUser(testUser);
        assertNull(userDAO.getUser("wrong Username"));

    }



    @Test
    void testClear()  throws DataAccessException {
        UserData testUser = new UserData("testingUsername", "testingPassword", "testingEmail");
        userDAO.createUser(testUser);
        userDAO.clearUserData();
    }
}
