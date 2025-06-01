/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

import java.util.List;

public class ItemEffectManager {

  private Player player;
  private boolean jampiActiveToday = false;
  private int currentDay = 0;

  public ItemEffectManager(Player player) {
    this.player = player;
  }

  public void setCurrentDay(int day) {
    this.currentDay = day;
  }

  public int getCurrentDay() {
    return currentDay;
  }

  public boolean applyHipnotis(Pembeli pembeli) {
    Item hipnotis = getActiveItemByName("Hipnotis");
    if (hipnotis != null && !hipnotis.isUsed()) {
      double chance = hipnotis.getHipnotisChance();
      if (Math.random() < chance) {
        hipnotis.markAsUsed();
        System.out.println("Hipnotis berhasil! Efek hipnotis membuat pembeli langsung tertarik tanpa menawar.");
        return true;
      }
    }
    return false;
  }

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
  }

  public int applySemproten(int originalPrice) {
    Item semproten = getActiveItemByName("Semproten");
    if (semproten != null) {
      double priceBoost = semproten.getSemprotenPriceBoost();
      int boostedPrice = (int) (originalPrice * (1 + priceBoost));
      System.out.println("[SEMPROTEN TRANSACTION EFFECT] Semproten Level " + semproten.getLevel() + " activated!");
      System.out
          .println("[SEMPROTEN TRANSACTION EFFECT] Price boost: +" + String.format("%.0f", priceBoost * 100) + "%");
      System.out.println("[SEMPROTEN TRANSACTION EFFECT] Price: " + originalPrice + " → " + boostedPrice);
      return boostedPrice;
    }
    return originalPrice;
  }

  public boolean canUseSemproten() {
    Item semproten = getActiveItemByName("Semproten");
    return semproten != null && !semproten.isUsed();
  }

  public double getSemprotenPriceBoost() {
    Item semproten = getActiveItemByName("Semproten");
    if (semproten != null) {
      return semproten.getSemprotenPriceBoost();
    }
    return 0.0;
  }

  public int applyTip(int finalPrice) {
    Item tip = getActiveItemByName("Tip");
    if (tip != null && Math.random() < tip.getTipBonusRate()) {
      int bonus = (int) (finalPrice * 0.15);
      System.out.println("Tip bonus! Pembeli memberi uang ekstra Rp" + bonus);
      return finalPrice + bonus;
    }
    return finalPrice;
  }

  public int applyPeluit(int currentDay) {
    List<Item> items = player.getInventory().getItemDibawa();

    System.out.println("=== DEBUG PELUIT DETECTION ===");
    System.out.println("Items in gerobak count: " + items.size());
    for (Item item : items) {
      System.out.println(
          "  - Item: '" + item.getNama() + "' | isPeluit(): " + item.isPeluit() + " | Quantity: " + item.getQuantity());
    }
    System.out.println("===========================");

    for (Item item : items) {
      if (item.isPeluit() && item.canUse()) {
        if (item.consumeOne()) {
          System.out.println("Peluit digunakan! Sisa: " + item.getQuantity() + "/" + item.getMaxQuantity());
          return item.getPeluitExtraBuyers();
        }
      }
    }
    System.out.println("Tidak ada item Peluit yang bisa digunakan!");
    return 0;
  }

  public boolean usePeluitManually() {
    List<Item> items = player.getInventory().getItemDibawa();
    for (Item item : items) {
      if (item.isPeluit() && item.canUse()) {
        return item.consumeOne();
      }
    }
    return false;
  }

  public boolean isItemActive(String namaItem) {
    Item item = getActiveItemByName(namaItem);
    return item != null;
  }

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
  }

  public void resetDailyEffects() {
    jampiActiveToday = false;
    List<Item> items = player.getInventory().getStokItem();
    for (Item item : items) {
      if (item.isHipnotis() || item.isSemproten() || item.isPeluit()) {
        item.resetUsage();
      }
      if (item.isPeluit()) {
        item.resetPeluitUsage(currentDay);
      }
    }
    System.out.println("Efek item harian telah direset untuk semua item.");
  }

  public String activateJampiIfAvailable() {
    List<Item> items = player.getInventory().getItemDibawa();
    for (Item item : items) {
      if (item.isJampi() && !jampiActiveToday) {
        item.activate();
        jampiActiveToday = true;
        return "Jampi telah diaktifkan otomatis untuk hari ini! Semua penghasilan akan dilipatgandakan (" +
            String.format("%.1fx multiplier", item.getJampiMultiplier()) + ").";
      }
    }
    return null;
  }

  public boolean activateItemForTransaction(String namaItem) {
    if (!namaItem.equalsIgnoreCase("Hipnotis") && !namaItem.equalsIgnoreCase("Semproten")) {
      return false;
    }

    List<Item> items = player.getInventory().getItemDibawa();
    for (Item item : items) {
      if (item.getNama().equalsIgnoreCase(namaItem) && !item.isUsed()) {
        item.activate();
        System.out.println("Item " + namaItem + " telah diaktifkan untuk transaksi berikutnya!");
        return true;
      }
    }
    return false;
  }

  public String getItemActivationMessage(String namaItem) {
    List<Item> items = player.getInventory().getItemDibawa();
    for (Item item : items) {
      if (item.getNama().equalsIgnoreCase(namaItem)) {
        if (item.isHipnotis()) {
          return "Hipnotis diaktifkan! Pembeli akan lebih tertarik untuk langsung membeli tanpa menawar.\n" +
              "Efek: Meningkatkan peluang pembeli langsung membeli tanpa menawar (" +
              String.format("%.0f%% peluang langsung beli", item.getHipnotisChance() * 100) + ").\n" +
              "Kondisi: Dapat digunakan satu kali per transaksi tawar-menawar.\n" +
              "Catatan: Sangat cocok untuk pembeli kategori miskin atau standar.";
        } else if (item.isSemproten()) {
          return "Semproten diaktifkan! Barang-barang akan terlihat lebih segar dan menarik, meningkatkan harga jual.\n"
              +
              "Efek: Menambah kesan barang terlihat lebih fresh, sehingga pembeli bisa menawar harga yang lebih mahal ("
              +
              String.format("+%.0f%% bonus harga", item.getSemprotenPriceBoost() * 100) + ").\n" +
              "Kondisi: Diaktifkan aktif saat transaksi dimulai.\n" +
              "Catatan: Ideal untuk menjual barang murah dengan harga lebih tinggi.";
        } else if (item.isJampi()) {
          return "Jampi diaktifkan! Semua penghasilan hari ini akan dilipatgandakan.\n" +
              "Efek: Melipatgandakan penghasilan dari transaksi pada hari itu (" +
              String.format("%.1fx multiplier", item.getJampiMultiplier()) + ").\n" +
              "Kondisi: Dapat diaktifkan sekali per hari saat berjualan keliling.\n" +
              "Catatan: Efektif jika pemain membawa banyak stok dan bertemu banyak pembeli.";
        } else if (item.isTip()) {
          return "Tip diaktifkan! Pembeli akan memberikan bonus uang ekstra.\n" +
              "Efek: Membuat pembeli kadang-kadang menambahkan sedikit uang ekstra dari harga deal (" +
              String.format("%.0f%% chance bonus tip", item.getTipBonusRate() * 100) + ").\n" +
              "Kondisi: Aktif otomatis saat transaksi berhasil.\n" +
              "Catatan: Efektif untuk pembeli tajir dan standar yang merasa puas.";
        } else if (item.isPeluit()) {
          return "Peluit siap digunakan! Tekan H di map untuk memanggil pembeli tambahan.\n" +
              "Efek: Memanggil pembeli tambahan secara instan (" +
              String.format("+%d pembeli tambahan", item.getPeluitExtraBuyers()) + ").\n" +
              "Kondisi: Sekali pakai (Quantity: " + item.getQuantity() + "/" + item.getMaxQuantity() +
              ", Daily limit: " + item.getPeluitUsesToday(currentDay) + "/" + item.getPeluitDailyLimit() + ").\n" +
              "Catatan: Cocok untuk mempercepat jualan ketika waktu terbatas.";
        }
      }
    }
    return "Item " + namaItem + " berhasil diaktifkan untuk transaksi berikutnya!";
  }

  public boolean canUseItem(String namaItem) {
    List<Item> items = player.getInventory().getItemDibawa();
    for (Item item : items) {
      if (item.getNama().equalsIgnoreCase(namaItem)) {
        if (item.isConsumable()) {
          return item.canUse();
        } else {
          return !item.isUsed();
        }
      }
    }
    return false;
  }

  public void deactivateConsumableItems() {
    List<Item> items = player.getInventory().getItemDibawa();
    for (Item item : items) {
      if (item.isActive() && !item.isConsumable()) {
        item.deactivate();
        item.markAsUsed();
        System.out.println("Item " + item.getNama() + " telah dinonaktifkan setelah transaksi.");
      }
    }
  }

  public void resetTransactionEffects() {
    List<Item> items = player.getInventory().getStokItem();
    for (Item item : items) {
      if (item.isHipnotis()) {
        item.resetUsage();
      }
    }
  }

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
