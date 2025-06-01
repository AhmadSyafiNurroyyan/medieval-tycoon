import model.*;
import enums.PerkType;
import exceptions.PerkConversionException;

/**
 * Test class untuk memverifikasi bahwa PerkConversionException
 * bekerja dengan benar dalam berbagai skenario perk operations
 */
public class TestPerkConversionException {

  public static void main(String[] args) {
    System.out.println("=== Test PerkConversionException Functionality ===\n");

    // Setup
    Player player = new Player("TestPlayer");
    player.tambahMoney(5_000_000); // Berikan uang cukup untuk testing
    TokoPerks tokoPerks = new TokoPerks();
    PerksManagement perksManagement = new PerksManagement();

    // Test 1: Membeli perk dengan uang tidak cukup
    testInsufficientMoneyForPurchase(player, tokoPerks);

    // Test 2: Membeli perk yang sudah dimiliki
    testDuplicatePerkPurchase(player, tokoPerks);

    // Test 3: Membeli perk ketika slot penuh
    testFullSlotPurchase(player, tokoPerks);

    // Test 4: Upgrade perk yang belum dimiliki
    testUpgradeUnownedPerk(player, tokoPerks);

    // Test 5: Upgrade perk yang sudah max level
    testUpgradeMaxLevelPerk(player, tokoPerks);

    // Test 6: Upgrade perk yang tidak aktif
    testUpgradeInactivePerk(player, tokoPerks);

    // Test 7: Konversi perk dengan aturan yang salah
    testInvalidConversion(player, tokoPerks);

    // Test 8: Konversi perk level 0
    testConvertLevelZeroPerk(player, tokoPerks);

    System.out.println("\n=== Semua Test PerkConversionException Selesai ===");
  }

  private static void testInsufficientMoneyForPurchase(Player player, TokoPerks tokoPerks) {
    System.out.println("Test 1: Membeli perk dengan uang tidak cukup");
    player.setMoney(100); // Set uang sedikit

    try {
      tokoPerks.buyPerk(player, PerkType.ELEGAN);
      System.out.println("❌ FAIL: Exception tidak dilempar");
    } catch (PerkConversionException e) {
      System.out.println("✅ PASS: " + e.getMessage());
    }

    player.tambahMoney(5_000_000); // Reset uang untuk test selanjutnya
    System.out.println();
  }

  private static void testDuplicatePerkPurchase(Player player, TokoPerks tokoPerks) {
    System.out.println("Test 2: Membeli perk yang sudah dimiliki");

    // Beli perk pertama kali
    try {
      tokoPerks.buyPerk(player, PerkType.ACTIVE);
      System.out.println("Berhasil membeli perk Active pertama kali");
    } catch (Exception e) {
      System.out.println("Error saat setup: " + e.getMessage());
      return;
    }

    // Coba beli lagi
    try {
      tokoPerks.buyPerk(player, PerkType.ACTIVE);
      System.out.println("❌ FAIL: Exception tidak dilempar");
    } catch (PerkConversionException e) {
      System.out.println("✅ PASS: " + e.getMessage());
    }
    System.out.println();
  }

  private static void testFullSlotPurchase(Player player, TokoPerks tokoPerks) {
    System.out.println("Test 3: Membeli perk ketika slot penuh");

    // Beli perk kedua
    try {
      tokoPerks.buyPerk(player, PerkType.CHARMING);
      System.out.println("Berhasil membeli perk Charming (slot ke-2)");
    } catch (Exception e) {
      System.out.println("Error saat setup: " + e.getMessage());
      return;
    }

    // Coba beli perk ketiga (slot penuh)
    try {
      tokoPerks.buyPerk(player, PerkType.ELEGAN);
      System.out.println("❌ FAIL: Exception tidak dilempar");
    } catch (PerkConversionException e) {
      System.out.println("✅ PASS: " + e.getMessage());
    }
    System.out.println();
  }

  private static void testUpgradeUnownedPerk(Player player, TokoPerks tokoPerks) {
    System.out.println("Test 4: Upgrade perk yang belum dimiliki");

    // Buat perk yang tidak dimiliki player
    Perk perkElegan = tokoPerks.getPerkByType(PerkType.ELEGAN);

    try {
      tokoPerks.upgrade(player, perkElegan);
      System.out.println("❌ FAIL: Exception tidak dilempar");
    } catch (PerkConversionException e) {
      System.out.println("✅ PASS: " + e.getMessage());
    }
    System.out.println();
  }

