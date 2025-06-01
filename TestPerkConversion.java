import model.*;
import enums.PerkType;
import exceptions.PerkConversionException;
import java.util.Map;

/**
 * Test program untuk memverifikasi sistem konversi perk
 * berdasarkan aturan:
 * • Elegan dapat diubah menjadi charming, tetapi tidak bisa menjadi active
 * • Charming dapat diubah menjadi active, tapi tidak bisa menjadi elegan
 * • Active dapat diubah menjadi elegan, tetapi tidak bisa menjadi charming
 */
public class TestPerkConversion {

  public static void main(String[] args) {
    System.out.println("=== Test Sistem Konversi Perk ===\n");

    // Setup
    Player player = new Player("TestPlayer");
    player.tambahMoney(10_000_000); // Berikan uang cukup
    PerksManagement perksManagement = new PerksManagement();
    TokoPerks tokoPerks = new TokoPerks();

    // Test 1: Beli perk Elegan dan test konversi
    testEleganConversion(player, perksManagement, tokoPerks);

    // Reset player untuk test berikutnya
    player = new Player("TestPlayer2");
    player.tambahMoney(10_000_000);

    // Test 2: Beli perk Charming dan test konversi
    testCharmingConversion(player, perksManagement, tokoPerks);

    // Reset player untuk test berikutnya
    player = new Player("TestPlayer3");
    player.tambahMoney(10_000_000);

    // Test 3: Beli perk Active dan test konversi
    testActiveConversion(player, perksManagement, tokoPerks);

    System.out.println("\n=== Test Sistem Konversi Selesai ===");
  }

  private static void testEleganConversion(Player player, PerksManagement perksManagement, TokoPerks tokoPerks) {
    System.out.println("=== Test Konversi Perk Elegan ===");

    // Beli dan upgrade perk Elegan
    try {
      boolean bought = perksManagement.buyPerk(player, PerkType.ELEGAN);
      System.out.println("Beli perk Elegan: " + (bought ? "Berhasil" : "Gagal"));

      if (bought) {
        Perk eleganPerk = perksManagement.getPlayerPerkByType(player, PerkType.ELEGAN);
        eleganPerk.activate();

        // Upgrade ke level 1 (syarat untuk konversi)
        boolean upgraded = perksManagement.upgradePerk(player, eleganPerk);
        System.out.println("Upgrade perk Elegan ke level 1: " + (upgraded ? "Berhasil" : "Gagal"));

        // Cek available conversions
        Map<Perk, PerkType> conversions = perksManagement.getAvailableConversions(player);
        System.out.println("Available conversions untuk player:");
        for (Map.Entry<Perk, PerkType> entry : conversions.entrySet()) {
          System.out.println("  " + entry.getKey().getName() + " → " + entry.getValue().getNama());
        }

        // Test konversi yang valid: Elegan → Charming
        try {
          boolean converted = perksManagement.convertPerk(player, eleganPerk, PerkType.CHARMING);
          System.out.println("Konversi Elegan → Charming: " + (converted ? "Berhasil" : "Gagal"));
        } catch (PerkConversionException e) {
          System.out.println("Konversi Elegan → Charming gagal: " + e.getMessage());
        }

        // Test konversi yang invalid: Elegan → Active (should fail)
        try {
          // Reset untuk test yang invalid
          player = new Player("TestElegan2");
          player.tambahMoney(10_000_000);
          perksManagement.buyPerk(player, PerkType.ELEGAN);
          eleganPerk = perksManagement.getPlayerPerkByType(player, PerkType.ELEGAN);
          eleganPerk.activate();
          perksManagement.upgradePerk(player, eleganPerk);

          boolean converted = perksManagement.convertPerk(player, eleganPerk, PerkType.ACTIVE);
          System.out.println("Konversi Elegan → Active: " + (converted ? "Berhasil (ERROR!)" : "Gagal"));
        } catch (PerkConversionException e) {
          System.out.println("Konversi Elegan → Active ditolak (BENAR): " + e.getMessage());
        }
      }
    } catch (PerkConversionException e) {
      System.out.println("Error dalam test Elegan: " + e.getMessage());
    }
    System.out.println();
  }

