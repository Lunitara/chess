package server;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JsonMapper;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.GameService;
import service.AuthService;

import java.lang.reflect.Type;
import java.util.Objects;

public class Server {

    private final Javalin javalin;
    private   UserService users;
    private   GameService games;
    private   AuthService auths;
    private final Gson gson = new Gson();


    public  void clear(@NotNull Context context)  {
        try {
            users.clearUserData();
            games.clearGameData();
            auths.clearAuthData();
        } catch (DataAccessException e) {
            context.status(500).result("{\"message\":\"error clearing data\"}");
        }
    }
    private  void register(@NotNull Context context) {
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
            catch (DataAccessException e) {
                context.status(500).result("{\"message\":\"error cannot register\"}");
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
    private  void login(@NotNull Context context)  {
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
                context.status(200);
                context.json(result);
            }
            catch (DataAccessException ex) {
                context.status(500).result("{\"message\":\"error unauthorized\"}");

            }
            catch (IllegalArgumentException ex) {
                context.status(401).result("{\"message\":\"error unauthorized\"}");

            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("Request body should be json");
        }
    }
    //
    private void listGames(@NotNull Context context){
        //context.bodyAsClass parses request body into record class probably

        try {
            String authToken = getAuthHeader(context);

            if (Objects.equals(authToken, "")) {
                context.status(400).result("{\"message\":\"error authToken is blank\"}");
            }
            if (Objects.equals(authToken, null)) {
                context.status(400).result("{\"message\":\"error authToken is null\"}");
            }
            try {
                GameService.ListGamesResult result = games.listGames(authToken);
                context.json(result);

            }
            catch (DataAccessException e) {
                context.status(500).result("{\"message\":\"error cannot list games\"}");
            }
            catch (IllegalArgumentException ex) {
                context.status(401).result("{\"message\":\"error unauthorized\"}");
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("{\"message\":\"error request body should be json\"}");
        }

    }
    //
    private  void logout(@NotNull Context context)  {
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
            catch (DataAccessException e) {
                context.status(500).result("{\"message\":\"error cannot logout\"}");
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
    private  void createGame(@NotNull Context context){
        //context.bodyAsClass parses request body into record class probably

        try {
            GameService.CreateGameRequest game = context.bodyAsClass(GameService.CreateGameRequest.class);
            if (Objects.equals(game.gameName(), "") || Objects.equals(game.gameName(), null)) {
                context.status(400).result("{\"message\":\"error cannot have blank catagory\"}");
                return;
            }
            try {

                GameService.CreateGameResult result = games.createGame(new GameService.CreateGameRequest(getAuthHeader(context),game.gameName()));
                context.json(result);
            }
            catch (DataAccessException e) {
                context.status(500).result("{\"message\":\"error cannot create game\"}");
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
    private  void joinGame(@NotNull Context context){
        //context.bodyAsClass parses request body into record class probably
        try {
            String authToken = getAuthHeader(context);
            if (authToken.equals("")) {
                context.status(400).result("{\"message\":\"error authToken is blank\"}");
                return;
            }

            try {
                GameService.JoinGameRequest game = context.bodyAsClass(GameService.JoinGameRequest.class);
                game = new GameService.JoinGameRequest(game.playerColor(), game.gameID(), authToken);
                games.joinGame(game);
                context.status(200).result("{}");
                return;
            }
            catch (DataAccessException e) {
                context.status(500).result("{\"message\":\"error cannot join game\"}");
            return;
            }
            catch (IllegalArgumentException ex) {
                context.status(400).result("{\"message\":\"error null game\"}");
                return;
            }
            catch (IllegalAccessError ex) {
                context.status(403).result("{\"message\":\"error color already used\"}");
                return;
            }
            catch (IllegalStateException ex) {
                context.status(401).result("{\"message\":\"error null auth\"}");
                return;
            }
            catch (IllegalCallerException ex) {
                context.status(400).result("{\"message\":\"error no good color\"}");
                return;
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("{\"message\":\"error Request body should be json\"}");
            return;
        }

    }
    //
    public Server(){
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
        try {
            UserDAO usersdao = new SqlUserDAO();
            GameDAO gamesdao = new SqlGameDAO();
            AuthDAO authsdao = new SqlAuthDAO();
            DatabaseManager.configureDatabase();

            users = new UserService(gamesdao, usersdao, authsdao);
            games = new GameService(authsdao, gamesdao, usersdao);
            auths = new AuthService(usersdao, gamesdao, authsdao);
            // Register your endpoints and exception handlers here.
            javalin.delete("/db", this::clear);
            javalin.post("/user", this::register);
            javalin.post("/session", this::login);
            javalin.delete("/session", this::logout);
            javalin.get("/game", this::listGames);
            javalin.post("/game", this::createGame);
            javalin.put("/game", this::joinGame);
        } catch (DataAccessException e) {
        };
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
