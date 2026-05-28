package sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SqlGameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlGameDAOTests {

    private SqlGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new SqlGameDAO();
        gameDAO.clearGameData();
    }
    @Test
    void positiveTestCreateGame()  throws DataAccessException{
        //passes
        ChessGame testingGame = new ChessGame();
        GameData testGame = new GameData(1234, null,null,"testGameName",testingGame);
        gameDAO.createGame(testGame);

    }

    @Test
    void positiveTestGetGame()  throws DataAccessException{
        //passes
        ChessGame testingGame = new ChessGame();
        GameData testGame = new GameData(1234, null,null,"testGameName",testingGame);
        gameDAO.createGame(testGame);
        gameDAO.getGame(1234);

    }
    @Test
    void positiveListGame()  throws DataAccessException{
        //passes
        ChessGame testingGame = new ChessGame();
        GameData testGame = new GameData(1234, null,null,"testGameName",testingGame);
        gameDAO.createGame(testGame);
        assertEquals(1, gameDAO.listGames().size());
    }
    @Test
    void positiveUpdateGame()  throws DataAccessException{
        //passes
        ChessGame testingGame = new ChessGame();
        GameData testGame = new GameData(1234, null,null,"testGameName",testingGame);
        gameDAO.createGame(testGame);
        gameDAO.updateGame(testGame);
    }
    @Test
    void negativeListGame()  throws DataAccessException{
        //fails because there should only be 1
        ChessGame testingGame = new ChessGame();
        GameData testGame = new GameData(1234, null,null,"testGameName",testingGame);
        gameDAO.createGame(testGame);
        assertEquals(2, gameDAO.listGames().size());

    }

    @Test
    void negativeTestCreateGame()  throws DataAccessException{
        //already exists
        ChessGame testingGame = new ChessGame();
        GameData testGame = new GameData(1234, null,null,"testGameName2",testingGame);
        gameDAO.createGame(testGame);
        ChessGame testingGame2 = new ChessGame();
        GameData testGame2 = new GameData(1234, null,null,"testGameName2",testingGame2);
        assertThrows(IllegalArgumentException.class, () -> {
            gameDAO.createGame(testGame2);
        });
    }
    @Test
    void negativeTestGetGame()  throws DataAccessException{
        //passes
        ChessGame testingGame = new ChessGame();
        GameData testGame = new GameData(1234, null,null,"testGameName2",testingGame);
        gameDAO.createGame(testGame);
        assertThrows(IllegalArgumentException.class, () -> {
            gameDAO.getGame(1234);        });
    }

    @Test
    void negativeUpdateGame()  throws DataAccessException{
        //fails because game should not go be created
        ChessGame testingGame = new ChessGame();
        GameData testGame = new GameData(12, null,null,"",testingGame);
        gameDAO.createGame(testGame);
        gameDAO.updateGame(testGame);
    }

    @Test
    void testClear()  throws DataAccessException {
        ChessGame testingGame = new ChessGame();

        GameData testGame = new GameData(12, null,null,"testingGame",testingGame);
        gameDAO.createGame(testGame);
        gameDAO.clearGameData();
    }
}