  private static void testCharmingConversion(Player player, PerksManagement perksManagement, TokoPerks tokoPerks) {
    System.out.println("=== Test Konversi Perk Charming ===");

    try {
      boolean bought = perksManagement.buyPerk(player, PerkType.CHARMING);
      System.out.println("Beli perk Charming: " + (bought ? "Berhasil" : "Gagal"));

      if (bought) {
        Perk charmingPerk = perksManagement.getPlayerPerkByType(player, PerkType.CHARMING);
        charmingPerk.activate();

        boolean upgraded = perksManagement.upgradePerk(player, charmingPerk);
        System.out.println("Upgrade perk Charming ke level 1: " + (upgraded ? "Berhasil" : "Gagal"));

        // Test konversi yang valid: Charming → Active
        try {
          boolean converted = perksManagement.convertPerk(player, charmingPerk, PerkType.ACTIVE);
          System.out.println("Konversi Charming → Active: " + (converted ? "Berhasil" : "Gagal"));
        } catch (PerkConversionException e) {
          System.out.println("Konversi Charming → Active gagal: " + e.getMessage());
        }

        // Test konversi yang invalid: Charming → Elegan (should fail)
        try {
          // Reset untuk test yang invalid
          player = new Player("TestCharming2");
          player.tambahMoney(10_000_000);
          perksManagement.buyPerk(player, PerkType.CHARMING);
          charmingPerk = perksManagement.getPlayerPerkByType(player, PerkType.CHARMING);
          charmingPerk.activate();
          perksManagement.upgradePerk(player, charmingPerk);

          boolean converted = perksManagement.convertPerk(player, charmingPerk, PerkType.ELEGAN);
          System.out.println("Konversi Charming → Elegan: " + (converted ? "Berhasil (ERROR!)" : "Gagal"));
        } catch (PerkConversionException e) {
          System.out.println("Konversi Charming → Elegan ditolak (BENAR): " + e.getMessage());
        }
      }
    } catch (PerkConversionException e) {
      System.out.println("Error dalam test Charming: " + e.getMessage());
    }
    System.out.println();
  }

  private static void testActiveConversion(Player player, PerksManagement perksManagement, TokoPerks tokoPerks) {
    System.out.println("=== Test Konversi Perk Active ===");

    try {
      boolean bought = perksManagement.buyPerk(player, PerkType.ACTIVE);
      System.out.println("Beli perk Active: " + (bought ? "Berhasil" : "Gagal"));

      if (bought) {
        Perk activePerk = perksManagement.getPlayerPerkByType(player, PerkType.ACTIVE);
        activePerk.activate();

        boolean upgraded = perksManagement.upgradePerk(player, activePerk);
        System.out.println("Upgrade perk Active ke level 1: " + (upgraded ? "Berhasil" : "Gagal"));

        // Test konversi yang valid: Active → Elegan
        try {
          boolean converted = perksManagement.convertPerk(player, activePerk, PerkType.ELEGAN);
          System.out.println("Konversi Active → Elegan: " + (converted ? "Berhasil" : "Gagal"));
        } catch (PerkConversionException e) {
          System.out.println("Konversi Active → Elegan gagal: " + e.getMessage());
        }

        // Test konversi yang invalid: Active → Charming (should fail)
        try {
          // Reset untuk test yang invalid
          player = new Player("TestActive2");
          player.tambahMoney(10_000_000);
          perksManagement.buyPerk(player, PerkType.ACTIVE);
          activePerk = perksManagement.getPlayerPerkByType(player, PerkType.ACTIVE);
          activePerk.activate();
          perksManagement.upgradePerk(player, activePerk);

          boolean converted = perksManagement.convertPerk(player, activePerk, PerkType.CHARMING);
          System.out.println("Konversi Active → Charming: " + (converted ? "Berhasil (ERROR!)" : "Gagal"));
        } catch (PerkConversionException e) {
          System.out.println("Konversi Active → Charming ditolak (BENAR): " + e.getMessage());
        }
      }
    } catch (PerkConversionException e) {
      System.out.println("Error dalam test Active: " + e.getMessage());
    }
    System.out.println();
  }
}
