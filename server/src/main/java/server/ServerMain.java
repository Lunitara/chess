package server;

import chess.*;
import dataaccess.*;

public class ServerMain {

    public static void main(String[] args) throws DataAccessException{
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess server.Server");
    }
}
