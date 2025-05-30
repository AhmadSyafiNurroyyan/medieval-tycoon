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
    private DialogSystem dialogSystem;
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

    // Icon preloading system
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();
    private static final Map<String, ImageIcon> scaledIconCache = new HashMap<>();
    private static boolean iconsLoaded = false;

    public GamePanel(Player player) {
        this.player = player;
        this.gerobak = new Gerobak();
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
        randomTriggerZoneManager = new RandomTriggerZoneManager();
        dialogSystem = new DialogSystem(this);
        randomTriggerZoneManager.setDialogSystem(dialogSystem);
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
                    if (dialogSystem != null && dialogSystem.isDialogVisible()) {
                        // Close dialog if open
                        dialogSystem.hideDialog();
                        return;
                    }

                    // Otherwise, check for trigger zones
                    List<MapManager.TriggerZoneManager.TriggerZone> zones = triggerZoneManager
                            .getZonesAt(playerMovement.getX(), playerMovement.getY());
                    for (MapManager.TriggerZoneManager.TriggerZone zone : zones) {
                        zone.trigger();
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
        randomTriggerZoneManager.clearZones();

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

        // Generate random trigger zones for map2
        // Constraint: x between 0-1150, y between 0-1450
        randomTriggerZoneManager.generateRandomZones(0, 1150, 0, 1450, triggerZoneManager);

        System.out.println("Map2 setup complete with " + randomTriggerZoneManager.getZoneCount() + " random zones");
    }
}
