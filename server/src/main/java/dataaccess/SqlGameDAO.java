package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import passoff.exception.ResponseParseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlGameDAO implements GameDAO {
    public SqlGameDAO() throws DataAccessException {
    }


    public int createGame(GameData gamedata) throws DataAccessException {
        var statement = "INSERT INTO gamedata (gameID, whiteUsername, " +
                "blackUsername, gameName, game) VALUES (?,?, ?, ?, ?)";
        String gameJson = new Gson().toJson(gamedata.game());
        DatabaseManager.executeUpdate(statement, gamedata.gameID(), gamedata.whiteUsername(),
                gamedata.blackUsername(), gamedata.gameName(), gameJson);
        return gamedata.gameID();
    }

    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gameList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName," +
                    " game FROM gamedata";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gameList.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
        return gameList;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, " +
                    "game FROM gamedata WHERE gameID = ?";
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
    public void updateGame(GameData gameData) throws DataAccessException {
        var statement = "UPDATE gamedata SET whiteUsername =?, blackUsername =?, " +
                "gameName =?, game =? WHERE gameID =? ";
        String gameJson = new Gson().toJson(gameData.game());

        DatabaseManager.executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), gameJson, gameData.gameID());
    }

    public void clearGameData() throws DataAccessException {
        var statement = "DELETE FROM gamedata";
        DatabaseManager.executeUpdate(statement);

    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        //change game to a ChessGame type
        String gameJson = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

}
