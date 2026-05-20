package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

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

    public record LoginResult(String username, String AuthToken) {
    }

    public record LoginRequest(String username, String password) {
    }

    public record LogoutRequest(String AuthToken) {
    }

    public RegisterResult register(UserData user) {
        String authToken = AuthData.generateToken();
        UserData existingUser = users.getUser(user.username());
        if (existingUser != null) {
            throw new IllegalArgumentException("Already Taken Exception");
        }
        users.createUser(user);
        AuthData authData = new AuthData(user.username(), authToken);
        auths.createAuth(authData);

        return new RegisterResult(user.username(), authToken);
    }


    public LoginResult login(LoginRequest loginRequest) {
        String authToken = AuthData.generateToken();
        UserData existingUser = users.getUser(loginRequest.username());
        if (existingUser != null) {
            if (Objects.equals(existingUser.password(), loginRequest.password())) {
                AuthData authData = new AuthData(existingUser.username(), authToken);
                auths.createAuth(authData);
                return new LoginResult(existingUser.username(), authToken);
            }
            else {
                throw new IllegalArgumentException("Passwords don't match");
            }
        }
        else {
            throw new IllegalArgumentException("Error null user");
        }

    }

    public void Logout(LogoutRequest logoutRequest) {
        String authToken = AuthData.generateToken();
        if (authToken == null) {
            throw new IllegalArgumentException("Error null user");
        }
        else {
            AuthData authData = new AuthData(authData.username(), authToken);
            auths.deleteAuth(authData);
        }
    }

    boolean checkPassword(String password) {


        return false;
    }

    public void clearUserData() {
        users.clearUserData();
    }
}
