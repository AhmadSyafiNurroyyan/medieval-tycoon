package model;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import javax.swing.*;

public class Player {
    private String username;
    private int ID;
    private int level;
    private int money;
    
    public Player() {}
    
    public Player(String username) {
        this.username = username;
        this.ID = (int) (Math.random() * 9999999 + 80000000);
        this.level = 1;
        this.money = 100;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMoney() {
        return money;
    }
    
    public class PlayerMovement {
        private int x = 64, y = 64;
        private final int speed = 5;
        private boolean up, down, left, right;
        private final Image[][] sprites = new Image[4][4];
        private int direction = 0, frameIndex = 0, animDelay = 5, animCount = 0;

        public PlayerMovement() {
            // ../assets/sprites/dir{dir}_{frame}.png
            for (int d = 0; d < sprites.length; d++) {
                for (int f = 0; f < sprites[d].length; f++) {
                    sprites[d][f] = new ImageIcon(
                        getClass().getResource("../assets/sprites/dir" + d + "_" + f + ".png")
                    ).getImage();
                }
            }
        }

        public void update() {
            boolean moving = false;
            if (up)    { y -= speed; direction = 3; moving = true; }
            if (down)  { y += speed; direction = 0; moving = true; }
            if (left)  { x -= speed; direction = 1; moving = true; }
            if (right) { x += speed; direction = 2; moving = true; }

            if (moving) {
                animCount++;
                if (animCount >= animDelay) {
                    animCount = 0;
                    frameIndex = (frameIndex + 1) % sprites[direction].length;
                }
            } else {
                frameIndex = 0;
            }
        }

        // New update method for collision with map boundaries
        public void update(int mapWidth, int mapHeight, int tileSize) {
            boolean moving = false;
            if (up)    { y -= speed; direction = 3; moving = true; }
            if (down)  { y += speed; direction = 0; moving = true; }
            if (left)  { x -= speed; direction = 1; moving = true; }
            if (right) { x += speed; direction = 2; moving = true; }

            // Clamp to map boundaries
            int min = 0;
            int maxX = mapWidth * tileSize - tileSize;
            int maxY = mapHeight * tileSize - tileSize;
            if (x < min) x = min;
            if (y < min) y = min;
            if (x > maxX) x = maxX;
            if (y > maxY) y = maxY;

            if (moving) {
                animCount++;
                if (animCount >= animDelay) {
                    animCount = 0;
                    frameIndex = (frameIndex + 1) % sprites[direction].length;
                }
            } else {
                frameIndex = 0;
            }
        }

        public void keyPressed(int keyCode) {
            switch (keyCode) {
                case KeyEvent.VK_W: up = true; break;
                case KeyEvent.VK_S: down = true; break;
                case KeyEvent.VK_A: left = true; break;
                case KeyEvent.VK_D: right = true; break;
            }
        }

        public void keyReleased(int keyCode) {
            switch (keyCode) {
                case KeyEvent.VK_W: up = false; break;
                case KeyEvent.VK_S: down = false; break;
                case KeyEvent.VK_A: left = false; break;
                case KeyEvent.VK_D: right = false; break;
            }
        }
        public void resetKeys() {
            up = down = left = right = false;
        }
        public int getX() { return x; }
        public int getY() { return y; }
        public Image getCurrentFrame() { return sprites[direction][frameIndex]; }
    }

    public class PlayerSkin implements ImageObserver{
        private Font nametagFont;
        
        public PlayerSkin() {
            nametagFont = new Font("Arial", Font.BOLD, 14);
        }
        
        public void render(Graphics g, int x, int y, Image playerSkin) {

            if (username != null && !username.isEmpty()) {
                g.drawImage(
                    playerSkin,
                    x,y,
                    this
                );
                Graphics2D g2d = (Graphics2D) g;
                g2d.setFont(nametagFont);
                
                FontMetrics metrics = g2d.getFontMetrics(nametagFont);
                int nameWidth = metrics.stringWidth(username);
                
                int nameX = x + 32 - (nameWidth / 2);
                int nameY = y - 10;
                
                g2d.setColor(new Color(0, 0, 0, 128));
                g2d.fillRoundRect(nameX - 4, nameY - metrics.getHeight() + 4, nameWidth + 8, metrics.getHeight() + 2, 5, 5);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(username, nameX, nameY);
            }
        }
        
        public void setFont(Font font) {
            this.nametagFont = font;
        }

        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'imageUpdate'");
        }
    }
    
    public PlayerSkin createNametag() {
        return new PlayerSkin();
    }

    public PlayerMovement createMovement() {
        return new PlayerMovement();
    }
}