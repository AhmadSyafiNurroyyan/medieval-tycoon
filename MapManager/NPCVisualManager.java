package MapManager;

import camera.Camera;
import model.Pembeli;
import model.PembeliTajir;
import model.PembeliStandar;
import model.PembeliMiskin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages visual representations of NPCs for trigger zones
 * Preloads sprites and handles rendering with nametags
 */
public class NPCVisualManager {
    
    // Preloaded NPC sprites
    private BufferedImage pembeli_tajir_sprite;
    private BufferedImage pembeli_standar_sprite;
    private BufferedImage pembeli_miskin_sprite;
    
    // Map zone ID to NPC visual data
    private final Map<String, NPCVisual> npcVisuals = new HashMap<>();
    
    // Font for nametags
    private Font nametagFont;
    
    public NPCVisualManager() {
        preloadSprites();
        initializeFont();
    }
    
    /**
     * Preload all NPC sprites from assets folder
     */
    private void preloadSprites() {
        try {
            pembeli_tajir_sprite = ImageIO.read(new File("assets/sprites/npcs/pembeli_tajir.png"));
            pembeli_standar_sprite = ImageIO.read(new File("assets/sprites/npcs/pembeli_standar.png"));
            pembeli_miskin_sprite = ImageIO.read(new File("assets/sprites/npcs/pembeli_miskin.png"));
            
            System.out.println("[NPCVisualManager] Successfully preloaded NPC sprites");
        } catch (IOException e) {
            System.err.println("[NPCVisualManager] Failed to preload NPC sprites: " + e.getMessage());
            // Create fallback colored rectangles if sprites fail to load
            createFallbackSprites();
        }
    }
    
    /**
     * Create simple colored rectangles as fallback sprites
     */
    private void createFallbackSprites() {
        pembeli_tajir_sprite = createFallbackSprite(Color.YELLOW, "Tajir");
        pembeli_standar_sprite = createFallbackSprite(Color.BLUE, "Standar");
        pembeli_miskin_sprite = createFallbackSprite(Color.GRAY, "Miskin");
        System.out.println("[NPCVisualManager] Created fallback sprites");
    }
    
    /**
     * Create a simple colored rectangle sprite with text as fallback
     */
    private BufferedImage createFallbackSprite(Color color, String text) {
        BufferedImage fallback = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = fallback.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw colored circle
        g.setColor(color);
        g.fillOval(8, 8, 48, 48);
        g.setColor(Color.BLACK);
        g.drawOval(8, 8, 48, 48);
        
        // Draw text
        g.setFont(new Font("Arial", Font.BOLD, 8));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, (64 - textWidth) / 2, 35);
        
