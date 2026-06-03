package client;

import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static ServerFacade serverFacade;
    private static server.Server server;

    @BeforeAll
    public static void init() {
        try {
            // 1. Initialize and start the backend server application
            server = new server.Server();
            int port = server.run(0); // 0 dynamically allocates an open port

            // 2. Point your client facade to that dynamic port
            String serverUrl = "http://localhost:" + port;
            serverFacade = new ServerFacade(serverUrl);

            System.out.println("Test server successfully running on port: " + port);
        } catch (Throwable t) {
            // 💡 THIS WILL CATCH THE REAL CRASH LOG:
            System.err.println("CRITICAL: Server failed to start during @BeforeAll initialization!");
            t.printStackTrace();
        }
    }

    //need to clear before each
    @BeforeEach
    public void setUp() throws Exception {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    //below register test was an example test of what they should look like
    @Test
    void registerPositive() throws Exception {
        var authData = serverFacade.register("player1", "password", "p1@email.com");
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void registerNegative() throws Exception {
        var authData = serverFacade.register("player1", "password", "p1@email.com");
        //cannot register same person twice

        assertThrows(Exception.class, () -> {
            serverFacade.register("player1", "password", "p1@email.com");
        });
    }

    //Login
    @Test
    void loginPositive() throws Exception {
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        serverFacade.login("chap", "password");
    }

    @Test
    void loginNegative() throws Exception {
        //cannot login the same person twice
        assertThrows(Exception.class, () -> {
            serverFacade.login("chap", "password");
        });
    }
    @Test
    void logoutPositive() throws Exception {
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        serverFacade.logout("chap", authToken.authToken());

    }

    @Test
    void logoutNegative() throws Exception {
        //cannot logout if wasn't logged in
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        serverFacade.logout("chap", authToken.authToken());
        assertThrows(Exception.class, () -> {
            serverFacade.logout("chap", authToken.authToken());
        });
    }
    //create
    @Test
    void createPositive() throws Exception {
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        serverFacade.create(authToken.authToken(), "firstGame");

    }

    @Test
    void createNegative() throws Exception {
        //cannot create if name is the same
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        serverFacade.logout("chap", authToken.authToken());
        assertThrows(Exception.class, () -> {
            serverFacade.create(authToken.authToken(), "firstGame");
        });
    }
    //list
    @Test
    void listPositive() throws Exception {
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        serverFacade.create(authToken.authToken(), "thegame");
        client.ListGamesResult games = serverFacade.listGames(authToken.authToken());
        assertTrue(games.games().size() == 1);
    }

    @Test
    void listNegative() throws Exception {
        //cannot checking that it'll be false if the amount is wrong
        var authToken = serverFacade.register("chap", "password", "p1@email.com");

        serverFacade.create(authToken.authToken(), "thegame");
        client.ListGamesResult games = serverFacade.listGames(authToken.authToken());
        assertFalse(games.games().isEmpty());
    }
    @Test
    void playPositive() throws Exception {
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        CreateGameResult game = serverFacade.create(authToken.authToken(), "chap");
        serverFacade.joinGame("WHITE", game.gameID(), authToken.authToken());
    }

    @Test
    void playNegative() throws Exception {
        //cannot checking that it'll be false if the amount is wrong
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        CreateGameResult game = serverFacade.create(authToken.authToken(), "chap");
        serverFacade.joinGame("WHITE", game.gameID(), authToken.authToken());
        var authToken2 = serverFacade.register("second", "password", "p1@email.com");
        assertThrows(Exception.class, () -> {
            serverFacade.joinGame("WHITE", 1, authToken.authToken());
        });

    }
    @Test
    void observePositive() throws Exception {
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        serverFacade.login("chap", authToken.authToken());
        serverFacade.create(authToken.authToken(), "chap");

        serverFacade.observeGame( 1, authToken.authToken());
    }

    @Test
    void observeNegative() throws Exception {
        //cannot checking that it'll be false if the amount is wrong
        var authToken = serverFacade.register("chap", "password", "p1@email.com");
        serverFacade.create(authToken.authToken(), "chap");
        serverFacade.logout("chap", authToken.authToken());

        serverFacade.observeGame( 1, authToken.authToken());

        assertThrows(Exception.class, () -> {
            serverFacade.observeGame( 1, authToken.authToken());
        });

    }
}
