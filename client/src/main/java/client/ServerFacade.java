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
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Throwable e) {
            System.out.print(RED + "Error: could not create game" + e.getMessage());
        }
    }


    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                System.out.print(RED + "Error: could not create game" + e.getMessage());
            }

            System.out.print(RED + "Error: could not create game" + e.getMessage());


            if (responseClass != null) {
                return new Gson().fromJson(response.body(), responseClass);
            }


        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private void register() {
    }

    private void login() {

    }

    private void listGames() {
        var request = buildRequest("GET", "/pet", null);
        var response = sendRequest(request);
        return handleResponse(response, PetList.class);

    }


