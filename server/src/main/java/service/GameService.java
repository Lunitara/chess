package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class GameService {
    private UserDAO users;
    private GameDAO games;
    private AuthDAO auths;

    public GameService(AuthDAO auths, GameDAO games, UserDAO users) {
        this.auths = auths;
        this.games = games;
        this.users = users;
    }
    public record CreateGameRequest(String authToken) {
    }
    public record CreateGameResult(String GameID) {
    }
    public GameService.CreateGameResult CreateGame(String authToken) {
        String authToken = AuthData.getAuth();
        UserData existingUser = users.getUser(user.username());
        if (existingUser != null) {
            throw new IllegalArgumentException("Already Taken Exception");
        }
        users.createUser(user);
        AuthData authData = new AuthData(user.username(), authToken);
        auths.createAuth(authData);

        return new GameService.createGameResult(user.username(), authToken);
    }

    public void clearGameData() {
        games.clearGameData();
    }
}
