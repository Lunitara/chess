package client;

import chess.*;
import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import ui.EscapeSequences;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static chess.ChessPiece.PieceType.PAWN;

public class Websocket extends Endpoint {
    private Session session;
    public String playerColor;
    private int gameID;
    private String authToken;
    private ChessBoard latestBoard = null;
    private ChessMove lastMove;
    private String userName = null;

    public static void joinGame(String playerColor, int gameID, String authToken) throws Exception {
        Websocket client = new Websocket(playerColor, gameID, authToken);
        client.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        Scanner scanner = new Scanner(System.in);
        String username = ChessClient.visitorName;
        client.userName = ChessClient.visitorName;
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

                    case "leave" -> {
                        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
                        client.send(command);
                        System.out.println("Left the game.");
                        return;
                    }
                    case "highlight" -> {
                        client.highlightMoves(tokens);
                        System.out.printf(username + " >>> ");
                    }
                    case "redraw" -> {
                        Collection<ChessMove> moves = new ArrayList<>();
                        client.redrawBoard(moves);
                        System.out.printf(username + " >>> ");
                    }

                    default -> {
                        System.out.println("Not an available command. Please type 'help' for options.");
                        System.out.printf(username + " >>> ");
                    }

                }
            } else {
                switch (cmd) {
                    case "help" -> {
                        help(playerColor);
                        System.out.printf(username + " >>> ");
                    }

                    case "leave" -> {
                        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
                        client.send(command);
                        System.out.println("Left the game.");
                        return;
                    }
                    case "move" -> {
                        client.makeMove(tokens);
                        System.out.printf(username + " >>> ");

                    }
                    case "redraw" -> {
                        Collection<ChessMove> moves = new ArrayList<>();
                        client.redrawBoard(moves);
                        System.out.printf(username + " >>> ");
                    }
                    case "resign" -> {
                        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
                        client.send(command);
                        System.out.println("Resigned the game.");
                        return;
                    }
                    case "highlight" -> {
                        client.highlightMoves(tokens);
                        System.out.printf(username + " >>> ");
                    }
                    default -> {
                        System.out.println("Not an available command. Please type 'help' for options.");
                        System.out.printf(username + " >>> ");
                    }
                }
            }

        }

    }

    private void redrawBoard(Collection<ChessMove> moves) {
        try {
            if (this.latestBoard == null) {
                System.out.println("board is null error");
                return;

            }

            if (Objects.equals(playerColor, "WHITE") || Objects.equals(playerColor, "OBSERVER")) {
                System.out.println("\n" + makeBoard(this.latestBoard, this.lastMove, moves, "WHITE"));

            } else {
                System.out.println("\n" + makeBoard(this.latestBoard, this.lastMove, moves, "BLACK"));
            }
        } catch (Exception e) {
            System.out.println("something went wrong with printing board");

            throw new RuntimeException(e);

        }
    }


    private String getPieceSym(ChessPiece piece) {
        if (piece == null) {
            return "";
        }
        boolean isWhite = (piece.getTeamColor() == ChessGame.TeamColor.WHITE);

        String pieceChar = switch (piece.getPieceType()) {
            case KING -> "♚";
            case QUEEN -> "♛";
            case ROOK -> "♜";
            case BISHOP -> "♝";
            case KNIGHT -> "♞";
            case PAWN -> "♟";
        };
        String black = EscapeSequences.SET_TEXT_COLOR_BLACK;
        String white = EscapeSequences.SET_TEXT_COLOR_WHITE;
        if (isWhite) {
            return white + pieceChar;
        } else {
            return black + pieceChar + white;
        }
    }

    public void setUpAlphabet(StringBuilder sb, String color) {
        String gray = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String reset = EscapeSequences.RESET_BG_COLOR;
        sb.append(EscapeSequences.SET_TEXT_COLOR_WHITE);
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

    public String makeBoard(ChessBoard board, ChessMove lastMove, Collection<ChessMove> validMoves, String color) {
        StringBuilder sb = new StringBuilder();
        String black = EscapeSequences.SET_BG_COLOR_SLATE_BLUE;
        String orange = EscapeSequences.SET_BG_COLOR_FROST_BLUE;
        String gray = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String reset = EscapeSequences.RESET_BG_COLOR;
        String grossOrange = EscapeSequences.SET_BG_COLOR_PINK_ORANGE;
        String highlightColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        String otherHighlightColor = EscapeSequences.SET_BG_COLOR_YELLOW;
        int startRow;
        int endRow;
        int rowStep;
        int startCol;
        int endCol;
        int colStep;
        if (Objects.equals(color, "BLACK")) {
            setUpAlphabet(sb, "BLACK");
            startRow = 1;
            endRow = 8;
            rowStep = 1;
            startCol = 8;
            endCol = 1;
            colStep = -1;
        } else {
            setUpAlphabet(sb, "WHITE");
            startRow = 8;
            endRow = 1;
            rowStep = -1;
            startCol = 1;
            endCol = 8;
            colStep = 1;
        }
        sb.append("\n");

        for (int row = startRow; rowStep > 0 ? row <= endRow : row >= endRow; row += rowStep) {
            sb.append(gray).append("\u2003").append(row).append("\u2003").append(reset);
            for (int col = startCol; colStep > 0 ? col <= endCol : col >= endCol; col += colStep) {
                boolean colored = false;
                for (ChessMove move : validMoves) {
                    if (row == move.getEndPosition().getRow() && col == move.getEndPosition().getColumn()) {
                        colored = true;
                        break;
                    }
                }
                if (lastMove != null && row == lastMove.getStartPosition().getRow() && col ==
                        lastMove.getStartPosition().getColumn()) {
                    sb.append(grossOrange);
                } else if (lastMove != null && row == lastMove.getEndPosition().getRow() && col ==
                        lastMove.getEndPosition().getColumn()) {
                    sb.append(grossOrange);
                } else if ((row + col) % 2 == 0) {
                    sb.append(colored ? highlightColor : black);

                } else {
                    sb.append(colored ? otherHighlightColor : orange);
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
        if (Objects.equals(color, "BLACK")) {
            setUpAlphabet(sb, "BLACK");
        } else {
            setUpAlphabet(sb, "WHITE");

        }
        return sb.toString();
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
        } else {
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

    private void makeMove(String[] tokens) throws IOException {

        if (tokens.length == 3) {
            String startPosition = tokens[1];
            String endPosition = tokens[2];
            String allPossibleNums = "12345678";
            String allPossibleAlp = "abcdefgh";
            ChessPosition startPos = new ChessPosition(allPossibleNums.indexOf(startPosition.charAt(1)) +
                    1, allPossibleAlp.indexOf(startPosition.charAt(0)) + 1);
            if (startPosition.length() != 2 || endPosition.length() != 2) {
                System.out.println("Please put in the right format for moves. Ex: move d4 f7");
            } else if (!allPossibleAlp.contains(startPosition.charAt(0) + "")) {
                System.out.println("Please put in a valid alphabet letter");
            } else if (!allPossibleNums.contains(startPosition.charAt(1) + "")) {
                System.out.println("Please put in a valid number");

            }
            else if (latestBoard.getPiece(startPos) == null) {
                System.out.println("Illegal move; no piece is there");
            }
            //probably a valid entry now lets check if it's actually valid
            ChessPosition endPos = new ChessPosition(allPossibleNums.indexOf(endPosition.charAt(1)) + 1,
                    allPossibleAlp.indexOf(endPosition.charAt(0)) + 1);

            ChessPiece piece = latestBoard.getPiece(startPos);
            if (((endPos.getRow() == 8 && Objects.equals(this.playerColor, "WHITE")) ||
                    (endPos.getRow() == 1 && Objects.equals(this.playerColor, "BLACK")))
                    && piece.getPieceType() == PAWN) {
                ChessPiece.PieceType promotionPiece = null;
                Scanner tempScanner = new Scanner(System.in);
                System.out.println(("Pawn can promote. Choose from the following options:\n" +
                        "Queen, Rook, Bishop, Knight\n"));
                System.out.println(this.userName + " >> ");
                String choice = tempScanner.nextLine().trim().toUpperCase();
                switch (choice) {
                    case "QUEEN" -> promotionPiece = ChessPiece.PieceType.QUEEN;
                    case "ROOK" -> promotionPiece = ChessPiece.PieceType.ROOK;
                    case "BISHOP" -> promotionPiece = ChessPiece.PieceType.BISHOP;
                    case "KNIGHT" -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                    default -> {
                        System.out.println(("Not an option. Piece promoted to QUEEN\n"));
                        promotionPiece = ChessPiece.PieceType.QUEEN;
                    }
                }
                ChessMove move = new ChessMove(startPos, endPos, promotionPiece);
                UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
                command.setMove(move);
                send(command);


            }
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, this.authToken, this.gameID);
            command.setMove(new ChessMove(startPos, endPos, null));
            send(command);


        } else {
            System.out.println("Please put in the right format and # of parameters for moves." +
                    " Ex: move d4 f7");

        }


    }

    //run highilght that akes the parameter of the piece you want to get the highlihgts for and then do
    //atest board.validmoves with that piece position an that will reutrn the collection of chessmoves
    //and draw readraw board with taht colleciton of chess moves
    //check if the move involves the pawn moving to the last or first row and then ask a follow up question
    //and then set the promotion piece on the move

    private void highlightMoves(String[] tokens) {
        try {

            if (tokens.length == 2) {
                String piecePos = tokens[1];
                String allPossibleNums = "12345678";
                String allPossibleAlp = "abcdefgh";
                ChessPosition finalPiecePos = new ChessPosition(allPossibleNums.indexOf(piecePos.charAt(1)) + 1,
                        allPossibleAlp.indexOf(piecePos.charAt(0)) + 1);
                if (piecePos.length() != 2) {
                    System.out.println("Please put in the right format for highlights. Ex: highlight d4");
                } else if (!allPossibleAlp.contains(piecePos.charAt(0) + "")) {
                    System.out.println("Please put in a valid alphabet letter");
                } else if (!allPossibleNums.contains(piecePos.charAt(1) + "")) {
                    System.out.println("Please put in a valid number");
                } else if (this.latestBoard.getPiece(finalPiecePos) == null) {
                        System.out.println("There is no piece there");
                        return;

                } else {
                    //probably a valid entry now lets check if it's actually valid

                    Collection<ChessMove> validMoves = this.latestBoard.getPiece(finalPiecePos).pieceMoves(this.latestBoard, finalPiecePos);
                    redrawBoard(validMoves);
                }

            } else {
                System.out.println("Please put in the right format and # of parameters for moves." +
                        "Ex: highlight d6");


            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                    case LOAD_GAME -> {
                        latestBoard = newMessage.game.getBoard();
                        lastMove = newMessage.lastMove;
                        Collection<ChessMove> moves = new ArrayList<>();
                        redrawBoard(moves);
                        System.out.print("\n" + username + " >>> ");

                    }
                    case NOTIFICATION -> {
                        System.out.print(newMessage.message);
                        System.out.print("\n" + username + " >>> ");
                    }
                    case ERROR -> {
                        System.out.print(newMessage.errorMessage);
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
