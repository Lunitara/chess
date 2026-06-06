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
import java.util.Scanner;

public class Websocket extends Endpoint {
    private Session session;
    private String playerColor;
    private int gameID;
    private String authToken;

    public static void joinGame(String playerColor, int gameID, String authToken) throws Exception {


        Websocket client = new Websocket(playerColor,gameID,authToken);
        client.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a message you want to echo:");
        while (true) {
            String[] tokens = scanner.nextLine().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            switch (cmd) {
                case "help" -> help();
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(tokens);
                case "resign" -> resign();
                case "highlight" -> highlightMoves(tokens);
                default -> System.out.println("Not an available command. Please type 'help' for options.\n");
            }
        }
    }


    private static void leaveGame() {
    }
    private static void redrawBoard() {
    }
    private static void help() {
    }
    private static void makeMove(String[] tokens) {
    }
    private static void resign() {
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
                switch (newMessage.getServerMessageType()) {
                    case NOTIFICATION -> System.out.println(newMessage.notificationString);
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
