package model;

import model.*;
import enums.PerkType;
import exceptions.*;
import interfaces.Transaksi;

import java.util.HashMap;
import java.util.Map;

public class TokoPerks implements Transaksi<Perk> {

    private Map<PerkType, Perk> daftarPerk;

    public TokoPerks() {
        daftarPerk = new HashMap<>();
        daftarPerk.put(PerkType.ELEGAN, new PerksElegan());
        daftarPerk.put(PerkType.CHARMING, new PerksCharming());
        daftarPerk.put(PerkType.ACTIVE, new PerksActive());
    }

    public void tampilkanDaftarPerks() {
        System.out.println("=== Daftar Perks di Toko ===");
        for (Perk perk : daftarPerk.values()) {
            perk.tampilkanDetail();
            System.out.println("Status: " + (perk.isActive() ? "Sudah dibeli" : "Belum dibeli"));
            System.out.println("----------------------------");
        }
    }

    @Override
    public boolean beli(Player player, Perk perk) {
        if (perk.isActive()) {
            System.out.println("Perk sudah dimiliki.");
            return false;
        }

        if (player.getMoney() >= perk.getHarga()) {
            player.kurangiUang(perk.getHarga());
            perk.activate();
            player.setPerk(perk);
            System.out.println("Berhasil membeli perk " + perk.getTypeName());
            return true;
        } else {
            System.out.println("Uang tidak cukup.");
            return false;
        }
    }

    public boolean upgrade(Player player) {
        Perk perk = player.getPerk();
        if (perk == null || !perk.isActive()) {
            System.out.println("Belum punya perk untuk di-upgrade.");
            return false;
        }

        int biaya = perk.getBiayaUpgrade();
        if (player.getUang() >= biaya) {
            if (perk.upgradeLevel()) {
                player.kurangiUang(biaya);
                System.out.println("Upgrade berhasil ke level " + perk.getLevel());
                return true;
            } else {
                System.out.println("Level sudah maksimum.");
                return false;
            }
        } else {
            System.out.println("Uang tidak cukup untuk upgrade.");
            return false;
        }
    }

    public boolean convert(Player player, PerkType targetType) {
        Perk current = player.getPerk();
        if (current == null || !current.isActive()) {
            System.out.println("Belum punya perk aktif untuk dikonversi.");
            return false;
        }

        if (!current.canConvertTo(targetType)) {
            System.out.println("Konversi tidak diizinkan.");
            return false;
        }

        Perk targetPerk = daftarPerk.get(targetType);
        if (targetPerk == null) {
            System.out.println("Perk target tidak ditemukan.");
            return false;
        }

        int biaya = targetPerk.getHarga();
        if (player.getUang() >= biaya) {
            player.kurangiUang(biaya);
            targetPerk.activate();
            player.setPerk(targetPerk);
            System.out.println("Konversi berhasil ke perk " + targetPerk.getTypeName());
            return true;
        } else {
            System.out.println("Uang tidak cukup untuk konversi.");
            return false;
        }
    }
}
