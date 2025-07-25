/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

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
    
    public static void drawNonRandomZones(Graphics g, TriggerZoneManager triggerZoneManager, Camera camera) {
        Graphics2D g2d = (Graphics2D) g;
        for (TriggerZoneManager.TriggerZone zone : triggerZoneManager.getAllZones()) {
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
