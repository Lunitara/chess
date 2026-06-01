package client;
import client.ServerFacade;
import java.util.Arrays;
import java.util.Scanner;
import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import model.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;

import static java.awt.Color.*;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String visitorName = null;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }


    public void run() {
        System.out.println("Welcome to CS 240 Stander Chess!");
        System.out.println("Type 'help' for a list of commands.");

        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                System.out.print("Error: " + e.getMessage());
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        if (state == State.SIGNEDOUT) {
            System.out.print("Chess 240 User >>> ");

        }
        else {
            System.out.print(visitorName + " >>> ");

        }
    }


    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            return switch (cmd) {
                case "quit" -> "quit";
                case "help" -> help();
                case "login" -> login(tokens);
                case "register" -> register(tokens);
                case "logout" -> logout();
                case "createGame" -> createGame(tokens);
                //case "listGames" -> listGames();
                case "playGame" -> playGame();
                case "observeGame" -> observeGame();
                default -> "Not an available command. Please type 'help' for options.";
            };
        } catch (DataProcessingException ex) {
            return ex.getMessage();
        }
    }

    //HELP UI


    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    quit -- exits program
                    help -- lists options
                    login -- <USERNAME> <PASSWORD>
                    register -- <USERNAME> <PASSWORD> <EMAIL>
                    """;
        }
        return """
                quit -- exits program
                help -- lists options
                logout -- logs out of current session
                createGame -- <GameName> creates a new game
                listGames lists game options
                playGame -- <game ID number> [WHITE|BLACK]
                observeGame <game ID number>
                quit
                """;
    }


    //PRE-LOGIN UI
    public String login(String... params) {
        try {
            if (params.length == 3) {
                String username = params[1];
                String password = params[2];
                AuthResult result = server.login(username, password);
                this.visitorName = username;
                state = State.SIGNEDIN;
                return String.format("You signed in as %s.", username + "\n");
            }
            else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length+ ".\n");

            }

        } catch (Throwable e) {
            return "Error: cannot login " + e.getMessage() + "\n";
        }
    }

    public String register(String... params) {
        try {
            if (params.length == 4) {
                String username = params[1];
                String password = params[2];
                String email = params[3];
                AuthResult result = server.register(username, password, email);
                state = State.SIGNEDIN;
                return String.format("Successfully registered as %s.", username  + "\n");
            }
            else {
                return "Please put in the correct # of parameters. You put in " + params.length + "\n";

            }

        } catch (
                Throwable e) {
            System.out.print("Error: could not register " + e.getMessage() + "\n");
        }
        return "";
    }


// POST LOGIN UI

    public String logout() {
        assertSignedIn();
        visitorName = null;
        state = State.SIGNEDOUT;
        return String.format("%s Successfully logged out ", visitorName);
    }

    public String createGame(String... params) {
        assertSignedIn();
        try {
            if (params.length == 2) {
                int gameName = Integer.parseInt(params[1]);
                return String.format("%s Successfully created game ", gameName  + "\n");

            }
            else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length + "\n");

            }

        } catch (Throwable e) {
            System.out.print("Error: could not create game " + e.getMessage() + "\n");
        }
        return "";
    }

    /*

    public String listGames() {
        assertSignedIn();
        GameData[] games = client.ServerFacade.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (GameData game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

     */

    private String playGame() {
        assertSignedIn();
        return null;
    }

    public String observeGame() {
        assertSignedIn();
        return null;
    }


    private void assertSignedIn() {
        if (state == State.SIGNEDOUT) {
            System.out.print("Error: could not fulfill request as" +
                    " user is currently logged out"  + "\n");
        }
    }
}


