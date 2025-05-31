package model;

import java.util.List;

/**
 * Kelas untuk mengelola dan mengintegrasikan efek dari semua perk
 * dalam sistem jual beli game Medieval Tycoon
 */
public class PerkEffectManager {

  /**
   * Membuat pembeli dengan mempertimbangkan semua perk aktif
   * Priority: Active > Elegan > Default
   * 
   * @param player player yang memiliki perk
   * @return pembeli yang dimodifikasi sesuai perk
   */  public static Pembeli createBuyerWithPerks(Player player) {
    List<Perk> activePerks = player.getPerkDipilihUntukJualan();
    
    System.out.println("[PERK DEBUG] Creating buyer with perks...");
    System.out.println("[PERK DEBUG] Active perks count: " + activePerks.size());
    
    for (Perk perk : activePerks) {
      System.out.println("[PERK DEBUG] - " + perk.getClass().getSimpleName() + 
                        " Lv." + perk.getLevel() + " (active: " + perk.isActive() + ")");
    }

    // Cek perk yang mempengaruhi spawning pembeli (prioritas: Active > Elegan)
    for (Perk perk : activePerks) {
      if (perk instanceof PerksActive && perk.isActive()) {
        System.out.println("[PERK DEBUG] Using PerksActive for buyer generation");
        return ((PerksActive) perk).buatPembeliDenganPerkActive();
      }
    }

    for (Perk perk : activePerks) {
      if (perk instanceof PerksElegan && perk.isActive()) {
        System.out.println("[PERK DEBUG] Using PerksElegan for buyer generation");
        return ((PerksElegan) perk).buatPembeliDenganPerkElegan();
      }
    }

    System.out.println("[PERK DEBUG] Using default buyer generation");
    return Pembeli.buatPembeliAcak(); // Default jika tidak ada perk aktif
  }
  /**
   * Menerapkan efek charming pada transaksi
   * 
   * @param player     player yang memiliki perk
   * @param hargaFinal harga final yang ditawarkan
   * @param pembeli    pembeli yang bernegosiasi
   * @return true jika transaksi berhasil
   */
  public static boolean applyCharmingEffect(Player player, int hargaFinal, Pembeli pembeli) {
    List<Perk> activePerks = player.getPerkDipilihUntukJualan();

    System.out.println("[PERK EFFECT DEBUG] Checking for PerksCharming in " + activePerks.size() + " active perks...");

    for (Perk perk : activePerks) {
      if (perk instanceof PerksCharming && perk.isActive()) {
        System.out.println("[PERK EFFECT DEBUG] Found active PerksCharming (Level " + perk.getLevel() + ")");
        System.out.println("[PERK EFFECT DEBUG] Applying charming effect to negotiation...");
        return ((PerksCharming) perk).applyCharmingEffect(hargaFinal, pembeli);
      }
    }

    System.out.println("[PERK EFFECT DEBUG] No active PerksCharming found - using default transaction decision");
    return pembeli.putuskanTransaksi(hargaFinal); // Default behavior
  }
  /**
   * Menerapkan bonus charming pada max tawaran pembeli
   * 
   * @param player     player yang memiliki perk
   * @param maxTawaran max tawaran original
   * @return max tawaran yang sudah dimodifikasi
   */
  public static double applyCharmingToMaxOffer(Player player, double maxTawaran) {
    List<Perk> activePerks = player.getPerkDipilihUntukJualan();

    System.out.println("[PERK EFFECT DEBUG] Checking for PerksCharming price boost in " + activePerks.size() + " active perks...");

    for (Perk perk : activePerks) {
      if (perk instanceof PerksCharming && perk.isActive()) {
        System.out.println("[PERK EFFECT DEBUG] Found active PerksCharming (Level " + perk.getLevel() + ")");
        System.out.println("[PERK EFFECT DEBUG] Applying charming price boost...");
        return ((PerksCharming) perk).applyCharmingToMaxOffer(maxTawaran);
      }
    }

    System.out.println("[PERK EFFECT DEBUG] No active PerksCharming found - using default max offer");
    return maxTawaran; // Default value
  }
  /**
   * Menerapkan bonus elegan pada multiplier pembeli
   * 
   * @param player  player yang memiliki perk
   * @param pembeli pembeli yang akan dimodifikasi
   * @return multiplier yang sudah dimodifikasi
   */
  public static double applyEleganBonus(Player player, Pembeli pembeli) {
    List<Perk> activePerks = player.getPerkDipilihUntukJualan();

    System.out.println("[PERK EFFECT DEBUG] Checking for PerksElegan bonus in " + activePerks.size() + " active perks...");

    for (Perk perk : activePerks) {
      if (perk instanceof PerksElegan && perk.isActive()) {
        System.out.println("[PERK EFFECT DEBUG] Found active PerksElegan (Level " + perk.getLevel() + ")");
        System.out.println("[PERK EFFECT DEBUG] Applying elegan bonus to buyer multiplier...");
        double result = ((PerksElegan) perk).applyEleganBonus(pembeli);
        System.out.println("[PERK EFFECT DEBUG] Original multiplier: " + String.format("%.2f", pembeli.getMultiplier()) + 
                          ", New multiplier: " + String.format("%.2f", result));
        return result;
      }
    }

    System.out.println("[PERK EFFECT DEBUG] No active PerksElegan found - using default multiplier");
    return pembeli.getMultiplier(); // Default multiplier
  }
  /**
   * Menghitung total efek dari semua perk aktif untuk display
   * 
   * @param player player yang memiliki perk
   * @return string describing all active perk effects
   */
  public static String getActivePerkEffectsSummary(Player player) {
    List<Perk> activePerks = player.getPerkDipilihUntukJualan();
    StringBuilder summary = new StringBuilder();

    for (Perk perk : activePerks) {
      if (perk.isActive() && perk.getLevel() > 0) {
        if (summary.length() > 0) {
          summary.append(", ");
        }
        
        if (perk instanceof PerksActive perksActive) {
          int additionalBuyers = perksActive.getAdditionalBuyersCount();
          summary.append("Active Lv").append(perk.getLevel()).append(" (+").append(additionalBuyers).append(" pembeli)");
        } else if (perk instanceof PerksCharming) {
          int charmBonus = perk.getLevel() * 15;
          summary.append("Charming +").append(charmBonus).append("%");
        } else if (perk instanceof PerksElegan) {
          int eleganBonus = perk.getLevel() * 10; // Fixed from 8% to 10%
          summary.append("Elegan +").append(eleganBonus).append("% tajir");
        }
      }
    }

    return summary.length() > 0 ? "Perk Aktif: " + summary.toString() : "Tidak ada perk aktif";
  }
  /**
   * Cek apakah player memiliki perk tertentu yang aktif
   * 
   * @param player    player yang akan dicek
   * @param perkClass class dari perk yang dicari
   * @return true jika player memiliki perk tersebut dan aktif
   */
  public static boolean hasActivePerk(Player player, Class<? extends Perk> perkClass) {
    List<Perk> activePerks = player.getPerkDipilihUntukJualan();

    for (Perk perk : activePerks) {
      if (perkClass.isInstance(perk) && perk.isActive() && perk.getLevel() > 0) {
        return true;
      }
    }

    return false;
  }
  /**
   * Calculate additional buyers based on PerksActive level
   * 
   * @param player player yang memiliki perk
   * @return number of additional buyers to generate
   */
  public static int getAdditionalBuyersFromPerks(Player player) {
    List<Perk> activePerks = player.getPerkDipilihUntukJualan();

    System.out.println("[PERK EFFECT DEBUG] Checking for PerksActive in " + activePerks.size() + " active perks...");
    
    for (Perk perk : activePerks) {
      if (perk instanceof PerksActive && perk.isActive()) {
        System.out.println("[PERK EFFECT DEBUG] Found active PerksActive (Level " + perk.getLevel() + ")");
        int additionalBuyers = ((PerksActive) perk).getAdditionalBuyersCount();
        System.out.println("[PERK EFFECT DEBUG] PerksActive returned: " + additionalBuyers + " additional buyers");
        return additionalBuyers;
      }
    }

    System.out.println("[PERK EFFECT DEBUG] No active PerksActive found - returning 0 additional buyers");
    return 0; // No additional buyers
  }
}
