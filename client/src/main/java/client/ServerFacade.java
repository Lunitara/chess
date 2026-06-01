package client;

import com.google.gson.Gson;

import model.*;
import model.GameData;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;
import java.util.Objects;

import static java.awt.Color.RED;

public class ServerFacade {
     final HttpClient client = HttpClient.newHttpClient();
     final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

     HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

     BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

     HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Throwable e) {
            System.out.print(RED + "Error: could not create game" + e.getMessage());
        }
        return null;
    }


     <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                System.out.print(RED + "Error: could not create game");
            }

            System.out.print(RED + "Error: could not create game");


            if (responseClass != null) {
                return new Gson().fromJson(response.body(), responseClass);
            }


        }
        return null;
    }

     boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

     AuthResult register(String username, String password, String email) {
         var reqBody = new RegisterRequest(username, password, email);
         var request = buildRequest("POST", "/user", null);
         var response = sendRequest(request);
         return handleResponse(response, AuthResult.class);

     }

     AuthResult login(String username, String password) {
         var reqBody = new LoginRequest(username, password);
         var request = buildRequest("POST", "/session", null);
         var response = sendRequest(request);
         return handleResponse(response, AuthResult.class);

    }

    //fix so it pulls the individual games later
      GameData listGames() {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        return handleResponse(response, GameData.class);

    }

}
record RegisterResult(String username, String authToken) {
}

record RegisterRequest(String username, String password, String email) {
}

record LoginResult(String username, String authToken) {
}
record LoginRequest(String username, String authToken) {
}
record CreateGameRequest(String authToken, String gameName) {
}
record CreateGameResult(int gameID) {
}
record JoinGameRequest(String playerColor, int gameID, String authToken) {
}
record ListGamesResult(Collection<GameData> games) {
}
record AuthResult(String username, String authToken) {}