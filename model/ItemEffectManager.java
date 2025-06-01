package model;

import java.util.List;

public class ItemEffectManager {

  private Player player;
  private boolean jampiActiveToday = false;

  public ItemEffectManager(Player player) {
    this.player = player;
  }

  // Hipnotis - meningkatkan chance beli langsung
  public boolean applyHipnotis(Pembeli pembeli) {
    Item hipnotis = getActiveItemByName("Hipnotis");
    if (hipnotis != null && !hipnotis.isUsed()) {
      double chance = hipnotis.getHipnotisChance();
      if (Math.random() < chance) {
        hipnotis.markAsUsed(); // sekali pakai per transaksi
        System.out.println("Hipnotis berhasil! Pembeli langsung membeli tanpa menawar.");
        return true;
      }
    }
    return false;
  }

  // Jampi - menggandakan penghasilan hari ini
  public int applyJampi(int originalEarning) {
    Item jampi = getActiveItemByName("Jampi");
    if (jampi != null && !jampiActiveToday) {
      jampiActiveToday = true;
      int boostedEarning = (int) (originalEarning * jampi.getJampiMultiplier());
      System.out.println("Jampi aktif! Penghasilan meningkat dari Rp" + originalEarning +
          " menjadi Rp" + boostedEarning);
      return boostedEarning;
    }
    return originalEarning;
  }  // Semproten - meningkatkan harga jual during transaction based on freshness
  public int applySemproten(int originalPrice) {
    Item semproten = getActiveItemByName("Semproten");
    if (semproten != null) {
      double priceBoost = semproten.getSemprotenPriceBoost();
      int boostedPrice = (int) (originalPrice * (1 + priceBoost));
      System.out.println("[SEMPROTEN TRANSACTION EFFECT] Semproten Level " + semproten.getLevel() + " activated!");
      System.out.println("[SEMPROTEN TRANSACTION EFFECT] Price boost: +" + String.format("%.0f", priceBoost * 100) + "%");
      System.out.println("[SEMPROTEN TRANSACTION EFFECT] Price: " + originalPrice + " → " + boostedPrice);
      return boostedPrice;
    }
    return originalPrice;
  }

  // Check if Semproten can be used during transaction
  public boolean canUseSemproten() {
    Item semproten = getActiveItemByName("Semproten");
    return semproten != null && !semproten.isUsed();
  }

  // Get Semproten price boost percentage for display
  public double getSemprotenPriceBoost() {
    Item semproten = getActiveItemByName("Semproten");
    if (semproten != null) {
      return semproten.getSemprotenPriceBoost();
    }
    return 0.0;
  }

  // Tip - bonus uang ekstra
  public int applyTip(int finalPrice) {
    Item tip = getActiveItemByName("Tip");
    if (tip != null && Math.random() < tip.getTipBonusRate()) {
      int bonus = (int) (finalPrice * 0.15); // 15% bonus
      System.out.println("Tip bonus! Pembeli memberi uang ekstra Rp" + bonus);
      return finalPrice + bonus;
    }
    return finalPrice;
  }

  // Peluit - panggil pembeli tambahan
  public int applyPeluit() {
    Item peluit = getActiveItemByName("Peluit");
    if (peluit != null && !peluit.isUsed()) {
      peluit.markAsUsed();
      int extraBuyers = peluit.getPeluitExtraBuyers();
      System.out.println("Peluit digunakan! " + extraBuyers + " pembeli tambahan datang!");
      return extraBuyers;
    }
    return 0;
  }

  // Cek apakah item tertentu aktif
  public boolean isItemActive(String namaItem) {
    Item item = getActiveItemByName(namaItem);
    return item != null;
  }

  // Aktifkan item
  public boolean activateItem(String namaItem) {
    List<Item> items = player.getInventory().getStokItem();
    for (Item item : items) {
      if (item.getNama().equalsIgnoreCase(namaItem)) {
        item.activate();
        System.out.println("Item " + namaItem + " telah diaktifkan!");
        return true;
      }
    }
    System.out.println("Item " + namaItem + " tidak ditemukan!");
    return false;
  }

  // Deaktifkan item
  public boolean deactivateItem(String namaItem) {
    List<Item> items = player.getInventory().getStokItem();
    for (Item item : items) {
      if (item.getNama().equalsIgnoreCase(namaItem)) {
        item.deactivate();
        System.out.println("Item " + namaItem + " telah dinonaktifkan!");
        return true;
      }
    }
    System.out.println("Item " + namaItem + " tidak ditemukan!");
    return false;
  }

  private Item getActiveItemByName(String nama) {
    List<Item> items = player.getInventory().getStokItem();
    for (Item item : items) {
      if (item.getNama().equalsIgnoreCase(nama) && item.isActive()) {
        return item;
      }
    }
    return null;
  }  // Reset untuk hari baru
  public void resetDailyEffects() {
    jampiActiveToday = false;
    List<Item> items = player.getInventory().getStokItem();
    for (Item item : items) {
      // Reset usage for all items that have daily usage limits
      if (item.isHipnotis() || item.isSemproten() || item.isPeluit()) {
        item.resetUsage();
      }
    }
    System.out.println("Efek item harian telah direset untuk semua item.");
  }

  // Reset untuk transaksi baru
  public void resetTransactionEffects() {
    List<Item> items = player.getInventory().getStokItem();
    for (Item item : items) {
      if (item.isHipnotis()) {
        item.resetUsage();
      }
    }
  }

  // Tampilkan status semua item aktif
  public void displayActiveItems() {
    List<Item> items = player.getInventory().getStokItem();
    System.out.println("=== Item Aktif ===");
    boolean hasActiveItems = false;

    for (Item item : items) {
      if (item.isActive()) {
        System.out.println(item.getDetail());
        hasActiveItems = true;
      }
    }

    if (!hasActiveItems) {
      System.out.println("Tidak ada item yang aktif.");
    }
  }
}
