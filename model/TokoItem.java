package model;

import enums.JenisItem;
import interfaces.Transaksi;
import interfaces.Showable;

public class TokoItem implements Transaksi, Showable{
    protected Player player;

    public void upgradeItem(Player player, JenisItem jenis) {
        Item item = player.cariItem(jenis);
        if (item == null) {
            System.out.println("Item belum dimiliki.");
            return;
        }

        int biaya = jenis.getBiayaUpgrade();
        if (player.getUang() < biaya) {
            System.out.println("Uang tidak cukup untuk upgrade.");
            return;
        }

        boolean berhasil = item.upgradeLevel();
        if (berhasil) {
            player.kurangiUang(biaya);
            System.out.println("Upgrade berhasil. Level sekarang: " + item.getLevel());
        } else {
            System.out.println("Item sudah mencapai level maksimal.");
        }
    }

    @Override
    public boolean transaksi(Player player, JenisItem jenis, int jumlah) {
        int totalHarga = jenis.getHarga() * jumlah;

        if (player.getUang() < totalHarga) {
            System.out.println("Uang tidak cukup untuk membeli item.");
            return false;
        }

        Item existing = player.cariItem(jenis);
        if (existing != null) {
            System.out.println("Item sudah dimiliki.");
            return false;
        }

        player.kurangiUang(totalHarga);
        for (int i = 0; i < jumlah; i++) {
            player.tambahItem(new Item(jenis));
        }
        System.out.println("Berhasil membeli " + jumlah + " item: " + jenis.getNama());
        return true;
        }
    
     @Override    
     public void tampilkanDetail(){
        System.out.println("=== Daftar Item di Toko ===");
        for (JenisItem jenis : JenisItem.values()) {
            jenis.tampilkanDetail();
        }
    }
}
