package camera;

import gui.GamePanel;
import model.Player.PlayerMovement;
import tiles.TileManager;

public class Camera {
    private int x, y; // top-left corner of camera in world coordinates
    private int viewportWidth, viewportHeight;
    private int worldWidth, worldHeight;

    public Camera(GamePanel gamePanel, TileManager tileManager) {
        this.viewportWidth = gamePanel.getWidth();
        this.viewportHeight = gamePanel.getHeight();
        this.worldWidth = tileManager.getMapWidth() * tileManager.getTileSize();
        this.worldHeight = tileManager.getMapHeight() * tileManager.getTileSize();
    }

    public void update(PlayerMovement player, TileManager tileManager, GamePanel gamePanel) {
        viewportWidth = gamePanel.getWidth();
        viewportHeight = gamePanel.getHeight();
        worldWidth = tileManager.getMapWidth() * tileManager.getTileSize();
        worldHeight = tileManager.getMapHeight() * tileManager.getTileSize();

        // Center camera on player
        x = player.getX() - viewportWidth / 2 + 16; // 16 = half tile size (32)
        y = player.getY() - viewportHeight / 2 + 16;

        // Clamp camera to world boundaries
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > worldWidth - viewportWidth) x = worldWidth - viewportWidth;
        if (y > worldHeight - viewportHeight) y = worldHeight - viewportHeight;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
