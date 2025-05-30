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

    public boolean buyPerk(Player player, PerkType perkType) {
        // Check validations first
        if (player.hasPerk(perkType)) {
            return false; // Already owned, return false instead of throwing
        }

        if (player.getSemuaPerkDimiliki().size() >= 2) {
            return false; // No slots available
        }

        Perk targetBase = daftarPerk.get(perkType);
        if (targetBase == null) {
            return false; // Perk not found
        }

        Perk newPerk = clonePerk(targetBase);
        int biaya = newPerk.getHarga();

        if (player.getMoney() >= biaya) {
            player.kurangiMoney(biaya);
            newPerk.resetUpgrade();
            player.addPerk(newPerk);
            return true;
        } else {
            return false; // Not enough money
        }
    }

    public boolean upgrade(Player player, Perk perk) {
        if (perk == null || !player.getSemuaPerkDimiliki().contains(perk)) {
            return false;
        }

        if (perk.isMaxLevel()) {
            return false; // Already at max level
        }

        int biaya = perk.getBiayaUpgrade();
        if (player.getMoney() >= biaya) {
            if (perk.upgradeLevel()) {
                player.kurangiMoney(biaya);
                return true;
            }
        }
        return false;
    }

    public boolean convert(Player player, Perk perkSaatIni, PerkType targetType) {
        Perk targetBase = daftarPerk.get(targetType);
        if (targetBase == null) {
            return false;
        }

        Perk perkTarget = clonePerk(targetBase);
        int biaya = perkTarget.getHarga();

        // Handle null case (direct purchase)
        if (perkSaatIni == null) {
            if (player.getSemuaPerkDimiliki().size() >= 2) {
                return false;
            }
            if (player.hasPerk(targetType)) {
                return false;
            }
            if (player.getMoney() >= biaya) {
                player.kurangiMoney(biaya);
                perkTarget.resetUpgrade();
                player.addPerk(perkTarget);
                return true;
            } else {
                return false;
            }
        }

        // Handle conversion case
        if (!player.getSemuaPerkDimiliki().contains(perkSaatIni)) {
            return false;
        }
        if (!perkSaatIni.canConvertTo(targetType)) {
            throw new PerkConversionException(
                    "Konversi dari " + perkSaatIni.getPerkType() + " ke " + targetType + " tidak diperbolehkan.");
        }
        if (player.hasPerk(targetType)) {
            return false;
        }
        if (player.getMoney() >= biaya) {
            player.kurangiMoney(biaya);
            perkTarget.resetUpgrade();
            player.removePerk(perkSaatIni);
            player.addPerk(perkTarget);
            return true;
        } else {
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
