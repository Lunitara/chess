package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import passoff.exception.ResponseParseException;

import java.sql.*;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlGameDAO implements GameDAO {
    public SqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  gamedata (
              'gameID' int NOT NULL,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              'game' ChessGame NOT NULL,
              PRIMARY KEY (gameID),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              INDEX(gameName),
              INDEX(game)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
    public int createGame(GameData gamedata) throws DataAccessException{
        var statement = "INSERT INTO gamedata (gameID, whiteUsername, backUsername, gameName, game) VALUES (?,?, ?, ?, ?)";
        executeUpdate(statement, gamedata.gameID(), gamedata.whiteUsername(), gamedata.blackUsername(), gamedata.gameName(), gamedata.game());
    }
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new PetList();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, backUsername, gameName, game FROM gamedata";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
        return result;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, backUsername, gameName, game FROM gamedata WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
        return null;
    }

    @Override
    public void updateGame(GameData gamedata) {
        var statement = "UPDATE gameID, whiteUsername, backUsername, gameName, game FROM gamedata ";
        executeUpdate(statement);
    }

    public void clearGameData() throws DataAccessException{
        var statement = "DELETE FROM gameData";
        executeUpdate(statement);

    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        //change game to a ChessGame type
        String gameJson = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(gameID,whiteUsername,blackUsername,gameName,game);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();


                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to update database: %s, %s", statement, e.getMessage()));        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
