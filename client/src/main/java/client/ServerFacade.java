package client;

import com.google.gson.Gson;

import model.GameData;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;


public class ServerFacade {
    final HttpClient client = HttpClient.newHttpClient();
    final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .header("Content-Type", "application/json");
        if (authToken != null) {
            request.header("Authorization", authToken);
        }
        request.method(method, makeRequestBody(body));

        return request.build();
    }

    HttpRequest.BodyPublisher makeRequestBody(Object body) {
        if (body == null) {
            return HttpRequest.BodyPublishers.noBody();
        }
        String jsonText = new com.google.gson.Gson().toJson(body);
        return HttpRequest.BodyPublishers.ofString(jsonText);
    }

    HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        return client.send(request, BodyHandlers.ofString());
    }


    <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (isSuccessful(status)) {
            var body = response.body();
            if (responseClass != null && body != null) {
                return new Gson().fromJson(body, responseClass);
            }
        } else {

            String errorString = response.body();
            if (errorString != null && errorString.contains("java.lang")) {
                errorString = errorString.split("java.lang")[0];
            }
            if (errorString == null) {
                System.out.print("Error: could not perform action, status code: " + status + "\n");
            }
            throw new Exception(errorString);
        }
        return null;
    }

    boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    AuthResult register(String username, String password, String email) throws Exception {
        var reqBody = new RegisterRequest(username, password, email);
        var request = buildRequest("POST", "/user", reqBody, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthResult.class);
    }

    AuthResult login(String username, String password) throws Exception {
        var reqBody = new LoginRequest(username, password);
        var request = buildRequest("POST", "/session", reqBody, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthResult.class);

    }

    CreateGameResult create(String authToken, String gameName) throws Exception {
        var reqBody = new CreateGameRequest(gameName);
        var request = buildRequest("POST", "/game", reqBody, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    ListGamesResult listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }
    JoinGameRequest joinGame(String playerColor, int gameID, String authToken) throws Exception {
        var reqBody = new JoinGameRequest(playerColor, gameID);
        var request = buildRequest("PUT", "/game", reqBody, authToken);
        var response = sendRequest(request);
        return handleResponse(response, JoinGameRequest.class);
    }
    ObserveGameRequest observeGame(int gameID, String authToken) throws Exception {
        var reqBody = new ObserveGameRequest(gameID);
        var request = buildRequest("PUT", "/game", reqBody, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ObserveGameRequest.class);
    }

    void clear(String authToken) throws Exception {
        var request = buildRequest("DELETE", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, Clear.class);
    }

}

record RegisterResult(String username, String authToken) {
}

record RegisterRequest(String username, String password, String email) {
}

record clear(String authToken) {
}

record LoginRequest(String username, String password) {
}

record CreateGameRequest(String gameName) {
}

record CreateGameResult(int gameID) {
}

record JoinGameRequest(String playerColor, int gameID) {
}
record ObserveGameRequest(int gameID) {
}

record ListGamesResult(Collection<GameData> games) {
}

record AuthResult(String username, String authToken) {
}