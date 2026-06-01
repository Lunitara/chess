package client;

import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {
    private static Server serverFacade;
    private static Server server;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(3306);
        System.out.println("Started test HTTP server on " + port);
    }

    //need to clear before each
    @BeforeEach
    public void setUp() {
        //serverFacade.clear();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    //below register test was an example test of what they should look like
    @Test
    void register() throws Exception {
        //var authData = facade.register("player1", "password", "p1@email.com");
       // assertTrue(authData.authToken().length() > 10);
    }
}
