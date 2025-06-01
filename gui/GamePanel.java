package gui;

import camera.*;
import debugger.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import MapManager.MapObjectManager;
import MapManager.RandomTriggerZoneManager;
import MapManager.TileManager;
import MapManager.TriggerZoneManager;
import model.*;
import model.Player.PlayerMovement;
import model.Player.PlayerSkin;

public class GamePanel extends JPanel implements Runnable {
    private Player player;
    private PlayerMovement playerMovement;
    private PlayerSkin PlayerSkin;
    MapObjectManager mapObjectManager = new MapObjectManager();
    private Thread gameThread;
    private int FPS = 60;
    private TileManager tileManager;
    private Camera camera;
    private TriggerZoneManager triggerZoneManager;
    private RandomTriggerZoneManager randomTriggerZoneManager;
    private TransactionsGUI transactions;
    private Supplier supplier;
    private TokoItem tokoItem;
    private TokoPerks tokoPerks;
    private PerksManagement perksManagement;
    private Gerobak gerobak;

    private Runnable showSupplierPanelCallback;
    private Runnable showHomeBasePanelCallback;
    private Runnable showTokoItemPanelCallback;
    private Runnable showTokoPerksPanelCallback;

    // Map management
    private String currentMap = "map1";
    // Tambahan: flag untuk random zone map2
    private boolean map2ZonesGenerated = false;

    // Icon preloading system
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();
    private static final Map<String, ImageIcon> scaledIconCache = new HashMap<>();
    private static boolean iconsLoaded = false;

    // Track current day
    private int currentDay = 1;

    // ItemEffectManager moved to global variable
    private ItemEffectManager itemEffectManager;

    public GamePanel(Player player) {
        this.player = player;

        // Initialize or reuse gerobak
        if (player.getInventory() != null && player.getInventory().getGerobak() != null) {
            // Use the player's existing gerobak if it exists
            this.gerobak = player.getInventory().getGerobak();
            System.out.println("GamePanel constructor: Using player's existing gerobak (Level: " +
                    this.gerobak.getLevel() + ")");
        } else {
            // Create a new gerobak if needed
            this.gerobak = new Gerobak();
            System.out.println("GamePanel constructor: Created new gerobak");

            // Ensure the player's inventory uses the same gerobak instance
            if (player.getInventory() != null) {
                player.getInventory().setGerobak(this.gerobak);
                System.out.println("GamePanel constructor: Set player's inventory to use our gerobak");
            }
        }

        setBackground(Color.BLACK);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Preload icons on first GamePanel creation
        if (!iconsLoaded) {
            preloadIcons();
            iconsLoaded = true;
        }
        playerMovement = player.createMovement();
        PlayerSkin = player.createNametag();
        tileManager = new TileManager(this);
        camera = new Camera(this, tileManager);
        triggerZoneManager = new TriggerZoneManager();
        
        // Initialize RandomTriggerZoneManager first
        randomTriggerZoneManager = new RandomTriggerZoneManager();
        
        transactions = new TransactionsGUI(this);
        transactions.setPlayer(player); // Set player untuk transactions
        transactions.setTriggerZoneManager(triggerZoneManager); // Set trigger zone manager
        // Set RandomTriggerZoneManager agar TransactionsGUI bisa remove zone
        transactions.setRandomTriggerZoneManager(randomTriggerZoneManager);
        
        randomTriggerZoneManager.setDialogSystem(transactions);
        randomTriggerZoneManager.setPlayer(player);
        supplier = new Supplier();
        tokoItem = new TokoItem(player);
        tokoPerks = new TokoPerks();
        perksManagement = new PerksManagement();

        // Initialize map1 content
        setupMap1Content();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                playerMovement.keyPressed(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_E) {
                    // Check if dialog is open first
                    if (transactions != null && transactions.isDialogVisible()) {
                        // Always allow closing dialog if in 'no items' state
                        if (transactions.isNoItemsState()) {
                            transactions.hideDialog();
                            return;
                        }
                        // Close dialog if open (default)
                        transactions.hideDialog();
                        return;
                    }

                    // Otherwise, check for trigger zones
                    List<MapManager.TriggerZoneManager.TriggerZone> zones = triggerZoneManager
                            .getZonesAt(playerMovement.getX(), playerMovement.getY());
                    for (MapManager.TriggerZoneManager.TriggerZone zone : zones) {
                        zone.trigger();
                    }
                }                // Peluit: tekan H di map2
                if ("map2".equals(currentMap) && e.getKeyCode() == KeyEvent.VK_H) {
                    if (player != null && player.getInventory() != null) {
                        // Debug the player instance and inventory state
                        System.out.println("=== DEBUG H KEY PRESS ===");
                        System.out.println("Player instance: " + player);
                        System.out.println("Player username: " + player.getUsername());
                        System.out.println("Player inventory: " + player.getInventory());
                        System.out.println("Player inventory itemDibawa: " + player.getInventory().getItemDibawa());
                        System.out.println("ItemDibawa size: " + player.getInventory().getItemDibawa().size());
                        System.out.println("Gerobak instance: " + player.getInventory().getGerobak());
                        System.out.println("========================");
                        
                        if (itemEffectManager == null) {
                            itemEffectManager = new ItemEffectManager(player);
                        }
                        int extraBuyers = itemEffectManager.applyPeluit(currentDay);
                        if (extraBuyers > 0) {
                            for (int i = 0; i < extraBuyers; i++) {
                                randomTriggerZoneManager.spawnSingleRandomZone(triggerZoneManager);
                            }
                            JOptionPane.showMessageDialog(GamePanel.this,
                                    "Peluit digunakan! " + extraBuyers + " pembeli tambahan muncul.", "Peluit",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(GamePanel.this,
                                    "Tidak ada item Peluit aktif atau sudah dipakai hari ini!", "Peluit",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                playerMovement.keyReleased(e.getKeyCode());
            }
        });

        // no stuck keys
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                playerMovement.resetKeys();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mapX = e.getX() + camera.getX();
                int mapY = e.getY() + camera.getY();
                debugger.DebugClickLogger.logClickCoordinates(e, mapX, mapY);
            }
        });

