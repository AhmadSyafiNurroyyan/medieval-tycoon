package tiles;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class TriggerZoneManager {
    public static class TriggerZone {
        private final String id;
        private final Rectangle bounds;

        public TriggerZone(String id, int x, int y, int width, int height) {
            this.id = id;
            this.bounds = new Rectangle(x, y, width, height);
        }

        // Constructor using rectangle sides
        public TriggerZone(String id, int leftX, int topY, int rightX, int bottomY, boolean useSides) {
            this.id = id;
            int width = rightX - leftX;
            int height = bottomY - topY;
            this.bounds = new Rectangle(leftX, topY, width, height);
        }

        public String getId() {
            return id;
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public boolean contains(int px, int py) {
            return bounds.contains(px, py);
        }
    }

    private final List<TriggerZone> zones = new ArrayList<>();

    public void addZone(String id, int x, int y, int width, int height) {
        zones.add(new TriggerZone(id, x, y, width, height));
    }

    public void addZone(String id, int leftX, int topY, int rightX, int bottomY, boolean useSides) {
        zones.add(new TriggerZone(id, leftX, topY, rightX, bottomY, useSides));
    }

    public void removeZoneById(String id) {
        zones.removeIf(zone -> zone.getId().equals(id));
    }

    public TriggerZone getZoneById(String id) {
        for (TriggerZone zone : zones) {
            if (zone.getId().equals(id)) {
                return zone;
            }
        }
        return null;
    }

    public List<TriggerZone> getZonesAt(int px, int py) {
        List<TriggerZone> result = new ArrayList<>();
        for (TriggerZone zone : zones) {
            if (zone.contains(px, py)) {
                result.add(zone);
            }
        }
        return result;
    }

    public List<TriggerZone> getAllZones() {
        return new ArrayList<>(zones);
    }
}
