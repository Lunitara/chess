package server;

import chess.*;
import dataaccess.*;

public class ServerMain {

    public static void main(String[] args) throws DataAccessException{
        UserDAO userDAO = new SqlUserDAO();
        GameDAO gameDAO = new SqlGameDAO();
        AuthDAO authDAO = new SqlAuthDAO();
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess server.Server");
    }
}
