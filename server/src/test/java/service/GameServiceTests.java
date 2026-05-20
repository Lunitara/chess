package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class GameServiceTests {
    public GameService.CreateGameResult CreateGame(GameService.CreateGameRequest createGameRequest) {
        AuthData existingAuth = auths.getAuth(createGameRequest.authToken());
        if (existingAuth == null) {
            throw new IllegalArgumentException("error null");
        }
        GameData gameData = new GameData(0,null,null, createGameRequest.gameName(), new ChessGame());
        int gameID = games.createGame(gameData);
        return new GameService.CreateGameResult(gameID);
    }
    public boolean checkColorAvailability(GameData gameData, String playerColor) {
        if (gameData.blackUsername() == null && Objects.equals(playerColor, "BLACK") || gameData.whiteUsername() == null && Objects.equals(playerColor, "WHITE")) {
            return true;
        }
        return false;
    }

    public void JoinGame(GameService.JoinGameRequest joinGameRequest) {
        model.GameData gameData = games.getGame(joinGameRequest.gameID());
        AuthData existingAuth = auths.getAuth(joinGameRequest.authToken());
        if (existingAuth == null) {
            throw new IllegalStateException("error null auth");
        }
        if (gameData == null) {
            throw new IllegalArgumentException("error null game");
        }
        if (!Objects.equals(joinGameRequest.playerColor, "WHITE") && !Objects.equals(joinGameRequest.playerColor, "BLACK")) {
            throw new IllegalCallerException("error unauthorized (color not available)");
        }
        if (checkColorAvailability(gameData, joinGameRequest.playerColor)) {

            if (Objects.equals(joinGameRequest.playerColor, "BLACK")) {
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),existingAuth.username(),gameData.gameName(),gameData.game());
            }
            if (Objects.equals(joinGameRequest.playerColor, "WHITE")) {
                gameData = new GameData(gameData.gameID(), existingAuth.username(),gameData.blackUsername(),gameData.gameName(),gameData.game());
            }
            games.updateGame(gameData);
        }
        else {
            throw new IllegalAccessError("error unauthorized (color not available)");
        }

    }
    public GameService.ListGamesResult listGames(String authToken) {
        if (auths.getAuth(authToken) == null) {
            throw new IllegalArgumentException("error unauthorized (color not available)");
        }
        return new GameService.ListGamesResult(games.listGames());
    }
    public void clearGameData() {
        games.clearGameData();
    }
        public void setUp() {
            AuthDAO authDAO = new AuthDAO();
            GameDAO gameDAO = new GameDAO();
            UserDAO userDAO = new UserDAO();
            GameService gameService = new GameService(authDAO, gameDAO, userDAO);
            authDAO.createAuth(new AuthData("banana", "Monkey"));
        }
        @Test
        void testClear() {

;
            gameService.CreateGame(new GameService.CreateGameRequest())
            gameService.clearGameData();

        }
}
