package dataaccess;

import com.google.gson.Gson;
import model.*;
import passoff.exception.ResponseParseException;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlAuthDAO implements AuthDAO {
    public SqlAuthDAO() throws DataAccessException {
    }


    public void createAuth(AuthData authdata) throws DataAccessException {
        var statement = "INSERT INTO authdata (authToken, username) VALUES (?,?)";
        DatabaseManager.executeUpdate(statement, authdata.authToken(), authdata.username());
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

    public void deleteAuth(AuthData authData) throws DataAccessException {
        var statement = "DELETE FROM authdata WHERE authToken = ?";
        DatabaseManager.executeUpdate(statement, authData.authToken());

    }

    public void clearAuthData() throws DataAccessException {
        var statement = "DELETE FROM authdata";
        DatabaseManager.executeUpdate(statement);

    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");

        return new AuthData(authToken, username);
    }

}
