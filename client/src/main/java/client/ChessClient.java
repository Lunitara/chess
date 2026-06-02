package client;
import model.GameData;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;
import com.google.gson.Gson;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String visitorName = null;
    public String authToken;

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
                case "create" -> create(tokens);
                case "list" -> listGames();
                case "play" -> joinGame();
                case "observe" -> observeGame();
                default -> "Not an available command. Please type 'help' for options.\n";
            };
        } catch (DataProcessingException ex) {
            return ex.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                create -- <GameName> creates a new game
                list lists game options
                play -- <game ID number> [WHITE|BLACK]
                observe <game ID number>
                """;
    }


    //PRE-LOGIN UI
    public String login(String... params) {
        try {
            if (params.length == 3) {
                String username = params[1];
                if (Objects.equals(this.visitorName, username)) {
                    return String.format("Already logged in as " + username + ".\n");
                }
                String password = params[2];
                AuthResult result = server.login(username, password);
                this.visitorName = username;
                state = State.SIGNEDIN;
                this.authToken = result.authToken();

                return String.format("You signed in as %s", username + "\n");
            }
            else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length+ ".\n");

            }

        } catch (Throwable e) {
            return "Error: cannot login. Make sure username and password are correct.\n";
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
                this.visitorName = username;
                this.authToken = result.authToken();

                return String.format("Successfully registered as %s.", username  + "\n");
            }
            else {
                return "Please put in the correct # of parameters. You put in " + params.length + "\n";

            }

        } catch (
                Throwable e) {
            return "Error: user already exists. \n";
        }
    }


// POST LOGIN UI

    public String logout() {
        assertSignedIn();
        state = State.SIGNEDOUT;
        this.authToken = null;
        return String.format("Successfully logged out.\n");
    }

    public String create(String... params) {
        if (!assertSignedIn()) {
            return "";
        }
        try {
            if (params.length == 2) {
                String gameName = params[1];
                ListGamesResult serverGames = server.listGames(this.authToken);
                Collection<GameData> allGames = serverGames.games();
                for (GameData game : allGames) {
                    if (game.gameName().equals(gameName)) {
                        return String.format("Game name must be unique.\n");

                    }
                }
                CreateGameResult result = server.create(this.authToken, gameName);
                return String.format("Successfully created game %s", gameName + "\n");

            }
            else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length + "\n");

            }

        } catch (Throwable e) {
            System.out.print("Error: could not create game.\n");
        }
        return "";
    }


    public String listGames() throws Exception {
         if (!assertSignedIn()) {
             return "";
         }
        try {
            ListGamesResult result = server.listGames(this.authToken);
            Collection<GameData> games = result.games();
            if (games == null || games.isEmpty()) {
                return String.format("No games to show.\n");

            }
            var resultingString = new StringBuilder();
            resultingString.append("Current games:\n");
            var gson = new Gson();
            for (GameData game : games) {
                resultingString.append(String.format("Game name: %s || Game ID: %s ", game.gameName(), game.gameID() + "\n"));
        }
            return resultingString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String joinGame(String... params) {
        if (!assertSignedIn()) {
            return "";
        }
        try {
            if (params.length == 3) {
                String gameName = params[1];
                ListGamesResult serverGames = server.listGames(this.authToken);
                Collection<GameData> allGames = serverGames.games();
                boolean gameExists =false;
                for (GameData game : allGames) {
                    if (game.gameName().equals(gameName)) {
                        gameExists = true;
                    }
                }
                if (!gameExists) {
                    return String.format("Game does not exist.\n");

                }
                String playerColor = params[2];
                CreateGameResult result = server.create(this.authToken, gameName);
                JoinGameRequest serverGames2 = server.joinGame(playerColor,result.gameID(), this.authToken);
                return String.format("Successfully created game %s", gameName + "\n");

            }
            else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length + "\n");

            }

        } catch (Throwable e) {
            System.out.print("Error: could not play game.\n");
        }
        return "";
    }

    public String observeGame(String... params) {
        if (!assertSignedIn()) {
            return "";
        }
        return null;
    }


    private boolean assertSignedIn() {
        if (state == State.SIGNEDOUT) {
            System.out.print("Error: could not fulfill request as" +
                    " user is currently logged out"  + "\n");
            return false;
        }
        return true;
    }
}


