package model;

import enums.JenisItem;
import interfaces.Transaksi;
import interfaces.Showable;

public class TokoItem implements Transaksi<JenisItem>, Showable {

    protected Player player;

    public TokoItem(Player player) {
        this.player = player;
    }

    @Override
    public boolean beli(Player player, JenisItem jenis) {
        int harga = jenis.getHarga();

        if (player.getMoney() < harga) {
            System.out.println("Uang tidak cukup untuk membeli item.");
            return false;
        }

        if (player.cariItem(jenis) != null) {
            System.out.println("Item sudah dimiliki.");
            return false;
        }

        player.kurangiMoney(harga);
        player.tambahItem(new Item(jenis));

        System.out.println("Berhasil membeli item: " + jenis.getNama());
        return true;
    }

    public void upgradeItem(Player player, JenisItem jenis) {
        Item item = player.cariItem(jenis);
        if (item == null) {
            System.out.println("Item belum dimiliki.");
            return;
        }

        int biaya = jenis.getBiayaUpgrade();
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
        for (JenisItem jenis : JenisItem.values()) {
            jenis.tampilkanDetail();
        }
    }
}
