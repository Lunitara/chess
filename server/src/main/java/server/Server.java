package server;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JsonMapper;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.GameService;
import service.AuthService;
import io.javalin.json.JavalinJackson;

import java.lang.reflect.Type;

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
            try {
                UserService.RegisterResult result = users.register(user);
                context.json(result);
            }
            catch (IllegalArgumentException ex) {
                context.status(403).result("That username already exists");
            }

        }
        catch (IllegalStateException ex) {
            context.status(400).result("Request body should be json");
        }


    }
    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JsonMapper() {
                @NotNull
                @Override
                public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                    return gson.fromJson(json,targetType);
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
        users = new UserService(usersdao, gamesdao, authsdao);
        games = new GameService(usersdao, gamesdao, authsdao);
        auths = new AuthService(usersdao, gamesdao, authsdao);
        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::Clear);
        javalin.post("/user", this::Register);



    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
