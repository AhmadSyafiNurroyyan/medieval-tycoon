/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package MapManager;

import gui.GamePanel;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class TileManager {
    private GamePanel gamePanel;
    private Tile[] tiles;
    private int[][] map;
    private final int tileSize = 32; 
    private String currentMapPath = "assets/tiles/map1"; 

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tiles = new Tile[10]; 
        loadTiles();
        loadMap("assets/tiles/map1");
    }

    private void loadTiles() {
        try {
            for (int i = 0; i <= tiles.length; i++) {
                File file = new File("assets/tiles/" + i + ".png");
                if (file.exists()) {
                    BufferedImage image = ImageIO.read(file);
                    boolean collision = (i == 2 || i == 3);
                    tiles[i] = new Tile(image, collision);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMap(String mapPath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(mapPath));
            String line;
            int rows = 0;
            int cols = 0;

            while ((line = br.readLine()) != null) {
                String[] values = line.trim().split(" ");
                if (cols == 0) cols = values.length;
                rows++;
            }
            br.close();
            map = new int[rows][cols];
 
            br = new BufferedReader(new FileReader(mapPath));
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.trim().split(" ");
                for (int col = 0; col < cols; col++) {
                    map[row][col] = Integer.parseInt(values[col]);
                }
                row++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tileNum = map[row][col];
                int x = col * tileSize - camX;
                int y = row * tileSize - camY;
                if (tileNum >= 0 && tileNum < tiles.length && tiles[tileNum] != null) {
                    g2.drawImage(tiles[tileNum].getImage(), x, y, tileSize, tileSize, null);
                }
            }
        }
    }

    public boolean isSolid(int worldX, int worldY) {
        int col = worldX / tileSize;
        int row = worldY / tileSize;
        if (col < 0 || col >= map[0].length || row < 0 || row >= map.length) {
            return true;
        }
        int tileNum = map[row][col];
        return tileNum >= 0 && tileNum < tiles.length && tiles[tileNum] != null && tiles[tileNum].hasCollision();
    }

    public String getCurrentMapPath() {
        return currentMapPath;
    }

    public void switchMap(String newMapPath) {
        currentMapPath = newMapPath;
        loadMap(newMapPath);
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getMapWidth() {
        return map[0].length;
    }
    public int getMapHeight() {
        return map.length;
    }
}
