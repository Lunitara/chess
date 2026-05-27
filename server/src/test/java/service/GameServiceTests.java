package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {
    GameService gameService;

    @BeforeEach
    public void setUp()  throws DataAccessException{
        AuthDAO authDAO = new AuthMemoryDAO();
        GameDAO gameDAO = new GameMemoryDAO();
        UserDAO userDAO = new UserMemoryDAO();
        gameService = new GameService(authDAO, gameDAO, userDAO);
        authDAO.createAuth(new AuthData("banana", "Monkey"));
        authDAO.createAuth(new AuthData("apple", "Horse"));

    }
    @Test
    void positiveTestCheckColorAvailability()  throws DataAccessException{
        //passes
        GameService.CreateGameResult game = gameService.createGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.joinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "banana"));

    }
    @Test
    void positiveTestCreateGame()  throws DataAccessException{
        //passes
        gameService.createGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("banana").games().size());
    }

    @Test
    void positiveTestJoinGame()  throws DataAccessException{
        //passes the join game
        GameService.CreateGameResult game = gameService.createGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.joinGame(new GameService.JoinGameRequest("BLACK", game.gameID(), "banana"));
    }
    @Test
    void positiveTestListGames()  throws DataAccessException{
        //passes
        gameService.createGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.createGame(new GameService.CreateGameRequest("apple", "MonkeyWorld"));
        assertEquals(2, gameService.listGames("banana").games().size());

    }
    @Test
    void negativeTestCheckColorAvailability()  throws DataAccessException{
        GameService.CreateGameResult game = gameService.createGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.joinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "banana"));
        //fails if auth is wrong
        assertThrows(IllegalStateException.class, () -> {gameService.joinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "carrot"));});
        //fails if color is already taken
        assertThrows(IllegalAccessError.class, () -> {gameService.joinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "apple"));;});

    }
    @Test
    void negativeTestCreateGame()  throws DataAccessException{
        gameService.createGame(new GameService.CreateGameRequest("apple", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("apple").games().size());
        //fails if auth is wrong
        assertThrows(IllegalArgumentException.class, () -> {gameService.listGames("carrot");});
    }

    @Test
    void negativeTestJoinGame()  throws DataAccessException{
        GameService.CreateGameResult game = gameService.createGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        gameService.joinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "banana"));
        //fails if auth is wrong
        assertThrows(IllegalStateException.class, () -> {gameService.joinGame(new GameService.JoinGameRequest("WHITE", 123, "carrot"));});
        //fails if color is already taken
        assertThrows(IllegalAccessError.class, () -> {gameService.joinGame(new GameService.JoinGameRequest("WHITE", game.gameID(), "apple"));;});
    }
    @Test
    void negativeTestListGames()  throws DataAccessException{
        gameService.createGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
        assertEquals(1, gameService.listGames("banana").games().size());
        //fails if it's different
        assertThrows(IllegalArgumentException.class, () -> {gameService.listGames("carrot");});
    }

        @Test
        void testClear()  throws DataAccessException{
            gameService.createGame(new GameService.CreateGameRequest("banana", "MonkeyWorld"));
            gameService.clearGameData();
            assertEquals(0, gameService.listGames("banana").games().size());
        }
}
