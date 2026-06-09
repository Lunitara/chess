package client;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;

import java.util.*;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    public static String visitorName = null;
    public String authToken;
    public Map<Integer, Integer> gameNumberTOGameID= new HashMap<>();

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
        } else {
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
                case "play" -> joinGame(tokens);
                case "observe" -> observeGame(tokens);
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
                list -- lists game options
                play -- <game ID number> [WHITE|BLACK]
                observe -- <game ID number>
                """;
    }
    //PRE-LOGIN UI
    public String login(String... params) {
        if (state == State.SIGNEDIN) {
            return "User already logged in.\n";
        }
        try {
            if (params.length == 3) {
                String username = params[1];
                String password = params[2];
                AuthResult result = server.login(username, password);
                this.visitorName = username;
                state = State.SIGNEDIN;
                this.authToken = result.authToken();
                return String.format("You signed in as %s", username + "\n");
            } else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length + ".\n");
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
                return String.format("Successfully registered as %s.", username + "\n");
            } else {
                return "Please put in the correct # of parameters. You put in " + params.length + "\n";
            }
        } catch (
                Throwable e) {
            return "Error: user already exists.\n";
        }
    }
// POST LOGIN UI
    public String logout() {
        try {
            assertSignedIn();

            server.logout(visitorName, this.authToken);
            state = State.SIGNEDOUT;
            this.authToken = null;
            return "Successfully logged out.\n";
        } catch (Exception e) {
            return String.format("Failed to logout" + e.getMessage() + "\n");
        }
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
                        return "Game name must be unique.\n";
                    }
                }
                CreateGameResult result = server.create(this.authToken, gameName);
                return String.format("Successfully created game %s", gameName + "\n");
            } else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length + "\n");
            }
        } catch (Throwable e) {
            return "Could not create game";
        }
    }
    public String listGames() {
        if (!assertSignedIn()) {
            return "";
        }
        try {
            ListGamesResult result = server.listGames(this.authToken);
            Collection<GameData> games = result.games();
            if (games == null || games.isEmpty()) {
                return "No games to show.\n";
            }
            var resultingString = new StringBuilder();
            resultingString.append("Current games:\n");
            int gameNumber = 1;
            gameNumberTOGameID.clear();

            for (GameData game : games) {
                gameNumberTOGameID.put(gameNumber, game.gameID());
                String blackTaken = "empty";
                String whiteTaken = "empty";
                if (game.whiteUsername() != null) {
                    whiteTaken = game.whiteUsername();
                }
                if (game.blackUsername() != null) {
                    blackTaken = game.blackUsername();
                }
                resultingString.append(String.format("Game Number: %s || Game name: %s || WHITE %s || BLACK %s",
                        gameNumber, game.gameName(), whiteTaken, blackTaken + "\n"));
                gameNumber++;
            }
            return resultingString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private String joinGame(String... params) {
        if (!assertSignedIn()) {
            return "";
        }
        try {
            if (params.length == 3) {
                String playerColor = params[2];
                int gameNumber = Integer.parseInt(params[1]);
                int gameID = gameNumberTOGameID.get(gameNumber);
                ListGamesResult serverGames = server.listGames(this.authToken);
                Collection<GameData> allGames = serverGames.games();
                GameData gameToJoin = null;
                for (GameData game : allGames) {
                    if (game.gameID() == (gameID)) {
                        gameToJoin = game;
                    }
                }
                if (gameToJoin == null) {
                    return "Game does not exist.\n";
                }
                ChessBoard currentBoard = gameToJoin.game().getBoard();
                if (gameToJoin.whiteUsername() != null && Objects.equals(playerColor, "WHITE")) {
                    if (gameToJoin.whiteUsername().equals(visitorName)) {
                        System.out.printf("Successfully joined game %s%n", gameToJoin.gameName());
                        Websocket.joinGame(playerColor, gameID, this.authToken, this);
                        return "";
                    }
                    else {
                        return "White is already being used.\n";

                    }
                }
                if (gameToJoin.blackUsername() != null && Objects.equals(playerColor, "BLACK")) {
                    if (gameToJoin.blackUsername().equals(visitorName)) {
                        System.out.printf("Successfully joined game %s%n", gameToJoin.gameName() );
                        Websocket.joinGame(playerColor, gameID, this.authToken, this);
                        return "";
                    }
                    else {
                        return "Black is already being used.\n";

                    }
                }
                if (Objects.equals(playerColor, "WHITE")) {
                    gameToJoin = new GameData(gameToJoin.gameID(), visitorName, gameToJoin.blackUsername(), gameToJoin.gameName(), gameToJoin.game());
                }
                if (Objects.equals(playerColor, "BLACK")) {
                    gameToJoin = new GameData(gameToJoin.gameID(), gameToJoin.whiteUsername(), visitorName, gameToJoin.gameName(), gameToJoin.game());
                }
                server.joinGame(playerColor, gameID, this.authToken);
                System.out.printf("Successfully joined game %s%n", gameToJoin.gameName());
                Websocket.joinGame(playerColor, gameID, this.authToken, this);
                return "";
            } else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length + "\n");
            }
        } catch (Throwable e) {
            return "Error: could not play game. Make sure it is typed in the correct format.\n";
        }

    }
    public String observeGame(String... params) {
        if (!assertSignedIn()) {
            return "";
        }
        try {
            if (params.length == 2) {
                int gameNumber = Integer.parseInt(params[1]);
                int gameID = gameNumberTOGameID.get(gameNumber);
                ListGamesResult serverGames = server.listGames(this.authToken);
                Collection<GameData> allGames = serverGames.games();
                GameData gameToJoin = null;
                for (GameData game : allGames) {
                    if (game.gameID() == (gameID)) {
                        gameToJoin = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                        break;
                    }
                }
                if (gameToJoin == null) {
                    return "Game does not exist.\n";
                }
                server.observeGame(gameToJoin.gameID(), this.authToken);
                ChessBoard currentBoard = gameToJoin.game().getBoard();
                System.out.printf("Successfully observing game %s", gameToJoin.gameName() + "\n");
                Websocket.joinGame("OBSERVER", gameID, this.authToken, this);
            } else {
                return String.format("Please put in the correct # of parameters. You put in " + params.length + "\n");
            }
        } catch (Exception e) {
            return "Error: could not observe game.\n";
        }
        return "";
    }

    public String makeBoardPlayerWhite(ChessBoard board) {
        StringBuilder sb = new StringBuilder();
        String black = EscapeSequences.SET_BG_COLOR_SLATE_BLUE;
        String orange = EscapeSequences.SET_BG_COLOR_FROST_BLUE;
        String gray = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String reset = EscapeSequences.RESET_BG_COLOR;
        setUpAlphabet(sb, "WHITE");
        sb.append("\n");
        for (int row = 8; row >= 1; row--) {
            sb.append(gray).append("\u2003").append(row).append("\u2003").append(reset);
            for (int col = 1; col <= 8; col++) {
                if ((row+col) %2 == 0) {
                    sb.append(black);
                }
                    else {
                        sb.append(orange);
                    }

                ChessPosition currentPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPos);
                String pieceSymbol = getPieceSym(piece);
                if (piece == null) {
                    sb.append("\u2003");
                }
                sb.append("\u2003").append(pieceSymbol).append("\u2003").append(reset);

            }
            sb.append(gray).append("\u2003").append(row).append("\u2003").append(reset);
            sb.append("\n");
        }
        setUpAlphabet(sb, "WHITE");
        return sb.toString();
    }
    private String getPieceSym(ChessPiece piece) {
        if (piece == null) {
            return "";
        }
        boolean isWhite = (piece.getTeamColor() == ChessGame.TeamColor.WHITE);
        return switch (piece.getPieceType()) {
            case KING -> isWhite ? "♔" : "♚";
            case QUEEN -> isWhite ? "♕" : "♛";
            case ROOK -> isWhite ? "♖" : "♜";
            case BISHOP -> isWhite ? "♗" : "♝";
            case KNIGHT -> isWhite ? "♘" : "♞";
            case PAWN -> isWhite ? "♙" : "♟";
        };
    }

    public void setUpAlphabet(StringBuilder sb, String color) {
        String gray = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String reset = EscapeSequences.RESET_BG_COLOR;
        if (Objects.equals(color, "BLACK")) {
            sb.append(gray).append("   ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("h ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("g").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("f ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("e").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("d ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("c").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("b ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("a").append("\u2003").append(reset);
            sb.append(gray).append("   ").append("\u2003").append(reset);

        } else {
            sb.append(gray).append("   ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("a ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("b").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("c ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("d").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("e ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("f").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("g ").append("\u2003").append(reset);
            sb.append(gray).append("\u2003").append("h").append("\u2003").append(reset);
            sb.append(gray).append("   ").append("\u2003").append(reset);
        }
    }
    public String makeBoardPlayerBlack(ChessBoard board) {
        StringBuilder sb = new StringBuilder();
        String black = EscapeSequences.SET_BG_COLOR_SLATE_BLUE;
        String orange = EscapeSequences.SET_BG_COLOR_FROST_BLUE;
        String gray = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String reset = EscapeSequences.RESET_BG_COLOR;
        setUpAlphabet(sb, "BLACK");
        sb.append("\n");
        for (int row = 1; row <= 8; row++) {
            sb.append(gray).append("\u2003").append(row).append("\u2003").append(reset);
            for (int col = 8; col >= 1; col--) {
                if ((row+col) % 2 == 0) {
                    sb.append(black);
                }
                    else {
                        sb.append(orange);
                    }

                ChessPosition currentPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPos);
                String pieceSymbol = getPieceSym(piece);
                if (piece == null) {
                    sb.append("\u2003");
                }
                sb.append("\u2003").append(pieceSymbol).append("\u2003").append(reset);

            }
            sb.append(gray).append("\u2003").append(row).append("\u2003").append(reset);
            sb.append("\n");
        }
        setUpAlphabet(sb, "BLACK");
        return sb.toString();
    }

    private boolean assertSignedIn() {
        if (this.state == State.SIGNEDOUT) {
            System.out.print("Error: could not fulfill request as" +
                    " user is currently logged out" + "\n");
            return false;
        }
        return true;
    }
}