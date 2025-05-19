package gui;

import camera.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.*;
import model.Player.PlayerMovement;
import model.Player.PlayerSkin;
import tiles.TileManager;

public class GamePanel extends JPanel implements Runnable {
    private Player player = new Player("tauwus");
    private PlayerMovement playerMovement;
    private PlayerSkin PlayerSkin;
    private Thread gameThread;
    private int FPS = 60;
    private TileManager tileManager;
    private Camera camera;

    
    
    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        playerMovement = player.createMovement();
        PlayerSkin = player.createNametag();
        tileManager = new TileManager(this);
        camera = new Camera(this, tileManager);

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                playerMovement.keyPressed(e.getKeyCode());
            }
            @Override public void keyReleased(KeyEvent e) {
                playerMovement.keyReleased(e.getKeyCode());
            }
        });
        
        // no stuck keys
        addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                playerMovement.resetKeys();
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
        // Stop the game thread when panel is removed
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
        
        while (gameThread != null) {
            // Use new update method with map boundaries
            playerMovement.update(
                tileManager.getMapWidth(),
                tileManager.getMapHeight(),
                tileManager.getTileSize()
            );
            repaint();
            
            try {
                double remaining = nextDrawTime - System.nanoTime();
                long sleepMs = Math.max(0, (long)(remaining / 1_000_000));
                
                if (sleepMs > 0) {
                    Thread.sleep(sleepMs);
                }
                
                nextDrawTime += drawInterval;
            } catch (InterruptedException ex) {
                System.out.println("Game thread was interrupted: " + ex.getMessage());
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
        PlayerSkin.render(g, playerMovement.getX() - camera.getX(), playerMovement.getY() - camera.getY(), playerMovement.getCurrentFrame());

        // Draw money info (Gold) at upper right
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

        // Draw UID at lower right
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
}
