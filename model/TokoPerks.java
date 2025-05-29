package model;

import enums.PerkType;
import exceptions.PerkConversionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokoPerks {

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

    public boolean upgrade(Player player, Perk perk) {
        if (perk == null || !player.getSemuaPerkDimiliki().contains(perk)) {
            System.out.println("Perk tidak dimiliki.");
            return false;
        }

        int biaya = perk.getBiayaUpgrade();
        if (player.getMoney() >= biaya) {
            if (perk.upgradeLevel()) {
                player.kurangiMoney(biaya);
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
        Perk targetBase = daftarPerk.get(targetType);
        if (targetBase == null) {
            throw new RuntimeException("Perk target tidak ditemukan.");
        }

        Perk perkTarget = clonePerk(targetBase);
        int biaya = perkTarget.getHarga();

        if (perkSaatIni == null) {
            if (player.getSemuaPerkDimiliki().size() >= 2) {
                throw new RuntimeException("Slot perk sudah penuh.");
            }
            if (player.hasPerk(targetType)) {
                throw new RuntimeException("Perk sudah dimiliki.");
            }
            if (player.getMoney() >= biaya) {
                player.kurangiMoney(biaya);
                perkTarget.resetUpgrade();
                player.addPerk(perkTarget);
                return true;
            } else {
                throw new RuntimeException("Uang tidak cukup untuk konversi.");
            }
        }

        if (!player.getSemuaPerkDimiliki().contains(perkSaatIni)) {
            throw new RuntimeException("Perk yang ingin diganti tidak dimiliki.");
        }
        if (!perkSaatIni.canConvertTo(targetType)) {
            throw new PerkConversionException(
                    "Konversi dari " + perkSaatIni.getPerkType() + " ke " + targetType + " tidak diperbolehkan.");
        }
        if (player.hasPerk(targetType)) {
            throw new RuntimeException("Perk sudah dimiliki.");
        }
        if (player.getMoney() >= biaya) {
            player.kurangiMoney(biaya);
            perkTarget.resetUpgrade();
            player.removePerk(perkSaatIni);
            player.addPerk(perkTarget);
            return true;
        } else {
            throw new RuntimeException("Uang tidak cukup untuk konversi.");
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
