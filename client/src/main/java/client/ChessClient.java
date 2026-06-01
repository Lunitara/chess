package client;

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
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                System.out.print(RED + "Error: " + e.getMessage());
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        String words = (state == State.SIGNEDOUT) ? "SIGNED_OUT" : "SIGNED_IN";
        System.out.print("\n" + GREEN + words + ">>> " + WHITE);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            return switch (cmd) {
                case "quit" -> "quit";
                case "help" -> "help";
                case "login" -> login();
                case "register" -> register(tokens);
                case "logout" -> logout();
                case "createGame" -> createGame();
                case "listGames" -> listGames();
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
                create game -- <GameName> creates a new game
                list games lists game options
                play game -- <game ID number> [WHITE|BLACK]
                observeGame <game ID number>
                quit
                """;
    }


    //PRELOGIN UI
    public String login(String... params) {
        try {
            if (params.length == 3) {
                String username = params[1];
                this.visitorName = username;
                state = State.SIGNEDIN;
                return String.format("You signed in as %s.", username);
            }

        } catch (Throwable e) {
            System.out.print(RED + "Error: cannot login" + e.getMessage());
        }
        return "";
    }

    public String register(String... params) {
        try {
            if (params.length == 4) {
                String username = params[1];
                return String.format("Successfuly registered as %s.", username);
            }
        } catch (
                Throwable e) {
            System.out.print(RED + "Error: could not register" + e.getMessage());
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
        if (params.length == 1) {

            int id = Integer.parseInt(params[0]);
            Game pet = getGame(id);
            if (pet != null) {
                server.deleteGame(id);
                return String.format("%s says %s", pet.name(), pet.sound());
            }
        }

    } catch (Throwable e) {
        System.out.print(RED + "Error: " + e.getMessage());
    }
}


public String listGames() {
    assertSignedIn();
    GameList pets = server.listGames();
    var result = new StringBuilder();
    var gson = new Gson();
    for (Game pet : pets) {
        result.append(gson.toJson(pet)).append('\n');
    }
    return result.toString();
}

private String playGame() {
    for (Game pet : server.listGames()) {
        if (pet.id() == id) {
            return pet;
        }
    }
    return null;
}

public String ObserveGame() {
    assertSignedIn();
    var buffer = new StringBuilder();
    for (Game pet : server.listGames()) {
        buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
    }

    server.deleteAllGames();
    return buffer.toString();
}

public String observeGame() {
    assertSignedIn();
    GameList pets = server.listGames();
    var result = new StringBuilder();
    var gson = new Gson();
    for (Game pet : pets) {
        result.append(gson.toJson(pet)).append('\n');
    }
    return result.toString();
}


private void assertSignedIn() {
    if (state == State.SIGNEDOUT) {
        throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
    }
}

    }
