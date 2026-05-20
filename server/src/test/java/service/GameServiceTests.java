package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {
    GameService gameService;

    @BeforeEach
    public void setUp() {
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();
        UserDAO userDAO = new UserDAO();
        gameService = new GameService(authDAO, gameDAO, userDAO);
        authDAO.createAuth(new AuthData("banana", "Monkey"));
    }
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
    @Test
    void testListGames() {
        //passes
        gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("banana").games().size());
        //fails
        assertThrows(IllegalArgumentException.class, () -> {gameService.listGames("carrot");});
    }

        @Test
        void testClear() {
            gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
            gameService.clearGameData();
            assertEquals(0, gameService.listGames("banana").games().size());
        }
}
