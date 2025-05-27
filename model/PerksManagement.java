package model;

import enums.PerkType;
import java.util.List;

public class PerksManagement {

  private final TokoPerks tokoPerks;

  public PerksManagement() {
    this.tokoPerks = new TokoPerks();
  }

  public List<Perk> getDaftarPerkDiToko() {
    return tokoPerks.getDaftarPerk();
  }

  public List<Perk> getPerkYangDimiliki(Player player) {
    return player.getSemuaPerkDimiliki();
  }

  public boolean beliPerk(Player player, PerkType type) {
    Perk perk = tokoPerks.getPerkByType(type);
    if (perk != null) {
      return tokoPerks.beli(player, perk);
    }
    return false;
  }

  public boolean upgradePerk(Player player, Perk perk) {
    return tokoPerks.upgrade(player, perk);
  }

  public boolean convertPerk(Player player, Perk perkLama, PerkType targetType) {
    return tokoPerks.convert(player, perkLama, targetType);
  }

  public boolean pilihPerkUntukJualan(Player player, Perk perk) {
    return player.pilihPerkUntukJualan(perk);
  }

  public void resetPerkUntukJualan(Player player) {
    player.resetPerkUntukJualan();
  }
}
