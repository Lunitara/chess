package server.Websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import service.AuthService;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConcurrentHashMap<Session, PlayerInfo> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, RunningGame> runningGames = new ConcurrentHashMap<>();

    private GameService gameService;
    private AuthService authService;
    private UserService userService;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;
    public boolean isObserver = false;

    public void populate(GameService gameService, AuthService authService, UserService userService, GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameService = gameService;
        this.authService = authService;
        this.userService = userService;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action, ctx.session);
            case MAKE_MOVE -> makeMove(action, action.getMove(), ctx.session);
            case LEAVE -> leave(action, ctx.session);
            case RESIGN -> resign(action, ctx.session);
        }
    }

    private void connect(UserGameCommand action, Session session) {

        System.out.println("Successfully connected to game through websocket");

        try {
            AuthData auth = authDAO.getAuth(action.getAuthToken());
            if (auth == null) {
                ServerMessage noDataMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                noDataMessage.errorMessage = "Error unauthorized";
                session.getRemote().sendString(new Gson().toJson(noDataMessage));
                return;
            }
            String username = authDAO.getAuth(action.getAuthToken()).username();
            GameData data = gameDAO.getGame(action.getGameID());
            try {
                if (data == null) {
                    ServerMessage noDataMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    noDataMessage.errorMessage = "Error game ID doesn't exist";
                    session.getRemote().sendString(new Gson().toJson(noDataMessage));
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            RunningGame runningGame = runningGames.get(action.getGameID());
            if (runningGame == null) {
                runningGame = new RunningGame(new ArrayList<>(), null, null);
            }

            websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            String newMessage = new Gson().toJson(message);
            if (Objects.equals(data.whiteUsername(), username)) {
                connections.put(session, new PlayerInfo(true, action.getGameID()));
                Session blackSession = runningGame.blackPlayer();
                runningGame = new RunningGame(runningGame.observers(), session, blackSession);
                runningGames.put(action.getGameID(), runningGame);
                message.message = "\n" + username + " joined the game as white player.";
                newMessage = new Gson().toJson(message);
                try {
                    if (blackSession != null) {

                        blackSession.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("sending a message to black player that white player joined failed");

                }
            } else if (Objects.equals(data.blackUsername(), username)) {
                connections.put(session, new PlayerInfo(false, action.getGameID()));
                Session whiteSession = runningGame.whitePlayer();
                runningGame = new RunningGame(runningGame.observers(), whiteSession, session);
                runningGames.put(action.getGameID(), runningGame);
                message.message = "\n" + username + " joined the game as black player.";
                newMessage = new Gson().toJson(message);
                try {
                    if (whiteSession != null) {
                        whiteSession.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("sending a message to white player that black player joined failed");

                }
            } else {
                connections.put(session, new PlayerInfo(false, action.getGameID()));
                runningGame.observers.add(session);
                isObserver = true;
                runningGames.put(action.getGameID(), runningGame);
                message.message = "\n" + username + " joined the game as an observer";
                newMessage = new Gson().toJson(message);
                try {
                    if (runningGame.whitePlayer != null) {
                        runningGame.whitePlayer.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("observer failed to send a message to white that an observer joined");

                }
                try {
                    if (runningGame.blackPlayer != null) {

                        runningGame.blackPlayer.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("observer failed to send a message to black that an observer joined");

                }
            }
            List<Session> deadObservers = new ArrayList<>();

            for (Session observer : runningGame.observers) {
                if (observer.equals(session)) {
                    continue;
                }
                try {
                    observer.getRemote().sendString(newMessage);
                } catch (IOException e) {
                    deadObservers.add(observer);
                    System.out.println("observer lost connection.");
                }
            }
            runningGame.observers().removeAll(deadObservers);
            ServerMessage loadMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadMessage.game = data.game();
            String actualJsonInfo = new Gson().toJson(loadMessage);
            try {
                session.getRemote().sendString(actualJsonInfo);
                System.out.println("successfully send the load message info");
            } catch (IOException e) {
                System.out.println("did NOT successfully send the load message info");

                throw new RuntimeException(e);
            }


        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void makeMove(UserGameCommand action, ChessMove move, Session session) {
        try {
            AuthData auth = authDAO.getAuth(action.getAuthToken());
            if (auth == null) {
                websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                message.errorMessage = "Error: unauthorized to make move";
                session.getRemote().sendString(new Gson().toJson(message));
                return;
            }
            GameData data = gameDAO.getGame(action.getGameID());
            RunningGame runningGame = runningGames.get(action.getGameID());

            if (data.game().isGameOver()) {
                websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                message.errorMessage = "\nCannot make a move as game is over";
                session.getRemote().sendString(new Gson().toJson(message));
                return;
            }
            ChessGame.TeamColor pieceColor =data.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
            if (data.game().getTeamTurn() !=  pieceColor ||
            pieceColor == ChessGame.TeamColor.WHITE && !session.equals(runningGame.whitePlayer) ||
            pieceColor == ChessGame.TeamColor.BLACK && !session.equals(runningGame.blackPlayer)) {
                websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                message.errorMessage = "\nCannot make a move on opponent's turn or using opponent's pieces";
                session.getRemote().sendString(new Gson().toJson(message));
                return;
            }
            try {
                data.game().makeMove(move);
                gameDAO.updateGame(data);
                websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                if (session.equals(runningGame.whitePlayer)) {
                    message.message = "\nWhite player moved piece.";
                }
                else {
                    message.message = "\nBlack player moved piece.";
                }
                String gameOverJson = null;
                if (data.game().isGameOver()) {
                    websocket.messages.ServerMessage gameOverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                    gameOverMessage.message = "CHECKMATE! Game over";
                    gameOverJson = new Gson().toJson(gameOverMessage);
                }
                String notificationJson = new Gson().toJson(message);
                ServerMessage chessMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                chessMessage.game = data.game();
                chessMessage.lastMove = move;
                if (runningGame.whitePlayer != null) {
                    runningGame.whitePlayer.getRemote().sendString(new Gson().toJson(chessMessage));
                    if (!session.equals(runningGame.whitePlayer)) {
                        runningGame.whitePlayer.getRemote().sendString(notificationJson);
                    }
                    if (gameOverJson != null) {
                        runningGame.whitePlayer.getRemote().sendString(gameOverJson);
                    }
                }
                if (runningGame.blackPlayer != null) {
                    runningGame.blackPlayer.getRemote().sendString(new Gson().toJson(chessMessage));
                    if (!session.equals(runningGame.blackPlayer)) {
                        runningGame.blackPlayer.getRemote().sendString(notificationJson);
                    }
                    if (gameOverJson != null) {
                        runningGame.blackPlayer.getRemote().sendString(gameOverJson);
                    }
                }
                for (Session observer : runningGame.observers) {
                    observer.getRemote().sendString(new Gson().toJson(chessMessage));
                    if (!session.equals(observer)) {
                        observer.getRemote().sendString(notificationJson);
                    }
                    if (gameOverJson != null) {
                        observer.getRemote().sendString(gameOverJson);
                    }
                }
            } catch (InvalidMoveException e) {
                websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                message.errorMessage = "\nIllegal move. Please type in a valid move.";
                session.getRemote().sendString(new Gson().toJson(message));
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void leave(UserGameCommand action, Session session) {
        System.out.println("Successfully disconnected to game through websocket");

        try {
            String username = authDAO.getAuth(action.getAuthToken()).username();
            GameData data = gameDAO.getGame(action.getGameID());
            RunningGame runningGame = runningGames.get(action.getGameID());
            if (runningGame == null) {
                return;
            }
            connections.remove(session);
            websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            String newMessage = "";
            if (Objects.equals(data.whiteUsername(), username)) {
                message.message = "\n" + username + " exited the game as white player.";
                newMessage = new Gson().toJson(message);
                runningGame = new RunningGame(runningGame.observers(), null, runningGame.blackPlayer());
                try {
                    if (runningGame.blackPlayer != null) {

                        runningGame.blackPlayer.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("sending a message to black player that white player left failed");

                }
            } else if (Objects.equals(data.blackUsername(), username)) {
                message.message = "\n" + username + " left the game as black player.";
                newMessage = new Gson().toJson(message);
                runningGame = new RunningGame(runningGame.observers(), runningGame.whitePlayer(), null);

                try {
                    if (runningGame.whitePlayer != null) {
                        runningGame.whitePlayer.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("sending a message to white player that black player left failed");

                }
            } else {
                message.message = "\n" + username + " left the game as an observer";
                newMessage = new Gson().toJson(message);
                runningGame.observers().remove(session);
                try {
                    if (runningGame.whitePlayer != null) {
                        runningGame.whitePlayer.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("observer failed to send a message to white that an observer left");

                }
                try {
                    if (runningGame.blackPlayer != null) {

                        runningGame.blackPlayer.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("observer failed to send a message to black that an observer left");

                }
            }
            runningGames.put(action.getGameID(), runningGame);
            List<Session> deadObservers = new ArrayList<>();
            for (Session observer : runningGame.observers) {
                try {
                    observer.getRemote().sendString(newMessage);
                } catch (IOException e) {
                    deadObservers.add(observer);
                    System.out.println("observer lost connection.");
                }
            }
            runningGame.observers().removeAll(deadObservers);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(UserGameCommand action, Session session) {
        System.out.println("Successfully resigned from game through websocket");

        try {

            GameData data = gameDAO.getGame(action.getGameID());
            AuthData auth = authDAO.getAuth(action.getAuthToken());
            if (auth == null) {
                websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                message.errorMessage = "Error: unauthorized to make move";
                session.getRemote().sendString(new Gson().toJson(message));
                return;
            }
            String username = auth.username();


            if (data.game().isGameOver()) {
                websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                message.errorMessage = "\n" + username + " can't resign as game is already over.";

                try {
                    session.getRemote().sendString(new Gson().toJson(message));


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            } else {
                data.game().resigned(true);
                gameDAO.updateGame(data);
                RunningGame runningGame = runningGames.get(action.getGameID());
                if (runningGame == null) {
                    runningGame = new RunningGame(new ArrayList<>(), null, null);
                }
                websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                String newMessage = new Gson().toJson(message);
                if (Objects.equals(data.whiteUsername(), username)) {
                    message.message = "\n" + username + " resigned from the game as white player.";
                    newMessage = new Gson().toJson(message);
                    try {
                        if (runningGame.whitePlayer != null) {
                            runningGame.whitePlayer.getRemote().sendString(newMessage);
                        }
                        if (runningGame.blackPlayer != null) {
                            runningGame.blackPlayer.getRemote().sendString(newMessage);
                        }
                        for (Session observer: runningGame.observers) {
                            observer.getRemote().sendString(newMessage);
                        }
                    } catch (IOException e) {
                        System.out.println("sending a message to players that white player resigning failed");

                    }
                } else if (Objects.equals(data.blackUsername(), username)) {
                    message.message = "\n" + username + " resigned from the game as black player.";
                    newMessage = new Gson().toJson(message);
                    try {
                        if (runningGame.whitePlayer != null) {
                            runningGame.whitePlayer.getRemote().sendString(newMessage);
                        }
                        if (runningGame.blackPlayer != null) {
                            runningGame.blackPlayer.getRemote().sendString(newMessage);
                        }
                        for (Session observer: runningGame.observers) {
                            observer.getRemote().sendString(newMessage);
                        }
                    } catch (IOException e) {
                        System.out.println("sending a message to players that black player resigning failed");

                    }
                } else {
                    websocket.messages.ServerMessage resignError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    resignError.errorMessage = "observers can't resign";
                    session.getRemote().sendString(new Gson().toJson(resignError));
                    return;
                }
            }

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    record RunningGame(List<Session> observers, Session whitePlayer, Session blackPlayer) {

    }

    record PlayerInfo(boolean playerIsWhite, int gameID) {
    }
}