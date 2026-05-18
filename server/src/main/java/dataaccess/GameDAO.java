package dataaccess;
import model.AuthData;
import model.GameData;
import java.util.Collection;

public class GameDAO {
    //CREATE OBJECTS FROM DATA STORE
    public void createGame(String authtoken) {

    }
    //READ OBJECTS FROM DATA STORE
    public Collection<GameData> listGames(String authtoken) {

        return java.util.List.of();
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
