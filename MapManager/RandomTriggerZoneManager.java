/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package MapManager;

import gui.TransactionsGUI;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import model.Pembeli;
import model.PerkEffectManager;
import model.Player;

public class RandomTriggerZoneManager {    
    private static final int MIN_ZONES = 3;
    private static final int MAX_ZONES = 8;
    private static final int ZONE_WIDTH = 128;
    private static final int ZONE_HEIGHT = 128;
    private static final int MIN_SPACING = 20;
    private final List<Rectangle> placedZones;
    private final List<Pembeli> zoneBuyers;
    private final List<String> zoneIds;
    private TransactionsGUI dialogSystem;
    private Player player;
    private final NPCVisualManager npcVisualManager;
    public RandomTriggerZoneManager() {
        this.placedZones = new ArrayList<>();
        this.zoneBuyers = new ArrayList<>();
        this.zoneIds = new ArrayList<>();
        this.dialogSystem = null;
        this.player = null;
        this.npcVisualManager = new NPCVisualManager();
    }
    public NPCVisualManager getNPCVisualManager() {
        return npcVisualManager;
    }
    public void setDialogSystem(TransactionsGUI dialogSystem) {
        this.dialogSystem = dialogSystem;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public void generateRandomZones(int minX, int maxX, int minY, int maxY, TriggerZoneManager triggerZoneManager) {
        placedZones.clear();
        zoneBuyers.clear();
        zoneIds.clear();
        int baseNumZones = (int)(Math.random() * (MAX_ZONES - MIN_ZONES + 1)) + MIN_ZONES;
        int additionalBuyers = 0;
        if (player != null) {
            additionalBuyers = PerkEffectManager.getAdditionalBuyersFromPerks(player);
        }
        int totalZones = baseNumZones + additionalBuyers;
        int cappedZones = Math.min(totalZones, MAX_ZONES * 3);
        totalZones = cappedZones;
        int attemptsPerZone = 50;
        int successfullyPlaced = 0;
        for (int i = 0; i < totalZones; i++) {
            boolean placed = false;
            for (int attempt = 0; attempt < attemptsPerZone && !placed; attempt++) {
                int width = ZONE_WIDTH;
                int height = ZONE_HEIGHT;
                int x = (int)(Math.random() * (maxX - minX - width + 1)) + minX;
                int y = (int)(Math.random() * (maxY - minY - height + 1)) + minY;
                Rectangle newZone = new Rectangle(x, y, width, height);
                if (!overlapsWithExistingZones(newZone)) {
                    placedZones.add(newZone);
                    String zoneId = "ArenaPembeli_" + (successfullyPlaced + 1);
                    zoneIds.add(zoneId);
                    triggerZoneManager.addZone(zoneId, x, y, x + width, y + height, true, () -> {
                        handleRandomZoneTriggered(x, y, width, height);
                    });
                    Pembeli pembeli = (player != null) ? PerkEffectManager.createBuyerWithPerks(player) : Pembeli.buatPembeliAcak();
                    zoneBuyers.add(pembeli);
                    npcVisualManager.registerNPC(zoneId, pembeli, x, y, width, height);
                    placed = true;
                    successfullyPlaced++;
                }
            }
        }
    }
    public void registerZonesToTriggerZoneManager(TriggerZoneManager triggerZoneManager) {
        for (int i = 0; i < placedZones.size(); i++) {
            Rectangle r = placedZones.get(i);
            String zoneId = (i < zoneIds.size()) ? zoneIds.get(i) : "ArenaPembeli_" + (i + 1);
            int x = r.x;
            int y = r.y;
            int width = r.width;
            int height = r.height;
            triggerZoneManager.addZone(zoneId, x, y, x + width, y + height, true, () -> {
                handleRandomZoneTriggered(x, y, width, height);
            });
        }
    }
    private boolean overlapsWithExistingZones(Rectangle newZone) {
        for (Rectangle existingZone : placedZones) {
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
    private void handleRandomZoneTriggered(int x, int y, int width, int height) {
        int idx = -1;
        for (int i = 0; i < placedZones.size(); i++) {
            Rectangle r = placedZones.get(i);
            if (r.x == x && r.y == y && r.width == width && r.height == height) {
                idx = i;
                break;
            }
        }
        Pembeli pembeli = (idx != -1) ? getPembeliForZone(idx) : null;
        if (dialogSystem != null && pembeli != null && idx != -1) {
            String zoneId = (idx < zoneIds.size()) ? zoneIds.get(idx) : "ArenaPembeli_" + (idx + 1);
            dialogSystem.setCurrentTriggerZoneId(zoneId);
            dialogSystem.setPembeli(pembeli);
            dialogSystem.showPembeliDialog();
        }
    }
    public Pembeli getPembeliForZone(int index) {
        if (index >= 0 && index < zoneBuyers.size()) {
            return zoneBuyers.get(index);
        }
        return null;
    }
    public List<Rectangle> getPlacedZones() {
        return new ArrayList<>(placedZones);
    }    public void clearZones() {
        npcVisualManager.clearAllNPCs();
        placedZones.clear();
        zoneBuyers.clear();
        zoneIds.clear();
    }
    public int getZoneCount() {
        return placedZones.size();
    }
    public boolean spawnSingleRandomZone(TriggerZoneManager triggerZoneManager) {
        int minX = 0, maxX = 1150, minY = 0, maxY = 1450;
        int width = ZONE_WIDTH;
        int height = ZONE_HEIGHT;
        int attempts = 50;
        for (int attempt = 0; attempt < attempts; attempt++) {
            int x = (int)(Math.random() * (maxX - minX - width + 1)) + minX;
            int y = (int)(Math.random() * (maxY - minY - height + 1)) + minY;            Rectangle newZone = new Rectangle(x, y, width, height);
            if (!overlapsWithExistingZones(newZone)) {
                placedZones.add(newZone);
                String zoneId = "ArenaPembeli_" + (placedZones.size());
                zoneIds.add(zoneId);
                triggerZoneManager.addZone(zoneId, x, y, x + width, y + height, true, () -> {
                    handleRandomZoneTriggered(x, y, width, height);
                });                Pembeli pembeli = (player != null) ? PerkEffectManager.createBuyerWithPerks(player) : Pembeli.buatPembeliAcak();
                zoneBuyers.add(pembeli);
                npcVisualManager.registerNPC(zoneId, pembeli, x, y, width, height);
                return true;
            }
        }
        return false;
    }    public boolean removeZoneById(String zoneId, TriggerZoneManager triggerZoneManager) {
        if (!zoneId.startsWith("ArenaPembeli_")) {
            return false;
        }
        int index = -1;
        for (int i = 0; i < zoneIds.size(); i++) {
            if (zoneIds.get(i).equals(zoneId)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }
        triggerZoneManager.removeZoneById(zoneId);
        npcVisualManager.removeNPC(zoneId);
        placedZones.remove(index);
        zoneIds.remove(index);
        if (index < zoneBuyers.size()) {
            zoneBuyers.remove(index);
        }
        return true;
    }
}
