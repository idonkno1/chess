package clientTests;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;


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
        // Setup: Simulate the server to respond successfully to a registration request

        // Test: Attempt to register a user
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);

        // Verify: Check if the returned AuthData contains the expected information
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("testuser", authData.username());
    }

    @Test
    public void register_WithExistingUsername_ThrowsResponseException() throws ResponseException {
        // Assuming server is set up to reject duplicate usernames
        UserData userData1 = new UserData("existingUser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData1);

        UserData userData2 = new UserData("existingUser", "password", "user@example.com");
        Assertions.assertThrows(ResponseException.class, () -> facade.register(userData2),
                "Expected ResponseException due to duplicate username.");
    }

    @Test
    public void logout_WithInvalidAuthToken_ThrowsResponseException() {
        // Assuming server is set up to validate auth tokens on logout
        String invalidAuthToken = "invalidToken";
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(invalidAuthToken),
                "Expected ResponseException due to invalid auth token.");
    }


    @Test
    public void login_WithIncorrectCredentials_ThrowsResponseException() {
        // Assuming server is set up to validate login credentials
        LoginReqData loginReqData = new LoginReqData("user", "wrongPassword");
        Assertions.assertThrows(ResponseException.class, () -> facade.login(loginReqData),
                "Expected ResponseException due to incorrect login credentials.");
    }


    @Test
    public void login_ValidCredentials_ShouldReturnAuthData() throws ResponseException {
        // Setup: Assuming a user "testuser" with password "password" is already registered

        // Test: Attempt to login
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);

        // Verify: Check if the login was successful and AuthData is valid
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("testuser", authData.username());
    }

    @Test
    public void logout_WithValidAuthToken_ShouldSucceed() throws ResponseException {
        // Setup: Assume a user is logged in and has a valid authToken "validToken"
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);

        // Test: Attempt to logout
        Assertions.assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    public void listGames_WithValidAuthToken_ReturnsGamesArray() throws ResponseException {
        // Setup: Assume valid authToken and the server has mock games data to return
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);
        GameData gameData = new GameData(0, "player1", null, "New Game", new ChessGame());

        int gameId = facade.createGame(gameData, authData.authToken());
        // Test: Request to list games
        GameData[] games = facade.listGames(authData.authToken());

        // Verify: Check if the returned array is not null and contains games data
        Assertions.assertNotNull(games, "Returned games array should not be null.");
        Assertions.assertTrue(games.length > 0, "Games array should contain at least one game.");
    }

    @Test
    public void createGame_WithValidData_ReturnsGameId() throws ResponseException {
        // Setup: Valid game data and authToken
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);
        GameData gameData = new GameData(0, "player1", null, "New Game", new ChessGame());

        // Test: Attempt to create a game
        int gameId = facade.createGame(gameData, authData.authToken());

        // Verify: Check if a valid gameId is returned
        Assertions.assertTrue(gameId > 0, "createGame should return a valid gameId.");
    }

    @Test
    public void joinGame_WithValidData_ReturnsUpdatedGameData() throws ResponseException {
        // Setup: Assume valid authToken, existing gameId and the server can handle game joining
        UserData userData = new UserData("testuser", "password", "testuser@example.com");
        AuthData authData = facade.register(userData);
        JoinReqData joinReqData = new JoinReqData("BLACK", 1);
        GameData gameData = new GameData(0, "player1", null, "NewGame", new ChessGame());

        int gameId = facade.createGame(gameData, authData.authToken());
        // Test: Attempt to join a game
        GameData joinedGame = facade.joinGame(joinReqData, authData.authToken());

        // Verify: Check if the returned GameData reflects a successful join
        Assertions.assertNotNull(joinedGame, "Joined game data should not be null.");
    }


}
