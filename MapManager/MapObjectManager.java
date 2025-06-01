/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package MapManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Manages objects (images) placed on the map at specific coordinates.
 */
public class MapObjectManager {
    private final List<MapObject> objects = new ArrayList<>();
      private static class MapObject {
        BufferedImage image;
        int x, y;
        boolean collision;
        MapObject(BufferedImage image, int x, int y, boolean collision) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.collision = collision;
        }
    }
    public void addObject(String imagePath, int x, int y, boolean collision) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            objects.add(new MapObject(img, x, y, collision));
        } catch (IOException e) {
            System.err.println("Failed to load image: " + imagePath);
        }
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        for (MapObject obj : objects) {
            g2.drawImage(obj.image, obj.x - camX, obj.y - camY, null);
        }
    }

    public boolean isSolid(int worldX, int worldY) {
        for (MapObject obj : objects) {
            if (obj.collision) {
                int objWidth = obj.image.getWidth();
                int objHeight = obj.image.getHeight();
                if (worldX >= obj.x && worldX < obj.x + objWidth && worldY >= obj.y && worldY < obj.y + objHeight) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Clear all objects from the map
     */
    public void clearObjects() {
        objects.clear();
    }

    /**
     * Get the number of objects currently on the map
     */
    public int getObjectCount() {
        return objects.size();
    }
}
