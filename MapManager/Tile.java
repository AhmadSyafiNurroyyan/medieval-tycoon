/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package MapManager;

import java.awt.image.BufferedImage;

public class Tile {
    private BufferedImage image;
    private boolean collision;

    public Tile(BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean hasCollision() {
        return collision;
    }
}
