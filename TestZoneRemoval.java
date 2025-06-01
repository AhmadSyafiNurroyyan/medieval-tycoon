import MapManager.RandomTriggerZoneManager;
import MapManager.TriggerZoneManager;
import gui.TransactionsGUI;
import javax.swing.JPanel;
import model.Player;

/**
 * Test class to verify zone removal functionality
 */
public class TestZoneRemoval {
    public static void main(String[] args) {
        System.out.println("=== Testing Zone Removal Functionality ===");
        
        // Create test components
        Player testPlayer = new Player("TestPlayer");
        TriggerZoneManager triggerZoneManager = new TriggerZoneManager();
        RandomTriggerZoneManager randomTriggerZoneManager = new RandomTriggerZoneManager();
        JPanel parentPanel = new JPanel();
        TransactionsGUI transactionsGUI = new TransactionsGUI(parentPanel);
        
        // Set up the components like GamePanel does
        randomTriggerZoneManager.setPlayer(testPlayer);
        randomTriggerZoneManager.setDialogSystem(transactionsGUI);
        transactionsGUI.setPlayer(testPlayer);
        transactionsGUI.setTriggerZoneManager(triggerZoneManager);
        transactionsGUI.setRandomTriggerZoneManager(randomTriggerZoneManager);
        
        // Generate some test zones
        System.out.println("\n1. Generating random zones...");
        randomTriggerZoneManager.generateRandomZones(0, 1000, 0, 1000, triggerZoneManager);
        
        System.out.println("Initial zone count: " + randomTriggerZoneManager.getZoneCount());
        System.out.println("TriggerZoneManager zone count: " + triggerZoneManager.getAllZones().size());
        
        // Print all zone IDs for debugging
        System.out.println("\nAll zones in TriggerZoneManager:");
        for (var zone : triggerZoneManager.getAllZones()) {
            System.out.println("  - " + zone.getId());
        }
        
        // Test zone removal
        if (!triggerZoneManager.getAllZones().isEmpty()) {
            String testZoneId = triggerZoneManager.getAllZones().get(0).getId();
            System.out.println("\n2. Testing zone removal for: " + testZoneId);
            
            // Simulate setting the current zone ID (like when a zone is triggered)
            transactionsGUI.setCurrentTriggerZoneId(testZoneId);
            
            // Test the removal method directly
            boolean removed = randomTriggerZoneManager.removeZoneById(testZoneId, triggerZoneManager);
            System.out.println("Zone removal result: " + removed);
            
            System.out.println("Remaining zone count: " + randomTriggerZoneManager.getZoneCount());
            System.out.println("TriggerZoneManager zone count: " + triggerZoneManager.getAllZones().size());
            
            // Verify the zone is actually gone
            var remainingZone = triggerZoneManager.getZoneById(testZoneId);
            System.out.println("Zone still exists in TriggerZoneManager: " + (remainingZone != null));
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}
