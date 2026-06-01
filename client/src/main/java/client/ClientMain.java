package client;

import model.*;

import static java.awt.Color.RED;

public class ClientMain {
    private ServerFacade server;

    void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Client: " + piece);
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        try {
            ChessClient client = new ChessClient(serverUrl);
            client.run();
        } catch (Throwable e) {
            System.out.print(RED + "Error: could not create game" + e.getMessage() + "\n");
        }
    }
}
