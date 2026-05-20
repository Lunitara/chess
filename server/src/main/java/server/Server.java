package server;
import com.google.gson.Gson;
import com.sun.jdi.InvalidStackFrameException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.json.JsonMapper;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.GameService;
import service.AuthService;
import io.javalin.json.JavalinJackson;

import java.io.EOFException;
import java.lang.reflect.Type;
import java.util.Objects;

public class Server {

    private final Javalin javalin;
    private  final UserService users;
    private  final GameService games;
    private  final AuthService auths;
    private final Gson gson = new Gson();


    private  void Clear(@NotNull Context context) {
        users.clearUserData();
        games.clearGameData();
        auths.clearAuthData();
    }
    private  void Register(@NotNull Context context) {
        //context.bodyAsClass parses request body into record class probably
        try {
            UserData user = context.bodyAsClass(UserData.class);
            if (Objects.equals(user.username(), "") || Objects.equals(user.password(), "") || Objects.equals(user.email(), "")) {
                context.status(400).result("{\"message\":\"error cannot have blank catagory\"}");
                return;
            }
            if (Objects.equals(user.username(), null) || Objects.equals(user.password(), null) || Objects.equals(user.email(), null)) {
                context.status(400).result("{\"message\":\"error cannot have blank catagory\"}");
                return;
            }
            try {
                UserService.RegisterResult result = users.register(user);
                context.json(result);
            }
            catch (IllegalArgumentException ex) {
                context.status(403).result("{\"message\":\"error already exists\"}");
                return;
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("Request body should be json");
            return;
        }
    }
    //login
    private  void Login(@NotNull Context context) {
        //context.bodyAsClass parses request body into record class probably
        try {
            UserService.LoginRequest user = context.bodyAsClass(UserService.LoginRequest.class);
            if (Objects.equals(user.username(), "") || Objects.equals(user.password(), "")) {
                context.status(400).result("{\"message\":\"error cannot have blank catagory\"}");
                return;
            }
            if (Objects.equals(user.username(), null) || Objects.equals(user.password(), null)) {
                context.status(400).result("{\"message\":\"error cannot have blank catagory\"}");
                return;
            }
            try {
                UserService.LoginResult result = users.login(user);
                context.json(result);

            }
            catch (IllegalArgumentException ex) {
                context.status(401).result("{\"message\":\"error unauthorized\"}");
                return;
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("Request body should be json");
            return;
        }
    }
    //
    private void listGames(@NotNull Context context) {
        //context.bodyAsClass parses request body into record class probably
        /*
        try {
            GameService.listGames games = context.bodyAsClass(GameService.listGames.class);
            if (Objects.equals(authInfo.AuthToken(), "")) {
                context.status(400).result("{\"message\":\"error authToken is blank\"}");
                return;
            }
            if (Objects.equals(authInfo.AuthToken(), null)) {
                context.status(400).result("{\"message\":\"error authToken is null\"}");
                return;
            }
            try {
                auth

            }
            catch (IllegalArgumentException ex) {
                context.status(401).result("{\"message\":\"error unauthorized\"}");
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("Request body should be json");
            return;
        }

         */
    }
    //
    private  void Logout(@NotNull Context context) {
        //context.bodyAsClass parses request body into record class probably
        try {
            String authToken = getAuthHeader(context);
            if (authToken.equals("")) {
                context.status(400).result("{\"message\":\"error authToken is blank\"}");
                return;
            }

            try {
                users.logout(authToken);

            }
            catch (IllegalArgumentException ex) {
                context.status(401).result("{\"message\":\"error unauthorized\"}");
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("{\"message\":\"error Request body should be json\"}");
            return;
        }
    }
    //
    private  void CreateGame(@NotNull Context context) {
        //context.bodyAsClass parses request body into record class probably

        try {
            GameData game = context.bodyAsClass(GameData.class);
            if (Objects.equals(game.gameName(), "") || Objects.equals(game.gameName(), null)) {
                context.status(400).result("{\"message\":\"error cannot have blank catagory\"}");
                return;
            }
            try {

                GameService.CreateGameResult result = games.CreateGame(new GameService.CreateGameRequest(getAuthHeader(context),game.gameName()));
                context.json(result);
            }
            catch (IllegalArgumentException ex) {
                context.status(401).result("{\"message\":\"error already exists\"}");
                return;
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("Request body should be json");
            return;
        }
    }
    private String getAuthHeader(Context context) {
        //way to get the auth header I guess
        return context.header("Authorization");
    }
    //
    private  void joinGame(@NotNull Context context) {
        //context.bodyAsClass parses request body into record class probably
        try {
            String authToken = getAuthHeader(context);
            if (authToken.equals("")) {
                context.status(400).result("{\"message\":\"error authToken is blank\"}");
                return;
            }

            try {
                GameService.JoinGameRequest game = context.bodyAsClass(GameService.JoinGameRequest.class);
                game.authToken = authToken;
                games.JoinGame(game);
            }
            catch (IllegalArgumentException ex) {
                context.status(400).result("{\"message\":\"error color already used\"}");
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("{\"message\":\"error Request body should be json\"}");
            return;
        }

    }
    //
    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JsonMapper() {
                @NotNull
                @Override
                public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                    try {
                        return gson.fromJson(json,targetType);
                    }
                    catch (Exception e) {
                        throw new IllegalStateException("Request body should be json");
                    }
                }

                @NotNull
                @Override
                public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                    return gson.toJson(obj,type);
                }
            });
        });
        UserDAO usersdao = new UserDAO();
        GameDAO gamesdao = new GameDAO();
        AuthDAO authsdao = new AuthDAO();
        users = new UserService(gamesdao,usersdao,  authsdao);
        games = new GameService(  authsdao, gamesdao, usersdao);
        auths = new AuthService(usersdao, gamesdao, authsdao);
        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::Clear);
        javalin.post("/user", this::Register);
        javalin.post("/session", this::Login);
        javalin.delete("/session", this::Logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::CreateGame);
        javalin.put("/game", this::joinGame);


    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
