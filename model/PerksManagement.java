package model;

import enums.PerkType;
import java.util.List;

public class PerksManagement {

  private final TokoPerks tokoPerks;

  public PerksManagement() {
    this.tokoPerks = new TokoPerks();
  }

  // Ambil semua perk yang tersedia di toko (pakai method getter di TokoPerks)
  public List<Perk> getDaftarPerkDiToko() {
    return tokoPerks.getDaftarPerk();
  }

  // Ambil semua perk yang dimiliki player
  public List<Perk> getPerkYangDimiliki(Player player) {
    return player.getSemuaPerkDimiliki();
  }

  // Beli perk berdasarkan tipe perk
  public boolean beliPerk(Player player, PerkType type) {
    Perk perk = tokoPerks.getPerkByType(type);
    if (perk != null) {
      return tokoPerks.beli(player, perk);
    }
    return false;
  }

  // Upgrade perk
  public boolean upgradePerk(Player player, Perk perk) {
    return tokoPerks.upgrade(player, perk);
  }

  // Konversi perk
  public boolean convertPerk(Player player, Perk perkLama, PerkType targetType) {
    return tokoPerks.convert(player, perkLama, targetType);
  }

  // Pilih perk untuk jualan
  public boolean pilihPerkUntukJualan(Player player, Perk perk) {
    return player.pilihPerkUntukJualan(perk);
  }

  // Reset perk jualan
  public void resetPerkUntukJualan(Player player) {
    player.resetPerkUntukJualan();
  }
}
