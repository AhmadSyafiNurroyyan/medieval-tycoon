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
   */
  public static Pembeli createBuyerWithPerks(Player player) {
    List<Perk> activePerks = player.getPerkDipilihUntukJualan();

    // Cek perk yang mempengaruhi spawning pembeli (prioritas: Active > Elegan)
    for (Perk perk : activePerks) {
      if (perk instanceof PerksActive && perk.isActive()) {
        return ((PerksActive) perk).buatPembeliDenganPerkActive();
      }
    }

    for (Perk perk : activePerks) {
      if (perk instanceof PerksElegan && perk.isActive()) {
        return ((PerksElegan) perk).buatPembeliDenganPerkElegan();
      }
    }

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

    for (Perk perk : activePerks) {
      if (perk instanceof PerksCharming && perk.isActive()) {
        return ((PerksCharming) perk).applyCharmingEffect(hargaFinal, pembeli);
      }
    }

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

    for (Perk perk : activePerks) {
      if (perk instanceof PerksCharming && perk.isActive()) {
        return ((PerksCharming) perk).applyCharmingToMaxOffer(maxTawaran);
      }
    }

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

    for (Perk perk : activePerks) {
      if (perk instanceof PerksElegan && perk.isActive()) {
        return ((PerksElegan) perk).applyEleganBonus(pembeli);
      }
    }

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

        if (perk instanceof PerksActive) {
          summary.append("Active Lv").append(perk.getLevel());
        } else if (perk instanceof PerksCharming) {
          int charmBonus = perk.getLevel() * 15;
          summary.append("Charming +").append(charmBonus).append("%");
        } else if (perk instanceof PerksElegan) {
          int eleganBonus = perk.getLevel() * 8;
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
}
