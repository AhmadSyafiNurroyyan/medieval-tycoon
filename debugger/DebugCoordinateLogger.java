package debugger;

import model.Player.PlayerMovement;

public class DebugCoordinateLogger {
    public static void logPlayerCoordinates(PlayerMovement playerMovement) {
        System.out.println("[DEBUG] Player Coords: (" + playerMovement.getX() + ", " + playerMovement.getY() + ")");
    }
}
