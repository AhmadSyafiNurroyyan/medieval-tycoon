package MapManager;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages random trigger zones with non-overlapping placement
 */
public class RandomTriggerZoneManager {    private static final int MIN_ZONES = 3;
    private static final int MAX_ZONES = 8;
    private static final int ZONE_WIDTH = 192;
    private static final int ZONE_HEIGHT = 192;
    private static final int MIN_SPACING = 20; // Minimum spacing between zones
    
    private final List<Rectangle> placedZones;
      public RandomTriggerZoneManager() {
        this.placedZones = new ArrayList<>();
    }
    
    /**
     * Generate random trigger zones within the specified bounds
     * @param minX minimum X coordinate
     * @param maxX maximum X coordinate
     * @param minY minimum Y coordinate
     * @param maxY maximum Y coordinate
     * @param triggerZoneManager the TriggerZoneManager to add zones to
     */
    public void generateRandomZones(int minX, int maxX, int minY, int maxY, TriggerZoneManager triggerZoneManager) {
        placedZones.clear();
          // Generate random number of zones
        int numZones = (int)(Math.random() * (MAX_ZONES - MIN_ZONES + 1)) + MIN_ZONES;
        System.out.println("Generating " + numZones + " random trigger zones...");
        
        int attemptsPerZone = 50; // Maximum attempts to place each zone
        int successfullyPlaced = 0;
        
        for (int i = 0; i < numZones; i++) {
            boolean placed = false;            for (int attempt = 0; attempt < attemptsPerZone && !placed; attempt++) {
                // Use fixed zone dimensions (192x192)
                int width = ZONE_WIDTH;
                int height = ZONE_HEIGHT;
                  // Generate random position ensuring zone fits within bounds
                int x = (int)(Math.random() * (maxX - minX - width + 1)) + minX;
                int y = (int)(Math.random() * (maxY - minY - height + 1)) + minY;
                
                Rectangle newZone = new Rectangle(x, y, width, height);
                
                // Check if this zone overlaps with any existing zones
                if (!overlapsWithExistingZones(newZone)) {
                    placedZones.add(newZone);
                      // Add to TriggerZoneManager with unique ID
                    String zoneId = "RandomZone_" + (successfullyPlaced + 1);
                    triggerZoneManager.addZone(zoneId, x, y, x + width, y + height, true, () -> {
                        handleRandomZoneTriggered(zoneId, x, y, width, height);
                    });
                    
                    placed = true;
                    successfullyPlaced++;
                    System.out.println("Placed zone " + zoneId + " at (" + x + ", " + y + ") size: " + width + "x" + height);
                }
            }
            
            if (!placed) {
                System.out.println("Failed to place zone " + (i + 1) + " after " + attemptsPerZone + " attempts");
            }
        }
        
        System.out.println("Successfully placed " + successfullyPlaced + " out of " + numZones + " random zones");
    }
    
    /**
     * Check if a new zone overlaps with any existing zones (including spacing)
     */
    private boolean overlapsWithExistingZones(Rectangle newZone) {
        for (Rectangle existingZone : placedZones) {
            // Create expanded rectangle that includes minimum spacing
            Rectangle expandedExisting = new Rectangle(
                existingZone.x - MIN_SPACING,
                existingZone.y - MIN_SPACING,
                existingZone.width + 2 * MIN_SPACING,
                existingZone.height + 2 * MIN_SPACING
            );
            
            if (expandedExisting.intersects(newZone)) {
                return true;
            }
        }
        return false;
    }
      /**
     * Handle when a random zone is triggered
     */
    private void handleRandomZoneTriggered(String zoneId, int x, int y, int width, int height) {
        // Generate random effects when zone is triggered
        String[] messages = {
            "Kamu menemukan koin emas!",
            "Area kosong yang misterius...",
            "Tempat yang tenang untuk beristirahat",
            "Daerah yang tampak tidak biasa",
            "Kamu merasakan energi aneh di sini",
            "Tempat yang sempurna untuk berdagang",
            "Area yang penuh dengan kemungkinan"
        };
          String randomMessage = messages[(int)(Math.random() * messages.length)];
        System.out.println("[" + zoneId + "] " + randomMessage + " at (" + x + ", " + y + ") size: " + width + "x" + height);
        
        // Optional: You can add more effects here like:
        // - Give player random items
        // - Add money
        // - Trigger special events
        // - Play sound effects
    }
    
    /**
     * Get list of all placed zone rectangles (for debugging)
     */
    public List<Rectangle> getPlacedZones() {
        return new ArrayList<>(placedZones);
    }
    
    /**
     * Clear all placed zones
     */
    public void clearZones() {
        placedZones.clear();
    }
    
    /**
     * Get the number of successfully placed zones
     */
    public int getZoneCount() {
        return placedZones.size();
    }
}
