/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package interfaces;

import model.Inventory;
import model.Player;

public interface FileManager {
    public boolean saveGame(Player player);
    public Player loadGame(String username);
    public boolean saveGameWithContext(Player player, int currentDay, String currentLocation, int gameScore);
    public Player loadGameWithContext(String username);
    public boolean saveHighScore(Player player, int finalScore);
    public String[] loadHighScores();
    public boolean saveInventory(String username, Inventory inventory);
    public boolean savePlayerProgress(String username, int level, int money, int day);
    public boolean saveGameSettings(String username, boolean bgmEnabled, int difficulty);
    public boolean doesSaveFileExist(String username);
    public boolean deleteSaveFile(String username);
    public String[] getAllSaveFiles();
    public boolean autoSave(Player player, int currentDay);
    public Player loadAutoSave(String username);
}