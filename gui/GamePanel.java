package gui;

import camera.*;
import debugger.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import model.*;
import model.Player.PlayerMovement;
import model.Player.PlayerSkin;
import tiles.MapObjectManager;
import tiles.TileManager;
import tiles.TriggerZoneManager;
//import debugger.DebugCoordinateLogger;

public class GamePanel extends JPanel implements Runnable {
    private Player player = new Player("tauwus");
    private PlayerMovement playerMovement;
    private PlayerSkin PlayerSkin;
    MapObjectManager mapObjectManager = new MapObjectManager();
    private Thread gameThread;
    private int FPS = 60;
    private TileManager tileManager;
    private Camera camera;
    private TriggerZoneManager triggerZoneManager;
    private Supplier supplier; 
    private Runnable showSupplierPanelCallback;

    
    
    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        playerMovement = player.createMovement();
        PlayerSkin = player.createNametag();
        tileManager = new TileManager(this);
        camera = new Camera(this, tileManager);
        triggerZoneManager = new TriggerZoneManager();
        supplier = new Supplier(); 
        triggerZoneManager.addZone("supplier", 511, 480, 1054, 575, true, () -> {
            if (showSupplierPanelCallback != null) {
                SwingUtilities.invokeLater(showSupplierPanelCallback);
            }
        });
        triggerZoneManager.addZone("home", 68, 32, 217, 205, true, () -> {
            System.out.println("Home triggered");
        });
        

        // mapObjectManager.addObject("assets/sprites/objects/tree.png", 128, 96);
        // mapObjectManager.addObject("assets/sprites/objects/rock.png", 200, 150); 
        int xshop = 935, yshop = 545;
        mapObjectManager.addObject("assets/sprites/objects/house.png", 80, 30, true);
        mapObjectManager.addObject("assets/sprites/objects/shop.png", xshop, yshop, true);
        mapObjectManager.addObject("assets/sprites/objects/shop.png", xshop-140, yshop, true);
        mapObjectManager.addObject("assets/sprites/objects/shop.png", xshop-140-140, yshop, true);
        mapObjectManager.addObject("assets/sprites/objects/shop.png", xshop-140-140-140, yshop, true);
        mapObjectManager.addObject("assets/sprites/objects/tent.png", 230, 1280, true);

        addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                playerMovement.keyPressed(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_E) {
                    List<tiles.TriggerZoneManager.TriggerZone> zones = triggerZoneManager.getZonesAt(playerMovement.getX(), playerMovement.getY());
                    for (tiles.TriggerZoneManager.TriggerZone zone : zones) {
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
            @Override public void focusLost(FocusEvent e) {
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
            playerMovement.update(tileManager.getMapWidth(), tileManager.getMapHeight(), tileManager.getTileSize(), mapObjectManager, tileManager);

            // boolean inZone = !triggerZoneManager.getZonesAt(playerMovement.getX(), playerMovement.getY()).isEmpty();
            // if (inZone && !wasInZone) {
            //     System.out.println("[DEBUG] Entered trigger zone");
            // } else if (!inZone && wasInZone) {
            //     System.out.println("[DEBUG] Exited trigger zone");
            // }
            // wasInZone = inZone;
            repaint();
            try {
                double remaining = nextDrawTime - System.nanoTime();
                long sleepMs = Math.max(0, (long)(remaining / 1_000_000));
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
        g2d.setColor(new Color(0,0,0,150));
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
        g2d.setColor(new Color(0,0,0,150));
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
    public Supplier getSupplier() {
        return supplier;
    }
}

