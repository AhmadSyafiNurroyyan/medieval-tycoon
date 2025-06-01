package model;

import enums.PerkType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  public boolean upgradePerk(Player player, Perk perk) {
    return tokoPerks.upgrade(player, perk);
  }

  public boolean convertPerk(Player player, Perk perkLama, PerkType targetType) {
    return tokoPerks.convert(player, perkLama, targetType);
  }

  public boolean buyPerk(Player player, PerkType perkType) {
    return tokoPerks.buyPerk(player, perkType);
  }

  public boolean pilihPerkUntukJualan(Player player, Perk perk) {
    return player.pilihPerkUntukJualan(perk);
  }

  public void resetPerkUntukJualan(Player player) {
    player.resetPerkUntukJualan();
  }

  public boolean canPlayerAffordPerk(Player player, PerkType perkType) {
    Perk perk = tokoPerks.getPerkByType(perkType);
    return perk != null && player.getMoney() >= perk.getHarga();
  }

  public boolean canPlayerAffordUpgrade(Player player, Perk perk) {
    if (perk == null || perk.isMaxLevel()) {
      return false;
    }
    return player.getMoney() >= perk.getBiayaUpgrade();
  }

  public boolean hasAvailablePerkSlot(Player player) {
    return player.getSemuaPerkDimiliki().size() < 2;
  }
  public boolean hasConvertiblePerk(Player player, PerkType targetType) {
    if (player.hasPerk(targetType)) {
      return false; // Already has target perk
    }
    
    // Check if player has any perk that can convert to the target type
    for (Perk perk : player.getSemuaPerkDimiliki()) {
      if (perk.canConvertTo(targetType)) {
        return true;
      }
    }
    return false;
  }
  public Perk getPlayerPerkByType(Player player, PerkType perkType) {
    for (Perk perk : player.getSemuaPerkDimiliki()) {
      if (perk.getPerkType() == perkType) {
        return perk;
      }
    }
    return null;
  }
  public List<Perk> getConvertiblePerks(Player player, PerkType targetType) {
    List<Perk> convertiblePerks = new ArrayList<>();
    for (Perk perk : player.getSemuaPerkDimiliki()) {
      if (perk.canConvertTo(targetType)) {
        convertiblePerks.add(perk);
      }
    }
    return convertiblePerks;
  }

  /**
   * Mendapatkan semua pilihan konversi yang tersedia untuk player
   * @param player player yang akan dicek
   * @return Map dengan key = perk yang dimiliki, value = target konversi yang diizinkan
   */
  public Map<Perk, PerkType> getAvailableConversions(Player player) {
    Map<Perk, PerkType> conversions = new HashMap<>();
    for (Perk perk : player.getSemuaPerkDimiliki()) {
      PerkType target = perk.getAllowedConversionTarget();
      if (target != null && !player.hasPerk(target) && perk.getLevel() > 0) {
        conversions.put(perk, target);
      }
    }
    return conversions;
  }
}