  private static void testUpgradeMaxLevelPerk(Player player, TokoPerks tokoPerks) {
    System.out.println("Test 5: Upgrade perk yang sudah max level");

    // Ambil perk yang dimiliki player dan set ke max level
    Perk ownedPerk = null;
    for (Perk perk : player.getSemuaPerkDimiliki()) {
      if (perk.getPerkType() == PerkType.ACTIVE) {
        ownedPerk = perk;
        break;
      }
    }

    if (ownedPerk == null) {
      System.out.println("❌ Setup error: Perk Active tidak ditemukan");
      return;
    }

    // Activate perk dan upgrade ke max level
    ownedPerk.activate();
    while (!ownedPerk.isMaxLevel()) {
      try {
        ownedPerk.upgradeLevel();
      } catch (Exception e) {
        break;
      }
    }

    try {
      tokoPerks.upgrade(player, ownedPerk);
      System.out.println("❌ FAIL: Exception tidak dilempar");
    } catch (PerkConversionException e) {
      System.out.println("✅ PASS: " + e.getMessage());
    }
    System.out.println();
  }

  private static void testUpgradeInactivePerk(Player player, TokoPerks tokoPerks) {
    System.out.println("Test 6: Upgrade perk yang tidak aktif");

    // Buat perk baru yang tidak aktif
    try {
      // Reset player dan beli perk baru
      player.getSemuaPerkDimiliki().clear();
      tokoPerks.buyPerk(player, PerkType.ELEGAN);

      // Ambil perk yang baru dibeli (tidak aktif secara default)
      Perk elegantPerk = null;
      for (Perk perk : player.getSemuaPerkDimiliki()) {
        if (perk.getPerkType() == PerkType.ELEGAN) {
          elegantPerk = perk;
          break;
        }
      }

      if (elegantPerk == null) {
        System.out.println("❌ Setup error: Perk Elegan tidak ditemukan");
        return;
      }

      // Pastikan perk tidak aktif
      elegantPerk.deactivate();

      try {
        tokoPerks.upgrade(player, elegantPerk);
        System.out.println("❌ FAIL: Exception tidak dilempar");
      } catch (PerkConversionException e) {
        System.out.println("✅ PASS: " + e.getMessage());
      }
    } catch (Exception e) {
      System.out.println("❌ Setup error: " + e.getMessage());
    }
    System.out.println();
  }

  private static void testInvalidConversion(Player player, TokoPerks tokoPerks) {
    System.out.println("Test 7: Konversi perk dengan aturan yang salah");

    // Ambil perk elegan dan coba konversi ke active (tidak diizinkan)
    Perk elegantPerk = null;
    for (Perk perk : player.getSemuaPerkDimiliki()) {
      if (perk.getPerkType() == PerkType.ELEGAN) {
        elegantPerk = perk;
        break;
      }
    }

    if (elegantPerk == null) {
      System.out.println("❌ Setup error: Perk Elegan tidak ditemukan");
      return;
    }

    // Activate dan upgrade perk agar bisa dikonversi
    elegantPerk.activate();
    try {
      elegantPerk.upgradeLevel();
    } catch (Exception e) {
      // Ignore upgrade error for this test
    }

    try {
      tokoPerks.convert(player, elegantPerk, PerkType.ACTIVE);
      System.out.println("❌ FAIL: Exception tidak dilempar");
    } catch (PerkConversionException e) {
      System.out.println("✅ PASS: " + e.getMessage());
    }
    System.out.println();
  }

  private static void testConvertLevelZeroPerk(Player player, TokoPerks tokoPerks) {
    System.out.println("Test 8: Konversi perk level 0");

    // Reset dan buat setup baru
    player.getSemuaPerkDimiliki().clear();
    try {
      tokoPerks.buyPerk(player, PerkType.CHARMING);
    } catch (Exception e) {
      System.out.println("❌ Setup error: " + e.getMessage());
      return;
    }

    // Ambil perk charming level 0
    Perk charmingPerk = null;
    for (Perk perk : player.getSemuaPerkDimiliki()) {
      if (perk.getPerkType() == PerkType.CHARMING) {
        charmingPerk = perk;
        break;
      }
    }

    if (charmingPerk == null) {
      System.out.println("❌ Setup error: Perk Charming tidak ditemukan");
      return;
    }

    // Pastikan level 0
    charmingPerk.resetUpgrade();

    try {
      tokoPerks.convert(player, charmingPerk, PerkType.ACTIVE);
      System.out.println("❌ FAIL: Exception tidak dilempar");
    } catch (PerkConversionException e) {
      System.out.println("✅ PASS: " + e.getMessage());
    }
    System.out.println();
  }
}
