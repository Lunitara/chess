package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

import java.util.Objects;
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
    public record CreateGameResult(int gameID) {
    }
    public record JoinGameRequest(String playerColor, int GameID, String authToken) {
    }


    public GameService.CreateGameResult CreateGame(CreateGameRequest createGameRequest) {
        AuthData existingAuth = auths.getAuth(createGameRequest.authToken());
        if (existingAuth == null) {
            throw new IllegalArgumentException("error null");
        }
        GameData gameData = new GameData(0,null,null, createGameRequest.gameName(), new ChessGame());
        int gameID = games.createGame(gameData);
        return new GameService.CreateGameResult(gameID);
    }
    public boolean checkColorAvailibility(GameData gameData, String playerColor) {
        if (gameData.blackUsername() == null && Objects.equals(playerColor, "BLACK") || gameData.whiteUsername() == null && Objects.equals(playerColor, "WHITE")) {
            return true;
        }
        return false;
    }

    public void JoinGame(JoinGameRequest joinGameRequest) {
        model.GameData gameData = games.getGame(joinGameRequest.GameID());
        AuthData existingAuth = auths.getAuth(joinGameRequest.authToken());
        if (existingAuth == null) {
            throw new IllegalArgumentException("error null");
        }
        if (gameData == null) {
            throw new IllegalArgumentException("error null");
        }
        if (checkColorAvailibility(gameData, joinGameRequest.playerColor)) {
            games.updateGame(gameData);
        }
        else {
            throw new IllegalArgumentException("error unauthorized (color not available)");
        }

    }

    public void clearGameData() {
        games.clearGameData();
    }
}
