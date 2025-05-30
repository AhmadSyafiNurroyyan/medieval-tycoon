package interfaces;

import model.Player;
import model.Inventory;
import model.Gerobak;

public interface FileManager {
    public boolean saveGame(Player player);

    public Player loadGame(String username);

    public boolean saveGameWithContext(Player player, int currentDay, String currentLocation, int gameScore);

    public Player loadGameWithContext(String username);

    public boolean saveHighScore(Player player, int finalScore);

    public String[] loadHighScores(); // Returns top 10 scores

    public boolean saveInventory(String username, Inventory inventory);

    public boolean savePlayerProgress(String username, int level, int money, int day);

    public boolean saveGameSettings(String username, boolean bgmEnabled, int difficulty);

    public boolean doesSaveFileExist(String username);

    public boolean deleteSaveFile(String username);

    public String[] getAllSaveFiles(); // List all available saves

    public boolean autoSave(Player player, int currentDay);

    public Player loadAutoSave(String username);
}