package dataaccess;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class UserDAO {
    ArrayList<UserData> allUsers = new ArrayList<>();
// CREATE OBJECTS FROM DATA STORE
    public void createUser(UserData userdata) {
        allUsers.add(userdata);
    }
    //READ OBJECTS FROM DATA STORE
    public UserData getUser(String username) {
        for (int i = 0; i < allUsers.size(); i++) {
            if (Objects.equals(allUsers.get(i).username(), username)) {
                return allUsers.get(i);
            }
        }
        return null;
    }


    //DELETE OBJECTS FROM DATA STORE
    public void clearUserData() {
        allUsers.clear();
    }
}
