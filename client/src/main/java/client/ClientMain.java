package client;

import chess.ChessGame;
import chess.ChessPiece;
import model.*;

import static java.awt.Color.RED;

public class ClientMain {
    private ServerFacade server;

    void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        try {
            ChessClient client = new ChessClient(serverUrl);
            client.run();
        } catch (Throwable e) {
            System.out.print(RED + "Error: could not create game.\n");
        }
    }
}
