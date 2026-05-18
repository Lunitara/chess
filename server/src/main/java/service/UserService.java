package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService(UserDAO userdao, GameDAO gamedao, AuthDAO authdao) {
    private UserDAO users;
    private GameDAO games;
    private AuthDAO auths;

    public record RegisterResult(String username, String authToken) {
    }

    public record RegisterRequest(String username, String password, String email) {
    }

    public record LoginResult(String username, String AuthToken) {
    }

    public record LoginRequest(String username, String password) {
    }

    public record LogoutRequest() {
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


    public UserData login(LoginRequest loginRequest) {
        return null;
    }

    public void logout(LogoutRequest logoutRequest) {
    }

    boolean checkPassword(String password) {


        return false;
    }

    public void clearUserData() {
        users.clearUserData();
    }
}
