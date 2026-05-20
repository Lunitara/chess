package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void positiveTestCheckColorAvailability() {
        //passes
        GameService.CreateGameResult game = gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.JoinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "banana"));

    }
    @Test
    void positiveTestCreateGame() {
        //passes
        gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("banana").games().size());
    }

    @Test
    void positiveTestJoinGame() {
        //passes
        GameService.CreateGameResult game = gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.JoinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "banana"));
    }
    @Test
    void positiveTestListGames() {
        //passes
        gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("banana").games().size());

    }
    @Test
    void negativeTestCheckColorAvailability() {
        GameService.CreateGameResult game = gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.JoinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "banana"));
        //fails if auth is wrong
        assertThrows(IllegalStateException.class, () -> {gameService.JoinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "carrot"));});
        //fails if color is already taken
        assertThrows(IllegalAccessError.class, () -> {gameService.JoinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "apple"));;});

    }
    @Test
    void negativeTestCreateGame() {
        gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("banana").games().size());
        //fails if auth is wrong
        assertThrows(IllegalArgumentException.class, () -> {gameService.listGames("carrot");});
    }

    @Test
    void negativeTestJoinGame() {
        GameService.CreateGameResult game = gameService.CreateGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.JoinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "banana"));
        //fails if auth is wrong
        assertThrows(IllegalStateException.class, () -> {gameService.JoinGame(new GameService.JoinGameRequest("WHITE", 123, "carrot"));});
        //fails if color is already taken
        assertThrows(IllegalAccessError.class, () -> {gameService.JoinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "apple"));;});
    }
    @Test
    void negativeTestListGames() {
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
