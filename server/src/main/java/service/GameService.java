package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class GameService {
    private UserDAO users;
    private GameDAO games;
    private AuthDAO auths;
    public void clearGameData() {
        games.clearGameData();
    }
}
