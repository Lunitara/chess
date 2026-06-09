package client;

import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Scanner;

public class Websocket extends Endpoint {
    private Session session;
    public String playerColor;
    private int gameID;
    private String authToken;

    public static void joinGame(String playerColor, int gameID, String authToken){

        try {
            Websocket client = new Websocket(playerColor, gameID, authToken);
            client.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
            Scanner scanner = new Scanner(System.in);
            String username = ChessClient.visitorName;
            while (true) {
                System.out.printf(username + " >>> ");
                String[] tokens = scanner.nextLine().split(" ");
                String cmd = (tokens.length > 0) ? tokens[0] : "help";
                if (Objects.equals(playerColor, "OBSERVER")) {
                    switch (cmd) {
                        case "help" -> help(playerColor);
                        case "redraw" -> redrawBoard();
                        case "leave" -> {
                            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
                            client.send(command);
                            System.out.println("Left the game.");
                            return;
                        }
                        case "highlight" -> highlightMoves(tokens);
                        default -> System.out.println("Not an available command. Please type 'help' for options.\n");

                    }
                }
                else {
                    switch (cmd) {
                        case "help" -> help(playerColor);
                        case "redraw" -> redrawBoard();
                        case "leave" -> {
                            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
                            client.send(command);
                            System.out.println("Left the game.");
                            return;
                        }
                        case "move" -> makeMove(tokens);
                        case "resign" -> {
                            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
                            client.send(command);
                            System.out.println("Resigned the game.");
                            return;
                        }
                        case "highlight" -> highlightMoves(tokens);
                        default -> System.out.println("Not an available command. Please type 'help' for options.");
                    }
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private static void redrawBoard() {
    }
    private static void help(String playerColor) {
        if (Objects.equals(playerColor, "OBSERVER")) {
            System.out.print("""
                    help -- lists options
                    redraw -- redraws current board
                    leave -- leaves current game
                    highlight -- <CHESSPIECE POSITION> highlights possible move options
                    """
            );
        }
        System.out.print("""
                help -- lists options
                redraw -- redraws current board
                leave -- leaves current game
                move -- <old ChessPiece position> <new ChessPiece position> moves piece to new position
                resign -- forfeits current game
                highlight -- <ChessPiece position> highlights possible move options
                """);
    }
    private static void makeMove(String[] tokens) {
    }

    private static void highlightMoves(String[] tokens) {
    }


    public Websocket(String playerColor, int gameID, String authToken) throws Exception {
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.authToken = authToken;
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage newMessage = new Gson().fromJson(message, ServerMessage.class);
                String username = ChessClient.visitorName;
                switch (newMessage.getServerMessageType()) {
                    case NOTIFICATION -> System.out.print(newMessage.notificationString + "\n" + username + " >>> ");
                }
            }
        });
    }

    public void send(UserGameCommand message) throws IOException {
        String newMessage = new Gson().toJson(message);
        session.getBasicRemote().sendText(newMessage);
    }

    // This method must be overridden, but we don't have to do anything with it
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
