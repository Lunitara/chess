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
import websocket.messages.ServerMessage;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConcurrentHashMap<Session, playerInfo> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Session> observers = new ConcurrentHashMap<>();

    private GameService gameService;
    private AuthService authService;
    private UserService userService;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;
    public WebSocketHandler(GameService gameService, AuthService authService, UserService userService, GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
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
        System.out.println("Succesffuly conencted to game through websocket");
        try {
            String username = authDAO.getAuth(action.getAuthToken()).username();
            GameData data = gameDAO.getGame(action.getGameID());
            if (Objects.equals(data.whiteUsername(), username)) {
                connections.put(session, new playerInfo(true, action.getGameID()));
            }
            else {
                connections.put(session, new playerInfo(false, action.getGameID()));

            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return;


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
    record playerInfo(boolean playerIsWhite, int gameID) {
    }
}