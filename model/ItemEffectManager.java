package model;

import java.util.List;

public class ItemEffectManager {

  private Player player;
  private boolean jampiActiveToday = false;
  private int currentDay = 0; // Track current day

  public ItemEffectManager(Player player) {
    this.player = player;
  }

  // Set current day untuk tracking daily effects
  public void setCurrentDay(int day) {
    this.currentDay = day;
  }

  public int getCurrentDay() {
    return currentDay;
  }

  // Hipnotis - meningkatkan chance beli langsung
  public boolean applyHipnotis(Pembeli pembeli) {
    Item hipnotis = getActiveItemByName("Hipnotis");
    if (hipnotis != null && !hipnotis.isUsed()) {
      double chance = hipnotis.getHipnotisChance();
      if (Math.random() < chance) {
        hipnotis.markAsUsed(); // sekali pakai per transaksi
        System.out.println("Hipnotis berhasil! Efek hipnotis membuat pembeli langsung tertarik tanpa menawar.");
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
  } // Semproten - meningkatkan harga jual during transaction based on freshness

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

  // Peluit - panggil pembeli tambahan (consumable item)
  public int applyPeluit(int currentDay) {
    // Cari item Peluit di gerobak/inventory
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
          return item.getPeluitExtraBuyers(); // Jumlah pembeli tambahan berdasarkan level
        }
      }
    }
    System.out.println("Tidak ada item Peluit yang bisa digunakan!");
    return 0;
  }

  // Method untuk menggunakan Peluit secara manual (tombol H)
  public boolean usePeluitManually() {
    List<Item> items = player.getInventory().getItemDibawa();
    for (Item item : items) {
      if (item.isPeluit() && item.canUse()) {
        return item.consumeOne();
      }
    }
    return false;
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
  } // Reset untuk hari baru

  public void resetDailyEffects() {
    jampiActiveToday = false;
    List<Item> items = player.getInventory().getStokItem();
    for (Item item : items) {
      // Reset usage for all items that have daily usage limits
      if (item.isHipnotis() || item.isSemproten() || item.isPeluit()) {
        item.resetUsage();
      }
      if (item.isPeluit()) {
        item.resetPeluitUsage(currentDay);
      }
    }
    System.out.println("Efek item harian telah direset untuk semua item.");
  }

  // Methods untuk aktivasi otomatis Jampi saat create/reset random trigger zone
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

  // Method untuk aktivasi manual item sebelum Start Selling (Hipnotis &
  // Semproten)
  public boolean activateItemForTransaction(String namaItem) {
    if (!namaItem.equalsIgnoreCase("Hipnotis") && !namaItem.equalsIgnoreCase("Semproten")) {
      return false; // Hanya Hipnotis dan Semproten yang bisa diaktifkan manual untuk transaksi
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

  // Get detailed activation message for item
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

  // Method untuk cek apakah item bisa digunakan
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

  // Method untuk deaktivasi item consumable setelah transaksi
  public void deactivateConsumableItems() {
    List<Item> items = player.getInventory().getItemDibawa();
    for (Item item : items) {
      if (item.isActive() && !item.isConsumable()) {
        item.deactivate();
        item.markAsUsed(); // Mark as used untuk non-consumable items
        System.out.println("Item " + item.getNama() + " telah dinonaktifkan setelah transaksi.");
      }
    }
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
