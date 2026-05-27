package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public interface GameDAO {
    //CREATE OBJECTS FROM DATA STORE
    public int createGame(GameData gameData);
    //READ OBJECTS FROM DATA STORE
    public Collection<GameData> listGames();
    public GameData getGame(int gameID);
    //UPDATE OBJECTS FROM DATA STORE

    public void updateGame(GameData gameData);
    //DELETE OBJECTS FROM DATA STORE
    public void clearGameData();
}