        // Initialize player-dependent objects
        updatePlayerData(player);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        Thread oldThread = gameThread;
        gameThread = null;
        if (oldThread != null && oldThread.isAlive()) {
            oldThread.interrupt();
        }
    }

    @Override
    public void run() {
        final double drawInterval = 1000000000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        // boolean wasInZone = false;
        while (gameThread != null) {
            playerMovement.update(tileManager.getMapWidth(), tileManager.getMapHeight(), tileManager.getTileSize(),
                    mapObjectManager, tileManager);

            // boolean inZone = !triggerZoneManager.getZonesAt(playerMovement.getX(),
            // playerMovement.getY()).isEmpty();
            // if (inZone && !wasInZone) {
            // System.out.println("[DEBUG] Entered trigger zone");
            // } else if (!inZone && wasInZone) {
            // System.out.println("[DEBUG] Exited trigger zone");
            // }
            // wasInZone = inZone;
            repaint();
            try {
                double remaining = nextDrawTime - System.nanoTime();
                long sleepMs = Math.max(0, (long) (remaining / 1_000_000));
                if (sleepMs > 0) {
                    Thread.sleep(sleepMs);
                }
                nextDrawTime += drawInterval;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        camera.update(playerMovement, tileManager, this);
        tileManager.draw(g2d, camera.getX(), camera.getY());

        int px = playerMovement.getX() - camera.getX() - playerMovement.getSpriteWidth() / 2;
        int py = playerMovement.getY() - camera.getY() - playerMovement.getSpriteHeight() / 2;
        PlayerSkin.render(g, px, py, playerMovement.getCurrentFrame());
        mapObjectManager.draw(g2d, camera.getX(), camera.getY());

        DebugTriggerZoneRender.drawAllZones(g2d, triggerZoneManager, camera);

        drawMoneyInfo(g2d);
        drawUID(g2d);
    }

    public void drawMoneyInfo(Graphics2D g2d) {
        String goldText = "Gold: " + player.getMoney() + "G";
        g2d.setFont(new Font("Serif", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int goldWidth = fm.stringWidth(goldText);
        int goldX = getWidth() - goldWidth - 20;
        int goldY = 40;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(goldX - 10, goldY - fm.getAscent(), goldWidth + 20, fm.getHeight() + 8, 12, 12);
        g2d.setColor(new Color(218, 165, 32));
        g2d.drawString(goldText, goldX, goldY);
    }

    public void drawUID(Graphics2D g2d) {
        g2d.setFont(new Font("Serif", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String uidText = "UID: " + player.getID();
        int uidWidth = fm.stringWidth(uidText);
        int uidX = getWidth() - uidWidth - 20;
        int uidY = getHeight() - 20;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(uidX - 10, uidY - fm.getAscent(), uidWidth + 20, fm.getHeight() + 8, 12, 12);
        g2d.setColor(Color.WHITE);
        g2d.drawString(uidText, uidX, uidY);
    }

    public Player getPlayer() {
        return player;
    }

    public TileManager getTileManager() {
        return tileManager;
    }

    public void setShowSupplierPanelCallback(Runnable cb) {
        this.showSupplierPanelCallback = cb;
    }

    public void setShowHomeBasePanelCallback(Runnable cb) {
        this.showHomeBasePanelCallback = cb;
    }

    public void setShowTokoItemPanelCallback(Runnable cb) {
        this.showTokoItemPanelCallback = cb;
    }

    public void setShowTokoPerksPanelCallback(Runnable cb) {
        this.showTokoPerksPanelCallback = cb;
    }

    /**
     * Call this method when the GamePanel becomes visible (e.g., after returning
     * from HomeBase)
     * to ensure it has the latest player inventory state
     */
    public void onPanelShown() {
        System.out.println("GamePanel: onPanelShown() called - syncing player inventory state");

        // Make sure we have the latest inventory state
        if (this.player != null && this.player.getInventory() != null) {
            // Make sure player's inventory gerobak matches our gerobak
            if (this.player.getInventory().getGerobak() != this.gerobak) {
                System.out.println("GamePanel: Updating player's inventory gerobak reference");
                this.player.getInventory().setGerobak(this.gerobak);
            }

            // Debug inventory state
            System.out.println("GamePanel: Current items in gerobak: " +
                    this.player.getInventory().getItemDibawa().size());

            for (Item item : this.player.getInventory().getItemDibawa()) {
                System.out.println("GamePanel: Item in gerobak: " + item.getNama());
            }
        }

        // Check if player has slept and regenerate zones if needed
        if (this.player != null && this.player.isHasSlept()) {
            System.out.println("GamePanel: Player has slept, checking for zone regeneration...");
            if ("map2".equals(currentMap)) {
                System.out.println("GamePanel: Regenerating random zones for map2 after sleep");
                setupMap2Content();
            }
            // The setupMap2Content() method will reset the hasSlept flag
        }
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Gerobak getGerobak() {
        return gerobak;
    }

    public TokoItem getTokoItem() {
        return tokoItem;
    }

    public TokoPerks getTokoPerks() {
        return tokoPerks;
    }

    public PerksManagement getPerksManagement() {
        return perksManagement;
    }

    /**
     * Update player reference and recreate all player-dependent objects for loading
     * saved games
     */
    public void updatePlayerData(Player newPlayer) {
        this.player = newPlayer;

        // Recreate PlayerMovement and PlayerSkin with the new player
        this.playerMovement = newPlayer.createMovement();
        this.PlayerSkin = newPlayer.createNametag();
        this.itemEffectManager = new ItemEffectManager(newPlayer);
        // CRITICAL FIX: Update TransactionsGUI with the new player reference
        if (transactions != null) {
            transactions.setPlayer(newPlayer);
            System.out.println("GamePanel: Updated TransactionsGUI player reference");
        }

        // CRITICAL FIX: Update RandomTriggerZoneManager with the new player reference
        if (randomTriggerZoneManager != null) {
            randomTriggerZoneManager.setPlayer(newPlayer);
            System.out.println("GamePanel: Updated RandomTriggerZoneManager player reference");
        }

        // Always synchronize gerobak and inventory references
        if (newPlayer.getInventory() != null) {
            // Always use the player's inventory gerobak as the single source of truth
            this.gerobak = newPlayer.getInventory().getGerobak();
            if (this.gerobak == null) {
                this.gerobak = new Gerobak();
                newPlayer.getInventory().setGerobak(this.gerobak);
                System.out.println("GamePanel: Created new gerobak for player inventory");
            }
            // Ensure both references are in sync
            newPlayer.getInventory().setGerobak(this.gerobak);
            System.out.println("GamePanel: Synced gerobak reference. Gerobak instance: " + this.gerobak);
            // Debug inventory state
            System.out.println("GamePanel: Current items in gerobak: " +
                    newPlayer.getInventory().getItemDibawa().size());
            for (Item item : newPlayer.getInventory().getItemDibawa()) {
                System.out.println("GamePanel: Item in gerobak: " + item.getNama());
            }
        }

        // Update TokoItem with the new player
        this.tokoItem = new TokoItem(newPlayer);

        System.out.println("GamePanel: Updated player data - Username: " + newPlayer.getUsername() +
                ", Money: " + newPlayer.getMoney() + ", ID: " + newPlayer.getID());
    }

    /**
     * Preload all icons from assets/icons directory
     */
    private static void preloadIcons() {
        System.out.println("Preloading icons...");
        File iconsDir = new File("assets/icons");
        if (!iconsDir.exists() || !iconsDir.isDirectory()) {
            System.err.println("Icons directory not found: " + iconsDir.getAbsolutePath());
            return;
        }

        File[] iconFiles = iconsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        });
        if (iconFiles == null)
            return;

        for (File iconFile : iconFiles) {
            try {
                String filename = iconFile.getName();
                String iconName = filename.substring(0, filename.lastIndexOf('.'));

                // Load original icon
                ImageIcon originalIcon = new ImageIcon(iconFile.getAbsolutePath());
                iconCache.put(iconName, originalIcon);

                // Preload common sizes
                Image img = originalIcon.getImage();
                scaledIconCache.put(iconName + "_32x32",
                        new ImageIcon(img.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
                scaledIconCache.put(iconName + "_40x40",
                        new ImageIcon(img.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
                scaledIconCache.put(iconName + "_36x36",
                        new ImageIcon(img.getScaledInstance(36, 36, Image.SCALE_SMOOTH)));

            } catch (Exception e) {
                System.err.println("Failed to load icon: " + iconFile.getName() + " - " + e.getMessage());
            }
        }
        System.out.println(
                "Icons preloaded: " + iconCache.size() + " icons with " + scaledIconCache.size() + " scaled versions");
    }

    /**
     * Get icon by name with specified size. Returns cached version if available.
     */
    public static ImageIcon getIcon(String iconName, int width, int height) {
        if (iconName == null)
            return null;

        // Clean the icon name (remove .png extension if present)
        if (iconName.endsWith(".png")) {
            iconName = iconName.substring(0, iconName.lastIndexOf('.'));
        }

        String cacheKey = iconName + "_" + width + "x" + height;

        // Return cached scaled version if available
        if (scaledIconCache.containsKey(cacheKey)) {
            return scaledIconCache.get(cacheKey);
        }

        // Get original icon
        ImageIcon originalIcon = iconCache.get(iconName);
        if (originalIcon == null) {
            // Fallback: try to load from file if not in cache
            File iconFile = new File("assets/icons/" + iconName + ".png");
            if (iconFile.exists()) {
                originalIcon = new ImageIcon(iconFile.getAbsolutePath());
                iconCache.put(iconName, originalIcon);
            } else {
                return null;
            }
        }

        // Create and cache scaled version
        Image scaledImg = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);
        scaledIconCache.put(cacheKey, scaledIcon);

        return scaledIcon;
    }

    /**
     * Get icon by name (original size)
     */
    public static ImageIcon getIcon(String iconName) {
        if (iconName == null)
            return null;

        // Clean the icon name
        if (iconName.endsWith(".png")) {
            iconName = iconName.substring(0, iconName.lastIndexOf('.'));
        }

        ImageIcon icon = iconCache.get(iconName);
        if (icon == null) {
            // Fallback: try to load from file
            File iconFile = new File("assets/icons/" + iconName + ".png");
            if (iconFile.exists()) {
                icon = new ImageIcon(iconFile.getAbsolutePath());
                iconCache.put(iconName, icon);
            }
        }
        return icon;
    }

    /**
     * Switch to a different map and move player to specified coordinates
     */
    public void switchToMap(String mapName, int newX, int newY) { // Clear existing map objects and trigger zones
        mapObjectManager.clearObjects();
        triggerZoneManager.clearAllZones();
        // Jangan clear randomTriggerZoneManager jika map2
        // FIX: Jangan clear randomTriggerZoneManager dan jangan reset
        // map2ZonesGenerated saat pindah ke map lain
        // if (!"map2".equals(mapName)) {
        // randomTriggerZoneManager.clearZones();
        // map2ZonesGenerated = false;
        // }

        // Switch the tile map
        String mapPath = "assets/tiles/" + mapName;
        tileManager.switchMap(mapPath);
        currentMap = mapName;

        // Move player to new coordinates
        playerMovement.setX(newX);
        playerMovement.setY(newY);

        // Setup map-specific objects and trigger zones
        setupMapContent(mapName);

        System.out.println("Switched to map: " + mapName + " at coordinates (" + newX + ", " + newY + ")");
    }

    /**
     * Setup map-specific objects and trigger zones
     */
    private void setupMapContent(String mapName) {
        if ("map1".equals(mapName)) {
            setupMap1Content();
        } else if ("map2".equals(mapName)) {
            setupMap2Content();
        }
    }

    /**
     * Setup content for map1 (original map)
     */
    private void setupMap1Content() {
        // Setup trigger zones for map1
        triggerZoneManager.addZone("Supplier", 511, 480, 1054, 575, true, () -> {
            if (showSupplierPanelCallback != null) {
                SwingUtilities.invokeLater(showSupplierPanelCallback);
            }
        });
        triggerZoneManager.addZone("Home", 68, 32, 217, 205, true, () -> {
            if (showHomeBasePanelCallback != null) {
                SwingUtilities.invokeLater(showHomeBasePanelCallback);
            }
        });
        triggerZoneManager.addZone("Toko Item", 511, 288, 1054, 383, true, () -> {
            if (showTokoItemPanelCallback != null) {
                SwingUtilities.invokeLater(showTokoItemPanelCallback);
            }
        });
        triggerZoneManager.addZone("Toko Perks", 511, 96, 1054, 191, true, () -> {
            if (showTokoPerksPanelCallback != null) {
                SwingUtilities.invokeLater(showTokoPerksPanelCallback);
            }
        });
        triggerZoneManager.addZone("Kota Lain", 0, 671, 92, 959, true, () -> {
            // Stop current BGM and play Kota Lain BGM
            BGMPlayer.getInstance().stopBGM();
            BGMPlayer.getInstance().playKotaLainBGM();
            switchToMap("map2", 1445, playerMovement.getY());
        });

        // Setup map objects for map1
        int xshop = 935, yshop = 545;
        mapObjectManager.addObject("assets/sprites/objects/house.png", 80, 30, true);
        mapObjectManager.addObject("assets/sprites/objects/tent.png", 230, 1280, true);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                mapObjectManager.addObject("assets/sprites/objects/shop.png", xshop - 140 * i, yshop - 190 * j, true);
            }
        }
    }

    /**
     * Setup content for map2 (new map)
     */
    private void setupMap2Content() { // Setup trigger zone to return to map1 on the right edge
        triggerZoneManager.addZone("Kembali ke Map1", 1500, 671, 1650, 959, true, () -> {
            // Stop Kota Lain BGM and restore Map BGM
            BGMPlayer.getInstance().stopKotaLainBGM();
            BGMPlayer.getInstance().playMapBGM();
            switchToMap("map1", 92, playerMovement.getY());
        });

        // Generate random trigger zones untuk map2 hanya jika belum pernah atau player
        // baru sleep
        if (player.isHasSlept()) {
            randomTriggerZoneManager.clearZones();
            randomTriggerZoneManager.generateRandomZones(0, 1150, 0, 1450, triggerZoneManager);
            player.setHasSlept(false);
            map2ZonesGenerated = true;
        } else if (!map2ZonesGenerated) {
            randomTriggerZoneManager.generateRandomZones(0, 1150, 0, 1450, triggerZoneManager);
            map2ZonesGenerated = true;
        } else {
            // Jika sudah pernah, cukup re-register zona ke triggerZoneManager
            randomTriggerZoneManager.registerZonesToTriggerZoneManager(triggerZoneManager);
        }
        System.out.println("Map2 setup complete with " + randomTriggerZoneManager.getZoneCount() + " random zones");

        // Jampi: aktif otomatis jika ada di gerobak
        if (player != null && player.getInventory() != null) {
            if (itemEffectManager == null) {
                itemEffectManager = new ItemEffectManager(player);
            }
            if (itemEffectManager.isItemActive("Jampi")) {
                int before = player.getMoney();
                itemEffectManager.activateItem("Jampi");
                JOptionPane.showMessageDialog(this, "Jampi aktif! Penghasilan hari ini akan dilipatgandakan.", "Jampi",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Advance the day, increment currentDay, and reset daily item effects
     */
    public void advanceDay() {
        currentDay++;
        if (player != null) {
            if (itemEffectManager == null) {
                itemEffectManager = new ItemEffectManager(player);
            }
            itemEffectManager.resetDailyEffects();
        }
    }

    /**
     * Optionally, allow setting the current day (e.g., after loading a save)
     */
    public void setCurrentDay(int day) {
        this.currentDay = day;
    }
    public int getCurrentDay() {
        return currentDay;
    }
}
