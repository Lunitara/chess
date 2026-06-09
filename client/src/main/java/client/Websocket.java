package client;

import chess.ChessBoard;
import chess.ChessGame;
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
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Websocket extends Endpoint {
    private Session session;
    public String playerColor;
    private int gameID;
    private String authToken;
    private final ChessClient client;
    private ChessBoard latestBoard = null;

    public static void joinGame(String playerColor, int gameID, String authToken, ChessClient clientInst){

        try {
            Websocket client = new Websocket(clientInst, playerColor, gameID, authToken);
            client.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
            Scanner scanner = new Scanner(System.in);
            String username = ChessClient.visitorName;
            while (true) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    System.out.printf(username + " >>> ");
                    continue;
                }
                String[] tokens = line.split(" ");
                String cmd = (tokens.length > 0) ? tokens[0] : "help";

                if (Objects.equals(playerColor, "OBSERVER")) {
                    switch (cmd) {
                        case "help" -> {
                            help(playerColor);
                        System.out.printf(username + " >>> ");
                        }
                        case "redraw" -> {
                            client.redrawBoard();
                        System.out.printf(username + " >>> ");
                        }
                        case "leave" -> {
                            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
                            client.send(command);
                            System.out.println("Left the game.");
                            return;
                        }
                        case "highlight" -> highlightMoves(tokens);
                        default -> System.out.println("Not an available command. Please type 'help' for options.");

                    }
                }
                else {
                    switch (cmd) {
                        case "help" -> {
                            help(playerColor);
                            System.out.printf(username + " >>> ");
                        }
                        case "redraw" -> {
                            client.redrawBoard();
                            System.out.printf(username + " >>> ");
                        }
                        case "leave" -> {
                            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
                            client.send(command);
                            System.out.println("Left the game.");
                            return;
                        }
                        case "move" -> {
                            makeMove(tokens);
                        System.out.printf(username + " >>> ");
                        }
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

private void redrawBoard() {
        try {
            if (this.latestBoard == null) {
                System.out.println("board is null error");
                return;

            }
            if (Objects.equals(playerColor, "WHITE")) {
                System.out.println("\n" + this.client.makeBoardPlayerWhite(this.latestBoard));

            } else {
                System.out.println("\n" + this.client.makeBoardPlayerBlack(this.latestBoard) );
            }
        } catch (Exception e) {
            System.out.println("something went wrong with printing board");

            throw new RuntimeException(e);

        }
}

private void drawBoard(String message) {
        try {
            ServerMessage newMessage = new Gson().fromJson(message, ServerMessage.class);
            String gameInJson = newMessage.notificationString;
            ChessGame currentGame = new Gson().fromJson(gameInJson, ChessGame.class);
            this.latestBoard = currentGame.getBoard();;
            if (Objects.equals(playerColor, "WHITE")) {
                System.out.println("\n" + this.client.makeBoardPlayerWhite(this.latestBoard) );

            } else {
                System.out.println("\n" + this.client.makeBoardPlayerBlack(this.latestBoard));
            }
        } catch (Exception e) {
            System.out.println("something went wrong with printing board");

            throw new RuntimeException(e);


        }
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
        else {
            System.out.print("""
                help -- lists options
                redraw -- redraws current board
                leave -- leaves current game
                move -- <old ChessPiece position> <new ChessPiece position> moves piece to new position
                resign -- forfeits current game
                highlight -- <ChessPiece position> highlights possible move options
                """);
        }

    }
    private static void makeMove(String[] tokens) {
        try {
            if ()
            if (tokens.length == 3) {
                String startPosition = tokens[1];
                String endPosition = tokens[2];
                if (startPosition.length() != 2 || endPosition.length() !=2) {
                    System.out.println("Please put in the right format for moves. Ex: move d4 f7");

                }
                String allPossibleNums = "12345678";
                String allPossibleAlp = "abcdefgh";
                if (!allPossibleAlp.contains(startPosition.charAt(0) + "")) {
                    System.out.println("Please put in a valid alphabet letter");
                }
                else if (!allPossibleNums.contains(startPosition.charAt(1) + "")) {
                    System.out.println("Please put in a valid number");

                }
                else {
                    //probably a valid entry now lets check if it's actually valid

                }
            } else {
                System.out.println("Please put in the right format and # of parameters for moves." +
                        " Ex: move d4 f7");

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        private static void highlightMoves(String[] tokens) {
    }


    public Websocket(ChessClient client, String playerColor, int gameID, String authToken) throws Exception {
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.authToken = authToken;
        this.client = client;
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage newMessage = new Gson().fromJson(message, ServerMessage.class);
                String username = ChessClient.visitorName;
                switch (newMessage.getServerMessageType()) {
                    case LOAD_GAME -> {
                        drawBoard(message);
                        System.out.print("\n" + username + " >>> ");

                    }
                    case NOTIFICATION -> {
                        System.out.print(newMessage.notificationString);
                    System.out.print("\n" + username + " >>> ");
                    }

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
