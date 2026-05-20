package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class GameDAO {
    //CREATE OBJECTS FROM DATA STORE
    private int counter = 0;
    ArrayList<GameData> allGames = new ArrayList<>();
    public int createGame(GameData gameData) {
        counter++;
        GameData newGame = new GameData(counter,gameData.whiteUsername(),gameData.blackUsername(),gameData.gameName(),gameData.game());
        allGames.add(newGame);
        return counter;
    }
    //READ OBJECTS FROM DATA STORE
    public Collection<GameData> listGames(String authtoken) {

        return allGames;
    }
    public GameData getGame(int gameID) {

        for (int i = 0; i < allGames.size(); i++) {
            if (Objects.equals(allGames.get(i).gameID(), gameID)) {
                return allGames.get(i);
            }
        }
        return null;
    }
    //UPDATE OBJECTS FROM DATA STORE

    public void updateGame(GameData gameData) {
        for (int i = 0; i < allGames.size(); i++) {
            if (Objects.equals(allGames.get(i).gameID(), gameData.gameID())) {
                allGames.set(i, gameData);
            }
        }
    }
    //DELETE OBJECTS FROM DATA STORE
    public void clearGameData() {
        allGames.clear();
    }
}
