package dataaccess;
import com.google.gson.Gson;
import model.*;
import passoff.exception.ResponseParseException;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlAuthDAO implements AuthDAO{
    public SqlAuthDAO() throws DataAccessException {
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
            CREATE TABLE IF NOT EXISTS  authdata (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (authToken),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
    public void createAuth(AuthData authdata) throws DataAccessException{
        var statement = "INSERT INTO authdata (authToken, username) VALUES (?,?)";
        executeUpdate(statement, authdata.authToken(), authdata.username());
    }


    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authdata WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
        return null;
    }
    public void deleteAuth(AuthData authToken) throws DataAccessException{
        var statement = "DELETE FROM authData WHERE authToken";
        executeUpdate(statement);

    }
    public void clearAuthData() throws DataAccessException{
        var statement = "DELETE FROM authData";
        executeUpdate(statement);

    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");

        return new AuthData(authToken,username);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof AuthData p) ps.setString(i + 1, p.toString());
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
