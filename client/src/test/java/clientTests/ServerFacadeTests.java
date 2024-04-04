package clientTests;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import ui.server.ResponseException;
import server.Server;
import ui.server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        server.stop();
    }
    @AfterEach
    void clearServer() throws ResponseException {
        facade.clearData();
    }

    @Test
    public void register_User_ShouldReturnAuthData() throws ResponseException {
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);

        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("testuser", authData.username());
    }

    @Test
    public void register_WithExistingUsername_ThrowsResponseException() throws ResponseException {
        UserData userData1 = new UserData("existingUser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData1);

        UserData userData2 = new UserData("existingUser", "password", "user@example.com");
        Assertions.assertThrows(ResponseException.class, () -> facade.register(userData2),
                "Expected ResponseException due to duplicate username.");
    }

    @Test
    public void logout_WithInvalidAuthToken_ThrowsResponseException() {
        String invalidAuthToken = "invalidToken";
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(invalidAuthToken),
                "Expected ResponseException due to invalid auth token.");
    }

    @Test
    public void login_WithIncorrectCredentials_ThrowsResponseException() {
        LoginReqData loginReqData = new LoginReqData("user", "wrongPassword");
        Assertions.assertThrows(ResponseException.class, () -> facade.login(loginReqData),
                "Expected ResponseException due to incorrect login credentials.");
    }

    @Test
    public void login_ValidCredentials_ShouldReturnAuthData() throws ResponseException {

        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);

        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("testuser", authData.username());
    }

    @Test
    public void logout_WithValidAuthToken_ShouldSucceed() throws ResponseException {
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);

        Assertions.assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    public void listGames_WithValidAuthToken_ReturnsGamesArray() throws ResponseException {
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);
        GameData gameData = new GameData(0, "player1", null, "New Game", new ChessGame());

        int gameId = facade.createGame(gameData, authData.authToken());

        GameData[] games = facade.listGames(authData.authToken());

        Assertions.assertNotNull(games, "Returned games array should not be null.");
        Assertions.assertTrue(games.length > 0, "Games array should contain at least one game.");
    }

    @Test
    public void listGames_WithValidAuthToken_ReturnsIncorrectLengthGamesArray() throws ResponseException {
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);
        GameData gameData = new GameData(0, "player1", null, "New Game", new ChessGame());

        int gameId = facade.createGame(gameData, authData.authToken());
        GameData[] games = facade.listGames(authData.authToken());

        Assertions.assertNotNull(games, "Returned games array should not be null.");
        Assertions.assertFalse(games.length == 0, "Games array should contain at least one game.");
    }

    @Test
    public void createGame_WithValidData_ReturnsGameId() throws ResponseException {
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);
        GameData gameData = new GameData(0, "player1", null, "New Game", new ChessGame());

        int gameId = facade.createGame(gameData, authData.authToken());

        Assertions.assertTrue(gameId > 0, "createGame should return a valid gameId.");
    }

    @Test
    public void createGame_WithInvalidData_ThrowsResponseException() {
        String authToken = "validAuthToken";
        GameData invalidGameData = new GameData(0, null, null, "", new ChessGame()); // Invalid because the game name is empty

        Assertions.assertThrows(ResponseException.class,
                () -> facade.createGame(invalidGameData, authToken),
                "Expected ResponseException due to invalid game data.");
    }

    @Test
    public void joinGame_WithValidData_ReturnsUpdatedGameData() throws ResponseException {
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);
        JoinReqData joinReqData = new JoinReqData("BLACK", 1);
        GameData gameData = new GameData(0, "player1", null, "NewGame", new ChessGame());

        int gameId = facade.createGame(gameData, authData.authToken());

        ChessGame joinedGame = facade.joinGame(joinReqData, authData.authToken());

        Assertions.assertNotNull(joinedGame, "Joined game data should not be null.");
    }

    @Test
    public void joinGame_NonexistentGameOrUnauthorized_ThrowsResponseException() {
        String authToken = "validAuthToken";
        JoinReqData joinReqData = new JoinReqData("WHITE", 999);

        Assertions.assertThrows(ResponseException.class,
                () -> facade.joinGame(joinReqData, authToken),
                "Expected ResponseException due to trying to join a non-existent game or unauthorized action.");
    }


}
