package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class GameDAO {
    //CREATE OBJECTS FROM DATA STORE
    ArrayList<GameData> allGames = new ArrayList<>();
    public void createGame(GameData gameData) {
        allGames.add(gameData);
    }
    //READ OBJECTS FROM DATA STORE
    public Collection<GameData> listGames(String authtoken) {

        return allGames;
    }
    public GameData getGame(String authtoken) {

        return null;
    }
    //UPDATE OBJECTS FROM DATA STORE
    public void joinGame(String authtoken) {

    }

    private void updateGame(GameData authtoken) {

    }
    //DELETE OBJECTS FROM DATA STORE
    public void clearGameData() {

    }
}
