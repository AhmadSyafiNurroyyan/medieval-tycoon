package model;

import enums.PerkType;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class Player {

    private String username;
    private int ID;
    private int level;
    private int money;
    private Inventory inventory;
    private Gerobak gerobak;
    private List<Perk> semuaPerkDimiliki = new ArrayList<>();
    private List<Perk> perkDipilihUntukJualan = new ArrayList<>();

    public Player() {
    }

    public Player(String username) {
        this.username = username;
        this.ID = (int) (Math.random() * 9999999 + 80000000);
        this.level = 1;
        this.money = 10000000;
        this.inventory = new Inventory();
        this.gerobak = new Gerobak(); // Initialize gerobak

        List<Perk> pilihanPerk = new ArrayList<>();
        pilihanPerk.add(new PerksElegan());
        pilihanPerk.add(new PerksCharming());
        pilihanPerk.add(new PerksActive());

        Perk perkGratis = pilihanPerk.get(new Random().nextInt(pilihanPerk.size()));
        semuaPerkDimiliki.add(perkGratis);
        System.out.println("Perk gratis diberikan: " + perkGratis.getName());
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

    public void tambahMoney(int jumlah) {
        this.money += jumlah;
    }

    public void kurangiMoney(int jumlah) {
        this.money -= jumlah;
    }

    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Set the player's inventory
     * 
     * @param inventory The new inventory
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public List<Perk> getSemuaPerkDimiliki() {
        return semuaPerkDimiliki;
    }

    public void addPerk(Perk perk) {
        // Jangan replace perk yang sudah ada, hanya tambah yang baru
        if (!hasPerk(perk.getPerkType()) && semuaPerkDimiliki.size() < 2) {
            semuaPerkDimiliki.add(perk);
            System.out.println("Perk " + perk.getName() + " berhasil ditambahkan.");
        }
    }

    public void removePerk(Perk perk) {
        semuaPerkDimiliki.remove(perk);
        perkDipilihUntukJualan.remove(perk);
    }

    public List<Perk> getPerkDipilihUntukJualan() {
        return perkDipilihUntukJualan;
    }

    public boolean pilihPerkUntukJualan(Perk perk) {
        if (perk == null) {
            throw new IllegalArgumentException("Perk tidak boleh null.");
        }

        if (!semuaPerkDimiliki.contains(perk)) {
            System.out.println("Perk tidak dimiliki.");
            return false;
        }

        if (perkDipilihUntukJualan.contains(perk)) {
            System.out.println("Perk sudah dipilih.");
            return false;
        }

        // Pastikan gerobak sudah diinisialisasi
        if (gerobak == null) {
            System.out.println("Gerobak belum diinisialisasi.");
            return false;
        }

        if (perkDipilihUntukJualan.size() >= gerobak.getKapasitasPerks()) {
            System.out.println("Maksimal " + gerobak.getKapasitasPerks() + " perk saja boleh dibawa saat jualan.");
            return false;
        }

        perkDipilihUntukJualan.add(perk);
        perk.activate();
        System.out.println("Perk dipilih untuk jualan: " + perk.getName());
        return true;
    }

    public void resetPerkUntukJualan() {
        for (Perk perk : perkDipilihUntukJualan) {
            perk.deactivate();
        }
        perkDipilihUntukJualan.clear();
        System.out.println("Perk untuk jualan telah direset.");
    }

    public boolean hasPerk(PerkType type) {
        for (Perk p : semuaPerkDimiliki) {
            if (p.getPerkType() == type) {
                return true;
            }
        }
        return false;
    }

    public class PlayerMovement {

        private int x = 885, y = 729;
        private final int speed = 5;
        private boolean up, down, left, right;
        private Image[][] sprites = new Image[4][4];
        private int direction = 0, frameIndex = 0, animDelay = 5, animCount = 0;
        private final int spriteWidth = 32;
        private final int spriteHeight = 32;

        public PlayerMovement() {
            // ../assets/sprites/dir{dir}_{frame}.png
            for (int d = 0; d < sprites.length; d++) {
                for (int f = 0; f < sprites[d].length; f++) {
                    sprites[d][f] = new ImageIcon(
                            getClass().getResource("../assets/sprites/dir" + d + "_" + f + ".png")).getImage();
                }
            }
        }

        // public void update() {
        // boolean moving = false;
        // if (up) { y -= speed; direction = 3; moving = true; }
        // if (down) { y += speed; direction = 0; moving = true; }
        // if (left) { x -= speed; direction = 1; moving = true; }
        // if (right) { x += speed; direction = 2; moving = true; }
        // if (moving) {
        // gui.DebugCoordinateLogger.logPlayerCoordinates(this);
        // animCount++;
        // if (animCount >= animDelay) {
        // animCount = 0;
        // frameIndex = (frameIndex + 1) % sprites[direction].length;
        // }
        // } else {
        // frameIndex = 0;
        // }
        // }
        public void update(int mapWidth, int mapHeight, int tileSize, MapManager.MapObjectManager mapObjectManager,
                MapManager.TileManager tileManager) {
            boolean moving = false;
            int nextX = x, nextY = y;
            if (up) {
                nextY -= speed;
            }
            if (down) {
                nextY += speed;
            }
            if (left) {
                nextX -= speed;
            }
            if (right) {
                nextX += speed;
            }

            int halfW = spriteWidth / 2;
            int halfH = spriteHeight / 2;
            int size = spriteWidth;
            int offset = 32;
            int[][] corners = {
                    { nextX - halfW, nextY - halfH }, // top-left
                    { nextX - halfW + size - 1, nextY - halfH }, // top-right
                    { nextX - halfW, nextY - halfH + size - 1 + (down ? offset : 0) }, // bottom-left
                    { nextX - halfW + size - 1 + (right ? offset : 0), nextY - halfH + size - 1 + (down ? offset : 0) } // bottom-right
            };

            boolean blockedByMap = false;
            int col = (nextX - halfW) / tileSize;
            int row = (nextY - halfH) / tileSize;
            if (col < 0 || col >= mapWidth || row < 0 || row >= mapHeight) {
                blockedByMap = true;
            }

            boolean blockedBySolidTile = false;
            if (tileManager != null) {
                for (int[] c : corners) {
                    if (tileManager.isSolid(c[0], c[1])) {
                        blockedBySolidTile = true;
                        break;
                    }
                }
            }

            boolean blockedByObject = false;
            if (mapObjectManager != null) {
                for (int[] c : corners) {
                    if (mapObjectManager.isSolid(c[0], c[1])) {
                        blockedByObject = true;
                        break;
                    }
                }
            }

            if (!blockedByMap && !blockedBySolidTile && !blockedByObject) {
                if (up) {
                    y -= speed;
                    direction = 3;
                    moving = true;
                }
                if (down) {
                    y += speed;
                    direction = 0;
                    moving = true;
                }
                if (left) {
                    x -= speed;
                    direction = 1;
                    moving = true;
                }
                if (right) {
                    x += speed;
                    direction = 2;
                    moving = true;
                }
            }

            int min = 0 + halfW;
            int maxX = mapWidth * tileSize - halfW;
            int maxY = mapHeight * tileSize - halfH;
            if (x < min) {
                x = min;
            }
            if (y < min) {
                y = min;
            }
            if (x > maxX) {
                x = maxX;
            }
            if (y > maxY) {
                y = maxY;
            }

            if (moving) {
                // debugger.DebugCoordinateLogger.logPlayerCoordinates(this);
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
                case KeyEvent.VK_W ->
                    up = true;
                case KeyEvent.VK_S ->
                    down = true;
                case KeyEvent.VK_A ->
                    left = true;
                case KeyEvent.VK_D ->
                    right = true;
            }
        }

        public void keyReleased(int keyCode) {
            switch (keyCode) {
                case KeyEvent.VK_W ->
                    up = false;
                case KeyEvent.VK_S ->
                    down = false;
                case KeyEvent.VK_A ->
                    left = false;
                case KeyEvent.VK_D ->
                    right = false;
            }
        }

        public void resetKeys() {
            up = down = left = right = false;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getSpriteWidth() {
            return spriteWidth;
        }

        public int getSpriteHeight() {
            return spriteHeight;
        }

        public Image getCurrentFrame() {
            return sprites[direction][frameIndex];
        }
    }

    public class PlayerSkin implements ImageObserver {

        private Font nametagFont;

        public PlayerSkin() {
            nametagFont = new Font("Arial", Font.BOLD, 14);
        }

        public void render(Graphics g, int x, int y, Image playerSkin) {

            if (username != null && !username.isEmpty()) {
                g.drawImage(
                        playerSkin,
                        x, y,
                        this);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setFont(nametagFont);

                FontMetrics metrics = g2d.getFontMetrics(nametagFont);
                int nameWidth = metrics.stringWidth(username);

                int nameX = x + 32 - (nameWidth / 2);
                int nameY = y - 10;

                g2d.setColor(new Color(0, 0, 0, 128));
                g2d.fillRoundRect(nameX - 4, nameY - metrics.getHeight() + 4, nameWidth + 8, metrics.getHeight() + 2, 5,
                        5);

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
