package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isValidAuth(String authToken) throws DataAccessException, SQLException {
        String query = "SELECT COUNT(*) FROM auth_tokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public UserData createUser(UserData userData) {
        var sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, userData.username());
            ps.setString(2, userData.password());
            ps.setString(3, userData.email());
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return userData;

    }

    public UserData getUser(String username){
        var sql = "SELECT * FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public GameData createGame(String gameName){
        var sql = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        int generatedId = 0;
        String serializedGame = serializeChessGame(new ChessGame());
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gameName);
            ps.setString(2, serializedGame);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new GameData(generatedId, null, null, gameName, new ChessGame());
    }


    public Collection<GameData> listGames() {
        var sql = "SELECT gameID, gameName, whiteUsername, blackUsername, game FROM games";
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                ChessGame game = deserializeChessGame(rs.getString("game"));
                games.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), game));
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return games;
    }


    public GameData getGame(int gameID) {
        var sql = "SELECT gameID, gameName, whiteUsername, blackUsername, game FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    ChessGame game = deserializeChessGame(rs.getString("game"));
                    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                            rs.getString("blackUsername"), rs.getString("gameName"), game);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public AuthData createAuthToken(String username) {
        var authToken = UUID.randomUUID().toString();
        var sql = "INSERT INTO auth_tokens (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new AuthData(authToken, username);
    }


    public AuthData getAuthToken(String authToken) {
        var sql = "SELECT * FROM auth_tokens WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public void deleteAuthToken(String authToken) {
        var sql = "DELETE FROM auth_tokens WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void clearDAO() {
        var sqls = new String[]{
                "TRUNCATE TABLE users",
                "TRUNCATE TABLE auth_tokens",
                "TRUNCATE TABLE games"
        };
        try (var conn = DatabaseManager.getConnection()) {
            for (var sql : sqls) {
                try (var ps = conn.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateGame(GameData updatedGame) {
        var sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, updatedGame.whiteUsername());
            ps.setString(2, updatedGame.blackUsername());
            ps.setString(3, serializeChessGame(updatedGame.game()));
            ps.setInt(4, updatedGame.gameID());
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public String serializeChessGame(ChessGame game) {
        Gson gson = new Gson();
        return gson.toJson(game);
    }

    public ChessGame deserializeChessGame(String gameJson) {
        Gson gson = new Gson();
        return gson.fromJson(gameJson, ChessGame.class);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
               `username` VARCHAR(256) NOT NULL,
               `password` VARCHAR(256) NOT NULL,
               `email` VARCHAR(256) NOT NULL,
               PRIMARY KEY (`username`)
             )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
               `gameID` INT NOT NULL AUTO_INCREMENT,
               `whiteUsername` VARCHAR(256),
               `blackUsername` VARCHAR(256),
               `gameName` VARCHAR(256) NOT NULL,
               `game` TEXT NOT NULL,
               PRIMARY KEY (`gameID`)
             )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth_tokens (
               `authToken` VARCHAR(256) NOT NULL,
               `username` VARCHAR(256) NOT NULL,
               PRIMARY KEY (`authToken`)
             )
            """,
            """
            ALTER TABLE games AUTO_INCREMENT = 0
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
