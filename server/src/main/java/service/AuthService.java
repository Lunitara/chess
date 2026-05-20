package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;

public class AuthService {
    private UserDAO users;
    private GameDAO games;
    private AuthDAO auths;

    public AuthService(UserDAO users, GameDAO games, AuthDAO auths) {
        this.users = users;
        this.games = games;
        this.auths = auths;
    }

    public void clearAuthData() {
        auths.clearAuthData();
    }
}
