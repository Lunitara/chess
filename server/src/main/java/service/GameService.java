package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static java.lang.Math.abs;

public class GameService {
    private UserDAO users;
    private GameDAO games;
    private AuthDAO auths;
    private int newGameID = 1;
    public GameService(AuthDAO auths, GameDAO games, UserDAO users) {
        this.auths = auths;
        this.games = games;
        this.users = users;

    }
    public record CreateGameRequest(String authToken, String gameName) {
    }
    public record CreateGameResult(int gameID) {
    }
    public record JoinGameRequest(String playerColor, int gameID, String authToken) {
    }
    public record ListGamesResult(Collection<GameData> games) {
    }

    public GameService.CreateGameResult createGame(CreateGameRequest createGameRequest)  throws DataAccessException{
        AuthData existingAuth = auths.getAuth(createGameRequest.authToken());
        if (existingAuth == null) {
            throw new IllegalArgumentException("error null");
        }
        Random rand = new Random();
        newGameID = abs(rand.nextInt());
        System.out.println(newGameID);
        GameData gameData = new GameData(newGameID,null,null,
                createGameRequest.gameName(), new ChessGame());
        int gameID = games.createGame(gameData);
        return new GameService.CreateGameResult(gameID);
    }
    public boolean checkColorAvailability(GameData gameData, String playerColor)  throws DataAccessException{
        if (gameData.blackUsername() == null && Objects.equals(playerColor, "BLACK") ||
                gameData.whiteUsername() == null && Objects.equals(playerColor, "WHITE")) {
            return true;
        }
        return false;
    }

    public void joinGame(JoinGameRequest joinGameRequest)  throws DataAccessException{

        model.GameData gameData = games.getGame(joinGameRequest.gameID());
        AuthData existingAuth = auths.getAuth(joinGameRequest.authToken());
        if (existingAuth == null) {
            throw new IllegalStateException("error null auth");
        }
        if (gameData == null) {
            throw new IllegalArgumentException("error null game");
        }
        String color = joinGameRequest.playerColor();
        if (color == null ||(!color.equals("WHITE") && !color.equals("BLACK"))) {
            if ("OBSERVER".equals(color)) {
                return;
            }
            throw new IllegalCallerException("error unauthorized (color not available)");
        }

            if (checkColorAvailability(gameData, color)) {

                if (Objects.equals(color, "BLACK")) {
                    gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                            existingAuth.username(), gameData.gameName(), gameData.game());
                }
                if (Objects.equals(color, "WHITE")) {
                    gameData = new GameData(gameData.gameID(), existingAuth.username(),
                            gameData.blackUsername(), gameData.gameName(), gameData.game());
                }
                games.updateGame(gameData);
            } else {
                throw new IllegalAccessError("error unauthorized (color not available)");
            }

    }

    public ListGamesResult listGames(String authToken)  throws DataAccessException {
        if (auths.getAuth(authToken) == null) {
            throw new IllegalArgumentException("error unauthorized (color not available)");
        }
        return new ListGamesResult(games.listGames());
    }
    public void clearGameData()  throws DataAccessException {
        games.clearGameData();
    }
}
