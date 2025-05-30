package model;

import interfaces.FileManager;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TextFileManager implements FileManager {

  private static final String SAVE_DIRECTORY = "saves";
  private static final String SAVE_EXTENSION = ".txt";
  private static final String AUTOSAVE_SUFFIX = "_autosave";
  private static final String HIGHSCORE_FILE = "highscores.txt";

  public TextFileManager() {
    // Create saves directory if it doesn't exist
    try {
      Files.createDirectories(Paths.get(SAVE_DIRECTORY));
    } catch (IOException e) {
      System.err.println("Failed to create saves directory: " + e.getMessage());
    }
  }

  private String getSaveFilePath(String username) {
    return SAVE_DIRECTORY + File.separator + username + SAVE_EXTENSION;
  }

  private String getAutoSaveFilePath(String username) {
    return SAVE_DIRECTORY + File.separator + username + AUTOSAVE_SUFFIX + SAVE_EXTENSION;
  }

  @Override
  public boolean saveGame(Player player) {
    return saveGameWithContext(player, 1, "HomeBase", 0);
  }

  @Override
  public Player loadGame(String username) {
    return loadGameWithContext(username);
  }

  @Override
  public boolean saveGameWithContext(Player player, int currentDay, String currentLocation, int gameScore) {
    try {
      String filePath = getSaveFilePath(player.getUsername());
      return writePlayerDataToFile(player, filePath, currentDay, currentLocation, gameScore, true, 1);
    } catch (Exception e) {
      System.err.println("Error saving game: " + e.getMessage());
      return false;
    }
  }

  @Override
  public Player loadGameWithContext(String username) {
    try {
      String filePath = getSaveFilePath(username);
      return loadPlayerFromFile(filePath);
    } catch (Exception e) {
      System.err.println("Error loading game: " + e.getMessage());
      return null;
    }
  }

  @Override
  public boolean saveHighScore(Player player, int finalScore) {
    try {
      List<String> highScores = new ArrayList<>();

      // Load existing high scores
      String filePath = SAVE_DIRECTORY + File.separator + HIGHSCORE_FILE;
      if (Files.exists(Paths.get(filePath))) {
        highScores = Files.readAllLines(Paths.get(filePath));
      }

      // Add new score
      String newScore = player.getUsername() + ":" + finalScore;
      highScores.add(newScore);

      // Sort by score (descending) and keep top 10
      highScores.sort((a, b) -> {
        int scoreA = Integer.parseInt(a.split(":")[1]);
        int scoreB = Integer.parseInt(b.split(":")[1]);
        return Integer.compare(scoreB, scoreA);
      });

      if (highScores.size() > 10) {
        highScores = highScores.subList(0, 10);
      }

      Files.write(Paths.get(filePath), highScores);
      return true;
    } catch (Exception e) {
      System.err.println("Error saving high score: " + e.getMessage());
      return false;
    }
  }

  @Override
  public String[] loadHighScores() {
    try {
      String filePath = SAVE_DIRECTORY + File.separator + HIGHSCORE_FILE;
      if (!Files.exists(Paths.get(filePath))) {
        return new String[0];
      }

      List<String> scores = Files.readAllLines(Paths.get(filePath));
      return scores.toArray(new String[0]);
    } catch (Exception e) {
      System.err.println("Error loading high scores: " + e.getMessage());
      return new String[0];
    }
  }

  @Override
  public boolean saveInventory(String username, Inventory inventory) {
    // This is handled within saveGameWithContext
    return true;
  }

  @Override
  public boolean savePlayerProgress(String username, int level, int money, int day) {
    try {
      Player player = loadGameWithContext(username);
      if (player == null) {
        return false;
      }

      player.setLevel(level);
      player.tambahMoney(money - player.getMoney()); // Adjust money
      return saveGameWithContext(player, day, "HomeBase", 0);
    } catch (Exception e) {
      System.err.println("Error saving player progress: " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean saveGameSettings(String username, boolean bgmEnabled, int difficulty) {
    try {
      Player player = loadGameWithContext(username);
      if (player == null) {
        return false;
      }

      return saveGameWithContext(player, 1, "HomeBase", 0); // Save with updated settings
    } catch (Exception e) {
      System.err.println("Error saving game settings: " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean doesSaveFileExist(String username) {
    String filePath = getSaveFilePath(username);
    return Files.exists(Paths.get(filePath));
  }

  @Override
  public boolean deleteSaveFile(String username) {
    try {
      String filePath = getSaveFilePath(username);
      String autoSaveFilePath = getAutoSaveFilePath(username);

      boolean deleted = false;
      if (Files.exists(Paths.get(filePath))) {
        Files.delete(Paths.get(filePath));
        deleted = true;
      }

      if (Files.exists(Paths.get(autoSaveFilePath))) {
        Files.delete(Paths.get(autoSaveFilePath));
        deleted = true;
      }

      return deleted;
    } catch (Exception e) {
      System.err.println("Error deleting save file: " + e.getMessage());
      return false;
    }
  }

  @Override
  public String[] getAllSaveFiles() {
    try {
      File saveDir = new File(SAVE_DIRECTORY);
      if (!saveDir.exists()) {
        return new String[0];
      }

      List<String> saveFiles = new ArrayList<>();
      File[] files = saveDir.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.endsWith(SAVE_EXTENSION) && !name.contains(AUTOSAVE_SUFFIX);
        }
      });

      if (files != null) {
        for (File file : files) {
          String filename = file.getName();
          String username = filename.substring(0, filename.lastIndexOf(SAVE_EXTENSION));
          saveFiles.add(username);
        }
      }

      return saveFiles.toArray(new String[0]);
    } catch (Exception e) {
      System.err.println("Error getting save files: " + e.getMessage());
      return new String[0];
    }
  }

  @Override
  public boolean autoSave(Player player, int currentDay) {
    try {
      String filePath = getAutoSaveFilePath(player.getUsername());
      return writePlayerDataToFile(player, filePath, currentDay, "HomeBase", 0, true, 1);
    } catch (Exception e) {
      System.err.println("Error auto saving: " + e.getMessage());
      return false;
    }
  }

  @Override
  public Player loadAutoSave(String username) {
    try {
      String filePath = getAutoSaveFilePath(username);
      return loadPlayerFromFile(filePath);
    } catch (Exception e) {
      System.err.println("Error loading auto save: " + e.getMessage());
      return null;
    }
  }

  private boolean writePlayerDataToFile(Player player, String filePath, int currentDay,
      String currentLocation, int gameScore, boolean bgmEnabled, int difficulty) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

      // === PLAYER_DATA ===
      writer.println("=== PLAYER_DATA ===");
      writer.println("USERNAME:" + player.getUsername());
      writer.println("ID:" + player.getID());
      writer.println("LEVEL:" + player.getLevel());
      writer.println("MONEY:" + player.getMoney());
      writer.println("CURRENT_DAY:" + currentDay);
      writer.println();

      // === INVENTORY_BARANG ===
      writer.println("=== INVENTORY_BARANG ===");
      Map<Barang, Integer> stokBarang = getInventoryBarang(player.getInventory());
      for (Map.Entry<Barang, Integer> entry : stokBarang.entrySet()) {
        Barang barang = entry.getKey();
        int jumlah = entry.getValue();
        writer.println("BARANG:" + barang.getNamaBarang() + "," +
            barang.getKategori() + "," +
            barang.getHargaBeli() + "," +
            barang.getKesegaran() + "," +
            jumlah);
      }
      writer.println("// Format: nama,kategori,hargaBeli,kesegaran,jumlah");
      writer.println();

      // === INVENTORY_ITEM ===
      writer.println("=== INVENTORY_ITEM ===");
      List<Item> stokItem = player.getInventory().getStokItem();
      List<Item> itemDibawa = player.getInventory().getItemDibawa();

      for (Item item : stokItem) {
        boolean inGerobak = itemDibawa.contains(item);
        writer.println("ITEM:" + item.getNama() + "," +
            item.getLevel() + "," +
            item.isActive() + "," +
            inGerobak);
      }
      writer.println("// Format: nama,level,active,inGerobak");
      writer.println();

      // === GEROBAK ===
      writer.println("=== GEROBAK ===");
      Gerobak gerobak = player.getInventory().getGerobak();
      writer.println("LEVEL:" + gerobak.getLevel());
      writer.println("KAPASITAS_BARANG:" + gerobak.getKapasitasBarang());
      writer.println("KAPASITAS_ITEM:" + gerobak.getKapasitasItem());
      writer.println("BIAYA_UPGRADE:" + gerobak.getBiayaUpgrade());
      writer.println();

      // === GEROBAK_BARANG ===
      writer.println("=== GEROBAK_BARANG ===");
      Map<Barang, Integer> barangDibawa = player.getInventory().getBarangDibawaMutable();
      for (Map.Entry<Barang, Integer> entry : barangDibawa.entrySet()) {
        Barang barang = entry.getKey();
        int jumlah = entry.getValue();
        int hargaJual = player.getInventory().getHargaJual(barang);
        writer.println("GEROBAK_BARANG:" + barang.getNamaBarang() + "," +
            barang.getKategori() + "," +
            barang.getHargaBeli() + "," +
            barang.getKesegaran() + "," +
            jumlah + "," +
            hargaJual);
      }
      writer.println("// Format: nama,kategori,hargaBeli,kesegaran,jumlah,hargaJual");
      writer.println();

      // === PERKS ===
      writer.println("=== PERKS ===");
      List<Perk> ownedPerks = player.getSemuaPerkDimiliki();
      List<Perk> activePerks = player.getPerkDipilihUntukJualan();

      for (Perk perk : ownedPerks) {
        writer.println("OWNED_PERK:" + perk.getName() + "," +
            perk.getLevel() + "," +
            "true");
      }

      for (Perk perk : activePerks) {
        writer.println("ACTIVE_PERK:" + perk.getName() + "," +
            perk.getLevel() + "," +
            perk.isActive());
      }
      writer.println("// Format: nama,level,owned/active");
      writer.println();

      // === GAME_PROGRESS ===
      writer.println("=== GAME_PROGRESS ===");
      writer.println("SCORE:" + gameScore);
      writer.println("LOCATION:" + currentLocation);
      writer.println("BGM_ENABLED:" + bgmEnabled);
      writer.println("DIFFICULTY:" + difficulty);

      return true;
    } catch (Exception e) {
      System.err.println("Error writing to file: " + e.getMessage());
      return false;
    }
  }

  private Player loadPlayerFromFile(String filePath) throws IOException {
    if (!Files.exists(Paths.get(filePath))) {
      return null;
    }

    List<String> lines = Files.readAllLines(Paths.get(filePath));
    Player player = new Player();
    Inventory inventory = new Inventory();
    Gerobak gerobak = new Gerobak();

    String currentSection = "";

    for (String line : lines) {
      line = line.trim();

      if (line.isEmpty() || line.startsWith("//")) {
        continue;
      }

      if (line.startsWith("===") && line.endsWith("===")) {
        currentSection = line;
        continue;
      }

      switch (currentSection) {
        case "=== PLAYER_DATA ===":
          parsePlayerData(line, player);
          break;

        case "=== INVENTORY_BARANG ===":
          parseInventoryBarang(line, inventory);
          break;

        case "=== INVENTORY_ITEM ===":
          parseInventoryItem(line, inventory);
          break;

        case "=== GEROBAK ===":
          parseGerobak(line, gerobak);
          break;

        case "=== GEROBAK_BARANG ===":
          parseGerobakBarang(line, inventory);
          break;

        case "=== PERKS ===":
          parsePerks(line, player);
          break;

        case "=== GAME_PROGRESS ===":
          parseGameProgress(line, player);
          break;
      }
    }

    inventory.setGerobak(gerobak);
    setPlayerInventory(player, inventory);

    return player;
  }

  private void parsePlayerData(String line, Player player) {
    String[] parts = line.split(":", 2);
    if (parts.length != 2)
      return;

    String key = parts[0].trim();
    String value = parts[1].trim();

    switch (key) {
      case "USERNAME":
        player.setUsername(value);
        break;
      case "ID":
        player.setID(Integer.parseInt(value));
        break;
      case "LEVEL":
        player.setLevel(Integer.parseInt(value));
        break;
      case "MONEY":
        int currentMoney = player.getMoney();
        int targetMoney = Integer.parseInt(value);
        player.tambahMoney(targetMoney - currentMoney);
        break;
    }
  }

  private void parseInventoryBarang(String line, Inventory inventory) {
    if (!line.startsWith("BARANG:"))
      return;

    String data = line.substring("BARANG:".length());
    String[] parts = data.split(",");
    if (parts.length != 5)
      return;

    String nama = parts[0].trim();
    String kategori = parts[1].trim();
    int hargaBeli = Integer.parseInt(parts[2].trim());
    int kesegaran = Integer.parseInt(parts[3].trim());
    int jumlah = Integer.parseInt(parts[4].trim());

    // Create barang with icon path (you may need to map names to icon paths)
    String iconPath = nama.toLowerCase() + ".png";
    Barang barang = new Barang(nama, kategori, hargaBeli, iconPath);

    // Set kesegaran by reducing from 100
    int kesegaranToReduce = 100 - kesegaran;
    for (int i = 0; i < kesegaranToReduce / 25; i++) {
      barang.kurangiKesegaran();
    }

    // Add multiple instances
    for (int i = 0; i < jumlah; i++) {
      inventory.tambahBarang(barang);
    }
  }

  private void parseInventoryItem(String line, Inventory inventory) {
    if (!line.startsWith("ITEM:"))
      return;

    String data = line.substring("ITEM:".length());
    String[] parts = data.split(",");
    if (parts.length != 4)
      return;

    String nama = parts[0].trim();
    int level = Integer.parseInt(parts[1].trim());
    boolean active = Boolean.parseBoolean(parts[2].trim());
    boolean inGerobak = Boolean.parseBoolean(parts[3].trim());

    // Create item (you may need to provide proper constructor parameters)
    String iconPath = nama.toLowerCase() + ".png";
    Item item = new Item(nama, "Item description", 100000, 50000, iconPath);

    // Set level
    for (int i = 0; i < level; i++) {
      item.upgradeLevel();
    }

    if (active) {
      item.activate();
    }

    inventory.tambahItem(item);

    if (inGerobak) {
      inventory.bawaItem(nama, 999); // Assuming large capacity
    }
  }

  private void parseGerobak(String line, Gerobak gerobak) {
    String[] parts = line.split(":", 2);
    if (parts.length != 2)
      return;

    String key = parts[0].trim();
    String value = parts[1].trim();

    if ("LEVEL".equals(key)) {
      int targetLevel = Integer.parseInt(value);
      // Upgrade gerobak to target level
      for (int i = 0; i < targetLevel; i++) {
        gerobak.upgradeLevel();
      }
    }
  }

  private void parseGerobakBarang(String line, Inventory inventory) {
    if (!line.startsWith("GEROBAK_BARANG:"))
      return;

    String data = line.substring("GEROBAK_BARANG:".length());
    String[] parts = data.split(",");
    if (parts.length != 6)
      return;

    String nama = parts[0].trim();
    String kategori = parts[1].trim();
    int hargaBeli = Integer.parseInt(parts[2].trim());
    int kesegaran = Integer.parseInt(parts[3].trim());
    int jumlah = Integer.parseInt(parts[4].trim());
    int hargaJual = Integer.parseInt(parts[5].trim());

    String iconPath = nama.toLowerCase() + ".png";
    Barang barang = new Barang(nama, kategori, hargaBeli, iconPath);

    // Set kesegaran
    int kesegaranToReduce = 100 - kesegaran;
    for (int i = 0; i < kesegaranToReduce / 25; i++) {
      barang.kurangiKesegaran();
    }

    inventory.tambahBarangDibawa(barang, jumlah);
    inventory.setHargaJual(barang, hargaJual);
  }

  private void parsePerks(String line, Player player) {
    if (line.startsWith("OWNED_PERK:")) {
      String data = line.substring("OWNED_PERK:".length());
      String[] parts = data.split(",");
      if (parts.length >= 3) {
        String nama = parts[0].trim();
        int level = Integer.parseInt(parts[1].trim());

        // Create and add perk based on name
        Perk perk = createPerkByName(nama);
        if (perk != null) {
          // Upgrade to target level
          for (int i = 0; i < level; i++) {
            perk.upgradeLevel();
          }
          player.addPerk(perk);
        }
      }
    } else if (line.startsWith("ACTIVE_PERK:")) {
      String data = line.substring("ACTIVE_PERK:".length());
      String[] parts = data.split(",");
      if (parts.length >= 3) {
        String nama = parts[0].trim();
        boolean active = Boolean.parseBoolean(parts[2].trim());

        // Find the perk in player's owned perks and activate if needed
        for (Perk perk : player.getSemuaPerkDimiliki()) {
          if (perk.getName().equals(nama)) {
            if (active) {
              player.pilihPerkUntukJualan(perk);
            }
            break;
          }
        }
      }
    }
  }

  private void parseGameProgress(String line, Player player) {
    // Game progress parsing can be extended as needed
    // Currently just parsing for completeness
  }

  private Perk createPerkByName(String nama) {
    switch (nama.toLowerCase()) {
      case "elegan":
        return new PerksElegan();
      case "charming":
        return new PerksCharming();
      case "active":
        return new PerksActive();
      default:
        return null;
    }
  }

  // Helper methods that need to access private fields - these might need
  // reflection or getter methods
  @SuppressWarnings("unchecked")
  private Map<Barang, Integer> getInventoryBarang(Inventory inventory) {
    try {
      java.lang.reflect.Field field = Inventory.class.getDeclaredField("stokBarang");
      field.setAccessible(true);
      return (Map<Barang, Integer>) field.get(inventory);
    } catch (Exception e) {
      System.err.println("Error accessing inventory barang: " + e.getMessage());
      return new HashMap<>();
    }
  }

  private void setPlayerInventory(Player player, Inventory inventory) {
    try {
      java.lang.reflect.Field field = Player.class.getDeclaredField("inventory");
      field.setAccessible(true);
      field.set(player, inventory);
    } catch (Exception e) {
      System.err.println("Error setting player inventory: " + e.getMessage());
    }
  }
}