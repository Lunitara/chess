package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import java.util.UUID;
import model.UserData;

public class GameService {
    private UserDAO users;
    private GameDAO games;
    private AuthDAO auths;
    public static String generateGameID() {
        return UUID.randomUUID().toString();
    }
    public GameService(AuthDAO auths, GameDAO games, UserDAO users) {
        this.auths = auths;
        this.games = games;
        this.users = users;
    }
    public record CreateGameRequest(String authToken, String gameName) {
    }
    public record CreateGameResult(int GameID) {
    }
    public GameService.CreateGameResult CreateGame(CreateGameRequest createGameRequest) {
        AuthData existingAuth = auths.getAuth(createGameRequest.authToken());
        if (existingAuth == null) {
            throw new IllegalArgumentException("error null");
        }
        GameData gameData = new GameData(0,"","", createGameRequest.gameName(), new ChessGame());
        int gameID = games.createGame(gameData);
        return new GameService.CreateGameResult(gameID);
    }

    public void clearGameData() {
        games.clearGameData();
    }
}