        g.dispose();
        return fallback;
    }
    
    /**
     * Initialize font for nametags
     */
    private void initializeFont() {
        nametagFont = new Font("Arial", Font.BOLD, 12);
    }
    
    /**
     * Register an NPC visual for a zone
     */
    public void registerNPC(String zoneId, Pembeli pembeli, int x, int y, int width, int height) {
        if (pembeli == null) {
            System.err.println("[NPCVisualManager] Cannot register NPC - pembeli is null for zone: " + zoneId);
            return;
        }
        
        BufferedImage sprite = getSpriteForPembeli(pembeli);
        String displayName = getDisplayNameForPembeli(pembeli);
        
        NPCVisual npcVisual = new NPCVisual(sprite, displayName, x, y, width, height);
        npcVisuals.put(zoneId, npcVisual);
        
        System.out.println("[NPCVisualManager] Registered NPC visual for zone " + zoneId + 
                          " (" + displayName + ") at (" + x + ", " + y + ")");
    }
    
    /**
     * Remove NPC visual for a zone
     */
    public void removeNPC(String zoneId) {
        NPCVisual removed = npcVisuals.remove(zoneId);
        if (removed != null) {
            System.out.println("[NPCVisualManager] Removed NPC visual for zone: " + zoneId);
        }
    }
    
    /**
     * Clear all NPC visuals
     */
    public void clearAllNPCs() {
        npcVisuals.clear();
        System.out.println("[NPCVisualManager] Cleared all NPC visuals");
    }
    
    /**
     * Get appropriate sprite for a Pembeli type
     */
    private BufferedImage getSpriteForPembeli(Pembeli pembeli) {
        if (pembeli instanceof PembeliTajir) {
            return pembeli_tajir_sprite;
        } else if (pembeli instanceof PembeliStandar) {
            return pembeli_standar_sprite;
        } else if (pembeli instanceof PembeliMiskin) {
            return pembeli_miskin_sprite;
        } else {
            // Default to standar if unknown type
            return pembeli_standar_sprite;
        }
    }
    
    /**
     * Get display name for a Pembeli type
     */
    private String getDisplayNameForPembeli(Pembeli pembeli) {
        if (pembeli instanceof PembeliTajir) {
            return "Pembeli Tajir";
        } else if (pembeli instanceof PembeliStandar) {
            return "Pembeli Standar";
        } else if (pembeli instanceof PembeliMiskin) {
            return "Pembeli Miskin";
        } else {
            return "Pembeli";
        }
    }
    
    /**
     * Render all NPCs and their nametags
     */
    public void renderAllNPCs(Graphics2D g2d, Camera camera, TriggerZoneManager triggerZoneManager) {
        for (Map.Entry<String, NPCVisual> entry : npcVisuals.entrySet()) {
            String zoneId = entry.getKey();
            NPCVisual npcVisual = entry.getValue();
            
            // Only render if zone still exists (safety check)
            if (triggerZoneManager.getZoneById(zoneId) != null) {
                renderNPC(g2d, npcVisual, camera);
            }
        }
    }
    
    /**
     * Render a single NPC with sprite and nametag
     */
    private void renderNPC(Graphics2D g2d, NPCVisual npcVisual, Camera camera) {
        // Calculate screen position
        int screenX = npcVisual.x - camera.getX();
        int screenY = npcVisual.y - camera.getY();
        
        // Calculate center position for sprite
        int spriteWidth = npcVisual.sprite.getWidth();
        int spriteHeight = npcVisual.sprite.getHeight();
        int spriteCenterX = screenX + (npcVisual.width - spriteWidth) / 2;
        int spriteCenterY = screenY + (npcVisual.height - spriteHeight) / 2;
        
        // Draw sprite
        g2d.drawImage(npcVisual.sprite, spriteCenterX, spriteCenterY, null);
        
        // Draw nametag above sprite
        g2d.setFont(nametagFont);
        FontMetrics fm = g2d.getFontMetrics();
        int nameWidth = fm.stringWidth(npcVisual.displayName);
        int nameX = screenX + (npcVisual.width - nameWidth) / 2;
        int nameY = screenY - 5; // 5 pixels above the zone
        
        // Draw nametag background
        g2d.setColor(new Color(0, 0, 0, 128)); // Semi-transparent black
        g2d.fillRect(nameX - 2, nameY - fm.getAscent(), nameWidth + 4, fm.getHeight());
        
        // Draw nametag text
        g2d.setColor(Color.WHITE);
        g2d.drawString(npcVisual.displayName, nameX, nameY);
    }
    
    /**
     * Check if a zone has an NPC visual registered
     */
    public boolean hasNPCVisual(String zoneId) {
        return npcVisuals.containsKey(zoneId);
    }
    
    /**
     * Get the number of registered NPC visuals
     */
    public int getNPCCount() {
        return npcVisuals.size();
    }
    
    /**
     * Get list of all zone IDs with NPC visuals
     */
    public List<String> getRegisteredZoneIds() {
        return new ArrayList<>(npcVisuals.keySet());
    }
    
    /**
     * Internal class to store NPC visual data
     */
    private static class NPCVisual {
        public final BufferedImage sprite;
        public final String displayName;
        public final int x, y, width, height;
        
        public NPCVisual(BufferedImage sprite, String displayName, int x, int y, int width, int height) {
            this.sprite = sprite;
            this.displayName = displayName;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
