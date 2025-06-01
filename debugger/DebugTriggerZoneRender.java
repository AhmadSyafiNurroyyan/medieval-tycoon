package debugger;

import MapManager.TriggerZoneManager;
import camera.Camera;
import java.awt.*;


public class DebugTriggerZoneRender {
    public static void drawAllZones(Graphics g, TriggerZoneManager triggerZoneManager, Camera camera) {
        Graphics2D g2d = (Graphics2D) g;
        for (TriggerZoneManager.TriggerZone zone : triggerZoneManager.getAllZones()) {
            Rectangle r = zone.getBounds();
            g2d.setColor(new Color(255, 0, 0, 80));
            g2d.fillRect(r.x - camera.getX(), r.y - camera.getY(), r.width, r.height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(r.x - camera.getX(), r.y - camera.getY(), r.width, r.height);
            g2d.setColor(Color.WHITE);
            g2d.drawString(zone.getId(), r.x - camera.getX() + 4, r.y - camera.getY() + 16);
        }
    }
    
    /**
     * Draw only non-random trigger zones (skip zones with IDs starting with "ArenaPembeli_")
     */
    public static void drawNonRandomZones(Graphics g, TriggerZoneManager triggerZoneManager, Camera camera) {
        Graphics2D g2d = (Graphics2D) g;
        for (TriggerZoneManager.TriggerZone zone : triggerZoneManager.getAllZones()) {
            // Skip random trigger zones (they will be rendered as NPCs instead)
            if (zone.getId().startsWith("ArenaPembeli_")) {
                continue;
            }
            
            Rectangle r = zone.getBounds();
            g2d.setColor(new Color(255, 0, 0, 80));
            g2d.fillRect(r.x - camera.getX(), r.y - camera.getY(), r.width, r.height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(r.x - camera.getX(), r.y - camera.getY(), r.width, r.height);
            g2d.setColor(Color.WHITE);
            g2d.drawString(zone.getId(), r.x - camera.getX() + 4, r.y - camera.getY() + 16);
        }
    }
}
