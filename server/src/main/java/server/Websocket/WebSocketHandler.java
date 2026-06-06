package server.Websocket;
import chess.ChessMove;
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

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

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

}