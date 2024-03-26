package clientTests;

import model.AuthData;
import model.LoginReqData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
    static void stopServer() {
        server.stop();
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
    public void login_ValidCredentials_ShouldReturnAuthData() throws ResponseException {
        // Setup: Assuming a user "testuser" with password "password" is already registered

        // Test: Attempt to login
        LoginReqData loginReqData = new LoginReqData("testuser", "password");
        AuthData authData = facade.login(loginReqData);

        // Verify: Check if the login was successful and AuthData is valid
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("testuser", authData.username());
    }

}
