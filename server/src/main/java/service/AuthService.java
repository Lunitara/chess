package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;

public class AuthService {
    private UserDAO users;

    public AuthService(UserDAO users, GameDAO games, AuthDAO auths) {
        this.users = users;
        this.games = games;
        this.auths = auths;
    }



    private GameDAO games;
    private AuthDAO auths;
    public void clearAuthData() {
        auths.clearAuthData();
    }
}
