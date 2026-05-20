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
        authDAO.createAuth(new AuthData("apple", "Horse"));

    }
    @Test
    void testCreateGame() {
        //passes
        gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("banana").games().size());
        //fails if auth is wrong
        assertThrows(IllegalArgumentException.class, () -> {gameService.listGames("carrot");});
    }

    @Test
    void testJoinGame() {
        //passes
        gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.JoinGame(new GameService.JoinGameRequest("WHITE", 123, "banana"));
        //fails if auth is wrong
        assertThrows(IllegalArgumentException.class, () -> {gameService.JoinGame(new GameService.JoinGameRequest("WHITE", 123, "carrot"));});
        //fails if color is already taken
        gameService.JoinGame(new GameService.JoinGameRequest("WHITE", 123, "apple"));

    }
    @Test
    void testListGames() {
        //passes
        gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("banana").games().size());
        //fails if auth is wrong
        assertThrows(IllegalArgumentException.class, () -> {gameService.listGames("carrot");});
    }

        @Test
        void testClear() {
            gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
            gameService.clearGameData();
            assertEquals(0, gameService.listGames("banana").games().size());
        }
}
