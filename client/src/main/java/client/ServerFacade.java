package client;

import com.google.gson.Gson;

import model.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }
}
/*
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
            GameData game = context.bodyAsClass(GameData.class);
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
            }
            catch (DataAccessException e) {
                context.status(500).result("{\"message\":\"error cannot join game\"}");
            }
            catch (IllegalArgumentException ex) {
                context.status(400).result("{\"message\":\"error null game\"}");
            }
            catch (IllegalAccessError ex) {
                context.status(403).result("{\"message\":\"error color already used\"}");
            }
            catch (IllegalStateException ex) {
                context.status(401).result("{\"message\":\"error null auth\"}");
            }
            catch (IllegalCallerException ex) {
                context.status(400).result("{\"message\":\"error no good color\"}");
            }
        }
        catch (IllegalStateException ex) {
            context.status(400).result("{\"message\":\"error Request body should be json\"}");
            return;
        }


*/