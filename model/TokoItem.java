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
        listItem.add(new Item("Hipnotis", "Meningkatkan peluang pembeli tidak menawar", 100_000, 40_000));
        listItem.add(new Item("Rayuan", "Meningkatkan peluang pembeli membeli dengan harga tinggi", 150_000, 60_000));
        listItem.add(new Item("Bonus Kesabaran", "Memperlama waktu tunggu pembeli", 120_000, 50_000));
        listItem.add(new Item("Segarkan Dagangan", "Meningkatkan kesegaran barang dagangan", 200_000, 90_000));
        listItem.add(
                new Item("Memperbesar Peluang Beli", "Meningkatkan peluang pembeli untuk JADI beli", 180_000, 70_000));
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
                .tambahItem(new Item(item.getNama(), item.getDeskripsi(), item.getHarga(), item.getBiayaUpgrade()));
        System.out.println("Berhasil membeli item: " + item.getNama());
        return true;
    }

    public void upgradeItem(Player player, String namaItem) {
        Item item = player.getInventory().getItem(namaItem);
        if (item == null) {
            System.out.println("Item belum dimiliki.");
            return;
        }

        int biaya = item.getBiayaUpgrade();
        if (player.getMoney() < biaya) {
            System.out.println("Uang tidak cukup untuk upgrade.");
            return;
        }

        boolean berhasil = item.upgradeLevel();
        if (berhasil) {
            player.kurangiMoney(biaya);
            System.out.println("Upgrade berhasil. Level sekarang: " + item.getLevel());
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
