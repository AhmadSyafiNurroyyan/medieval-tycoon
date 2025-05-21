package debugger;

import java.awt.event.MouseEvent;

public class DebugClickLogger {
    public static void logClickCoordinates(MouseEvent e, int mapX, int mapY) {
        System.out.println("[DEBUG] Clicked map at: (" + mapX + ", " + mapY + ")");
    }
}
