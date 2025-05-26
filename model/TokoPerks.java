package model;

import enums.PerkType;
import interfaces.Transaksi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokoPerks implements Transaksi<Perk> {

    private Map<PerkType, Perk> daftarPerk;

    public TokoPerks() {
        daftarPerk = new HashMap<>();
        daftarPerk.put(PerkType.ELEGAN, new PerksElegan());
        daftarPerk.put(PerkType.CHARMING, new PerksCharming());
        daftarPerk.put(PerkType.ACTIVE, new PerksActive());
    }

    public List<Perk> getDaftarPerk() {
        return new ArrayList<>(daftarPerk.values());
    }

    public Perk getPerkByType(PerkType type) {
        return daftarPerk.get(type);
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
        if (perk == null) {
            System.out.println("Perk tidak valid.");
            return false;
        }

        if (player.hasPerk(perk.getPerkType())) {
            System.out.println("Perk sudah dimiliki.");
            return false;
        }

        if (player.getMoney() >= perk.getHarga()) {
            player.kurangiMoney(perk.getHarga());

            // Clone agar tidak share object
            Perk perkBaru = clonePerk(perk);
            player.addPerk(perkBaru);

            System.out.println("Berhasil membeli perk " + perk.getName());
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

        Perk targetBase = daftarPerk.get(targetType);
        if (targetBase == null) {
            System.out.println("Perk target tidak ditemukan.");
            return false;
        }

        Perk perkTarget = clonePerk(targetBase);
        int biaya = perkTarget.getHarga();

        if (player.getMoney() >= biaya) {
            player.kurangiMoney(biaya);
            perkTarget.resetUpgrade();
            player.removePerk(perkSaatIni);
            player.addPerk(perkTarget);
            System.out.println("Konversi berhasil ke perk " + perkTarget.getName());
            return true;
        } else {
            System.out.println("Uang tidak cukup untuk konversi.");
            return false;
        }
    }

    private Perk clonePerk(Perk perk) {
        if (perk instanceof PerksActive) {
            return new PerksActive((PerksActive) perk);
        } else if (perk instanceof PerksElegan) {
            return new PerksElegan((PerksElegan) perk);
        } else if (perk instanceof PerksCharming) {
            return new PerksCharming((PerksCharming) perk);
        } else {
            throw new IllegalArgumentException("Perk tidak diketahui: " + perk.getClass());
        }
    }

}
