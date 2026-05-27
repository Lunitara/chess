package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class UserService {
    private final UserDAO users;
    private final GameDAO games;
    private final AuthDAO auths;

    public UserService(GameDAO games, UserDAO users, AuthDAO auths) {
        this.games = games;
        this.users = users;
        this.auths = auths;
    }

    public record RegisterResult(String username, String authToken) {
    }

    public record RegisterRequest(String username, String password, String email) {
    }

    public record LoginResult(String username, String authToken) {
    }

    public record LoginRequest(String username, String password) {
    }

    public RegisterResult register(UserData user)  throws DataAccessException {
        String authToken = AuthData.generateToken();
        UserData existingUser = users.getUser(user.username());
        if (existingUser != null) {
            throw new IllegalArgumentException("Already Taken Exception");
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData hashedUser = new UserData(user.username(), hashedPassword, user.email());
        users.createUser(hashedUser);
        AuthData authData = new AuthData(authToken,user.username());
        auths.createAuth(authData);

        return new RegisterResult(user.username(), authToken);
    }



    public LoginResult login(LoginRequest loginRequest)  throws DataAccessException {
        String authToken = AuthData.generateToken();
        UserData existingUser = users.getUser(loginRequest.username());
        if (existingUser != null) {
            boolean verifyUser = BCrypt.checkpw(loginRequest.password, existingUser.password());
            if (!verifyUser) {
                throw new DataAccessException("Error password doesn't match");
            }
                AuthData authData = new AuthData( authToken, existingUser.username());
                auths.createAuth(authData);
                return new LoginResult(existingUser.username(), authToken);

        }
        else {
            throw new DataAccessException("Error null user");
        }

    }

    public void logout(String authToken)  throws DataAccessException{
        AuthData authData = auths.getAuth(authToken);
        if (authData == null) {
            throw new IllegalArgumentException("Error not logged in");
        }
        else {
            auths.deleteAuth(authData);
        }
    }

    public void clearUserData()  throws DataAccessException {
        users.clearUserData();
    }
}
