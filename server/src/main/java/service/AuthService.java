package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class AuthService {
    private UserDAO users;

    public AuthService(UserDAO users, GameDAO games, AuthDAO auths) {
        this.users = users;
        this.games = games;
        this.auths = auths;
    }

    public void Logout(String authToken) {
        String authTok = auths.getAuth(authToken);
        if (authToken != null) {
            auths.deleteAuth(authToken);
        }
        else {
            throw new IllegalArgumentException("error unauthorized");
        }
    }

    private GameDAO games;
    private AuthDAO auths;
    public void clearAuthData() {
        auths.clearAuthData();
    }
}
