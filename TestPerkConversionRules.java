import model.*;
import enums.PerkType;
import exceptions.PerkConversionException;

public class TestPerkConversionRules {
    public static void main(String[] args) {
        System.out.println("=== Testing Perk Conversion Rules ===\n");
        
        // Create player with money
        Player player = new Player("TestPlayer");
        player.tambahMoney(10000);
        
        // Create TokoPerks and buy some perks
        TokoPerks toko = new TokoPerks();
        
        try {
            // Test 1: Buy Elegan perk and upgrade it
            System.out.println("1. Buying Elegan perk...");
            toko.buyPerk(player, PerkType.ELEGAN);
            
            Perk eleganPerk = player.getPerkByType(PerkType.ELEGAN);
            eleganPerk.activate(); // Activate it first
            
            // Try to upgrade it to level 1 (required for conversion)
            System.out.println("2. Upgrading Elegan to level 1...");
            toko.upgrade(player, eleganPerk);
            System.out.println("   Elegan level: " + eleganPerk.getLevel());
            
            // Test allowed conversion: Elegan -> Charming
            System.out.println("\n3. Testing allowed conversion: Elegan -> Charming");
            System.out.println("   Can convert Elegan to Charming: " + eleganPerk.canConvertTo(PerkType.CHARMING));
            
            // Test disallowed conversion: Elegan -> Active  
            System.out.println("4. Testing disallowed conversion: Elegan -> Active");
            System.out.println("   Can convert Elegan to Active: " + eleganPerk.canConvertTo(PerkType.ACTIVE));
            
            // Actually convert Elegan to Charming
            System.out.println("\n5. Converting Elegan to Charming...");
            boolean convertSuccess = toko.convert(player, eleganPerk, PerkType.CHARMING);
            System.out.println("   Conversion successful: " + convertSuccess);
            
            // Check if player now has Charming perk
            Perk charmingPerk = player.getPerkByType(PerkType.CHARMING);
            if (charmingPerk != null) {
                System.out.println("   Player now has Charming perk at level: " + charmingPerk.getLevel());
                
                // Upgrade Charming to test next conversion
                charmingPerk.activate();
                toko.upgrade(player, charmingPerk);
                
                // Test Charming -> Active (allowed)
                System.out.println("\n6. Testing Charming -> Active conversion");
                System.out.println("   Can convert Charming to Active: " + charmingPerk.canConvertTo(PerkType.ACTIVE));
                
                // Test Charming -> Elegan (not allowed)
                System.out.println("7. Testing Charming -> Elegan conversion (should be false)");
                System.out.println("   Can convert Charming to Elegan: " + charmingPerk.canConvertTo(PerkType.ELEGAN));
                
                // Convert to Active
                System.out.println("\n8. Converting Charming to Active...");
                boolean convertSuccess2 = toko.convert(player, charmingPerk, PerkType.ACTIVE);
                System.out.println("   Conversion successful: " + convertSuccess2);
                
                // Check Active perk
                Perk activePerk = player.getPerkByType(PerkType.ACTIVE);
                if (activePerk != null) {
                    activePerk.activate();
                    toko.upgrade(player, activePerk);
                    
                    // Test Active -> Elegan (allowed)
                    System.out.println("\n9. Testing Active -> Elegan conversion");
                    System.out.println("   Can convert Active to Elegan: " + activePerk.canConvertTo(PerkType.ELEGAN));
                    
                    // Test Active -> Charming (not allowed)
                    System.out.println("10. Testing Active -> Charming conversion (should be false)");
                    System.out.println("   Can convert Active to Charming: " + activePerk.canConvertTo(PerkType.CHARMING));
                }
            }
            
        } catch (PerkConversionException e) {
            System.out.println("PerkConversionException caught: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test error cases
        System.out.println("\n=== Testing Error Cases ===");
        
        try {
            // Test converting to same type
            Perk testPerk = player.getPerkByType(PerkType.ACTIVE);
            if (testPerk != null) {
                System.out.println("\n11. Testing conversion to same type (should throw exception):");
                toko.convert(player, testPerk, PerkType.ACTIVE);
            }
        } catch (PerkConversionException e) {
            System.out.println("    Expected exception: " + e.getMessage());
        }
        
        try {
            // Test converting level 0 perk
            PerksElegan testPerkLevel0 = new PerksElegan();
            player.addPerk(testPerkLevel0);
            System.out.println("\n12. Testing conversion of level 0 perk (should throw exception):");
            toko.convert(player, testPerkLevel0, PerkType.CHARMING);
        } catch (PerkConversionException e) {
            System.out.println("    Expected exception: " + e.getMessage());
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}
