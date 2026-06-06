package server.Websocket;
import chess.ChessMove;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
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
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> connect(action, ctx.session);
                case MAKE_MOVE -> makeMove(action.getMove(), ctx.session);
                case LEAVE -> leave(action, ctx.session);
                case RESIGN -> resign(action, ctx.session);
            }
        } catch (Exception ex) {
            System.out.println("Failed to connect, make move, leave, or resign");
        }
    }
    private void connect(UserGameCommand action, Session session) {

        System.out.println("Successfully connected to game through websocket");
        try {
            String username = authDAO.getAuth(action.getAuthToken()).username();
            GameData data = gameDAO.getGame(action.getGameID());
            RunningGame runningGame = runningGames.get(action.getGameID());
            websocket.messages.ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            String newMessage = new Gson().toJson(message);
            if (Objects.equals(data.whiteUsername(), username)) {
                connections.put(session, new PlayerInfo(true, action.getGameID()));
                runningGames.put(action.getGameID(), new RunningGame(runningGame.observers,session, runningGame.blackPlayer()));
                message.notificationString = username + " joined the game as white player.";
                newMessage = new Gson().toJson(message);
                try {
                    if (runningGame.blackPlayer != null) {

                        runningGame.blackPlayer.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("sending a message to black player that white player joined failed");

                }
            } else if (Objects.equals(data.blackUsername(), username)) {
                connections.put(session, new PlayerInfo(false, action.getGameID()));
                runningGames.put(action.getGameID(), new RunningGame(runningGame.observers, runningGame.whitePlayer(),session));
                message.notificationString = username + " joined the game as black player.";
                newMessage = new Gson().toJson(message);
                try {
                    if (runningGame.whitePlayer != null) {
                        runningGame.whitePlayer.getRemote().sendString(newMessage);
                    }
                } catch (IOException e) {
                    System.out.println("sending a message to white player that black player joined failed");

                }
            } else {
                connections.put(session, new PlayerInfo(false, action.getGameID()));
                runningGame.observers.add(session);
                message.notificationString = username + " joined the game as an observer";
                newMessage = new Gson().toJson(message);
            }
            for (Session observer:runningGame.observers) {
                try {
                    observer.getRemote().sendString(newMessage);
                } catch (IOException e) {
                    runningGame.observers.remove(observer);
                    System.out.println("observer lost connection.");
                }
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void makeMove(ChessMove move, Session session) {
    }
    private void leave(UserGameCommand action, Session session) {
    }

    private void resign(UserGameCommand action, Session session) {
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