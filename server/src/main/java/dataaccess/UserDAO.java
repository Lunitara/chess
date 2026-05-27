package dataaccess;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public interface UserDAO {
// CREATE OBJECTS FROM DATA STORE
     void createUser(UserData userdata) throws DataAccessException;
    //READ OBJECTS FROM DATA STORE
     UserData getUser(String username)  throws DataAccessException ;


    //DELETE OBJECTS FROM DATA STORE
     void clearUserData()  throws DataAccessException ;
}
