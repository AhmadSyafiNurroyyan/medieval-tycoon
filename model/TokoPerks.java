package model;

import enums.PerkType;
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
        // Cek apakah perk sudah dimiliki
        if (player.getSemuaPerkDimiliki().contains(perk)) {
            System.out.println("Perk sudah dimiliki.");
            return false;
        }

        if (player.getMoney() >= perk.getHarga()) {
            player.kurangiMoney(perk.getHarga());
            player.addPerk(perk);  // Simpan ke koleksi
            System.out.println("Berhasil membeli perk " + perk.getTypeName());
            return true;
        } else {
            System.out.println("Uang tidak cukup.");
            return false;
        }
    }

    public boolean upgrade(Player player, Perk perk) {
        if (perk == null || !player.getSemuaPerkDimiliki().contains(perk)) {
            System.out.println("Perk tidak dimiliki.");
            return false;
        }

        int biaya = perk.getBiayaUpgrade();
        if (player.getMoney() >= biaya) {
            if (perk.upgradeLevel()) {
                player.kurangiMoney(biaya);
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

    public boolean convert(Player player, Perk perkSaatIni, PerkType targetType) {
        if (perkSaatIni == null || !player.getSemuaPerkDimiliki().contains(perkSaatIni)) {
            System.out.println("Perk tidak dimiliki.");
            return false;
        }

        if (!perkSaatIni.canConvertTo(targetType)) {
            System.out.println("Konversi tidak diizinkan.");
            return false;
        }

        Perk targetPerk = daftarPerk.get(targetType);
        if (targetPerk == null) {
            System.out.println("Perk target tidak ditemukan.");
            return false;
        }

        int biaya = targetPerk.getHarga();
        if (player.getMoney() >= biaya) {
            player.kurangiMoney(biaya);
            targetPerk.resetUpgrade(); // Reset upgrade sebelum digunakan
            player.removePerk(perkSaatIni);
            player.addPerk(targetPerk);
            System.out.println("Konversi berhasil ke perk " + targetPerk.getTypeName());
            return true;
        } else {
            System.out.println("Uang tidak cukup untuk konversi.");
            return false;
        }
    }
}
