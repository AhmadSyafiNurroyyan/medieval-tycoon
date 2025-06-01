package model;

import interfaces.Transaksi;
import interfaces.Showable;
import java.util.*;

public class TokoItem implements Transaksi<Item>, Showable {

    protected Player player;
    private final List<Item> listItem = new ArrayList<>();

    public TokoItem(Player player) {
        this.player = player;
        inisialisasiItem();
    }

    private void inisialisasiItem() {
        listItem.add(
                new Item("Hipnotis", "Meningkatkan peluang pembeli langsung membeli tanpa menawar",
                        50_000, 25_000, "hipnotis.png"));
        listItem.add(
                new Item("Jampi", "Melipatgandakan penghasilan dari transaksi hari ini",
                        75_000, 35_000, "rayuan.png"));
        listItem.add(
                new Item("Peluit", "Memanggil pembeli tambahan secara instan",
                        60_000, 30_000, "bonus_kesabaran.png"));
        listItem.add(new Item("Semproten", "Meningkatkan harga jual saat transaksi berdasarkan kesegaran barang",
                80_000, 40_000, "segarkan_dagangan.png"));
        listItem.add(
                new Item("Tip", "Pembeli kadang memberi uang ekstra",
                        65_000, 32_000, "peluang_beli.png"));
    }

    @Override
    public boolean beli(Player player, Item item) {
        if (!listItem.contains(item)) {
            System.out.println("Item tidak tersedia di toko.");
            return false;
        }

        if (player.getMoney() < item.getHarga()) {
            System.out.println("Uang tidak cukup untuk membeli item.");
            return false;
        }

        boolean sudahPunya = player.getInventory().getStokItem().stream()
                .anyMatch(i -> i.getNama().equalsIgnoreCase(item.getNama()));

        if (sudahPunya) {
            System.out.println("Item sudah dimiliki.");
            return false;
        }

        player.kurangiMoney(item.getHarga());
        player.getInventory()
                .tambahItem(new Item(item.getNama(), item.getDeskripsi(), item.getHarga(), item.getBiayaUpgrade(),
                        item.getIconPath()));
        System.out.println("Berhasil membeli item: " + item.getNama());
        return true;
    }

    public boolean beliItem(Player player, String namaItem) {
        for (Item item : listItem) {
            if (item.getNama().equalsIgnoreCase(namaItem)) {
                return beli(player, item); // panggil method beli yang sudah ada
            }
        }
        System.out.println("Item dengan nama '" + namaItem + "' tidak ditemukan di toko.");
        return false;
    }

    public void upgradeItem(Player player, String namaItem) {
        Item item = player.getInventory().getItem(namaItem);
        if (item == null) {
            System.out.println("Item belum dimiliki.");
            return;
        }
        int biaya = item.getBiayaUpgrade() * item.getLevel(); // biaya meningkat per level
        if (player.getMoney() < biaya) {
            System.out.println("Uang tidak cukup untuk upgrade. Dibutuhkan: Rp" + biaya);
            return;
        }

        if (item.isMaxLevel()) {
            System.out.println("Item sudah mencapai level maksimal (" + item.getMaxLevel() + ").");
            return;
        }

        boolean berhasil = item.upgradeLevel();
        if (berhasil) {
            player.kurangiMoney(biaya);
            System.out.println("Upgrade berhasil! " + item.getNama() + " sekarang level " + item.getLevel());
            System.out.println("Efek baru: " + item.getDetail());
        } else {
            System.out.println("Item sudah mencapai level maksimal.");
        }
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("=== Daftar Item di Toko ===");
        for (Item item : listItem) {
            item.tampilkanDetail();
        }
    }

    public List<Item> getDaftarItem() {
        return new ArrayList<>(listItem);
    }
}
