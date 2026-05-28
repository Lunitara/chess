package dataaccess;

import model.*;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlUserDAO implements UserDAO {
    public SqlUserDAO() throws DataAccessException {
    }


    public void createUser(UserData userdata) throws DataAccessException {
        var statement = "INSERT INTO userdata (username, password, email) VALUES (?,?,?)";
        DatabaseManager.executeUpdate(statement, userdata.username(), userdata.password(), userdata.email());
    }


    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM userdata WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("error failed to get connection", ex);
        }
        return null;
    }

    public void clearUserData() throws DataAccessException {
        var statement = "DELETE FROM userdata";
        DatabaseManager.executeUpdate(statement);

    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }


}
