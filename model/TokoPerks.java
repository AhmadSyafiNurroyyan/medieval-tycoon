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
    }    public boolean buyPerk(Player player, PerkType perkType) {
        // Validasi input
        if (player == null) {
            throw new PerkConversionException("Player tidak boleh null.");
        }
        if (perkType == null) {
            throw new PerkConversionException("Perk type tidak boleh null.");
        }
        
        // Check validations first
        if (player.hasPerk(perkType)) {
            throw new PerkConversionException("Player sudah memiliki perk " + perkType.getNama() + ".");
        }

        if (player.getSemuaPerkDimiliki().size() >= 2) {
            throw new PerkConversionException("Slot perk sudah penuh. Maksimal 2 perk yang dapat dimiliki.");
        }

        Perk targetBase = daftarPerk.get(perkType);
        if (targetBase == null) {
            throw new PerkConversionException("Perk dengan tipe " + perkType.getNama() + " tidak ditemukan di toko.");
        }

        Perk newPerk = clonePerk(targetBase);
        int biaya = newPerk.getHarga();

        if (player.getMoney() >= biaya) {
            player.kurangiMoney(biaya);
            newPerk.resetUpgrade();
            player.addPerk(newPerk);
            return true;
        } else {
            throw new PerkConversionException("Uang tidak cukup untuk membeli perk " + perkType.getNama() + 
                ". Dibutuhkan " + biaya + "G, tersedia " + player.getMoney() + "G.");
        }
    }    public boolean upgrade(Player player, Perk perk) {
        // Validasi input
        if (player == null) {
            throw new PerkConversionException("Player tidak boleh null.");
        }
        if (perk == null) {
            throw new PerkConversionException("Perk tidak boleh null.");
        }
        
        if (!player.getSemuaPerkDimiliki().contains(perk)) {
            throw new PerkConversionException("Perk " + perk.getName() + " tidak dimiliki oleh player ini.");
        }

        if (perk.isMaxLevel()) {
            throw new PerkConversionException("Perk " + perk.getName() + " sudah mencapai level maksimum (" + perk.getLevel() + ").");
        }

        int biaya = perk.getBiayaUpgrade();
        if (player.getMoney() >= biaya) {
            if (perk.upgradeLevel()) {
                player.kurangiMoney(biaya);
                return true;
            } else {
                throw new PerkConversionException("Gagal melakukan upgrade pada perk " + perk.getName() + ".");
            }
        } else {
            throw new PerkConversionException("Uang tidak cukup untuk upgrade perk " + perk.getName() + 
                ". Dibutuhkan " + biaya + "G, tersedia " + player.getMoney() + "G.");
        }
    }public boolean convert(Player player, Perk perkSaatIni, PerkType targetType) {
        // Validasi input
        if (player == null) {
            throw new PerkConversionException("Player tidak boleh null.");
        }
        if (targetType == null) {
            throw new PerkConversionException("Target perk type tidak boleh null.");
        }
        
        Perk targetBase = daftarPerk.get(targetType);
        if (targetBase == null) {
            throw new PerkConversionException("Perk dengan tipe " + targetType.getNama() + " tidak ditemukan di toko.");
        }

        Perk perkTarget = clonePerk(targetBase);
        int biaya = perkTarget.getHarga();

        // Handle null case (direct purchase)
        if (perkSaatIni == null) {
            if (player.getSemuaPerkDimiliki().size() >= 2) {
                throw new PerkConversionException("Slot perk sudah penuh. Maksimal 2 perk per player.");
            }
            if (player.hasPerk(targetType)) {
                throw new PerkConversionException("Player sudah memiliki perk " + targetType.getNama() + ".");
            }
            if (player.getMoney() < biaya) {
                throw new PerkConversionException("Uang tidak cukup. Dibutuhkan " + biaya + "G, tersedia " + player.getMoney() + "G.");
            }
            
            player.kurangiMoney(biaya);
            perkTarget.resetUpgrade();
            player.addPerk(perkTarget);
            return true;
        }

        // Handle conversion case - validasi lebih ketat
        if (!player.getSemuaPerkDimiliki().contains(perkSaatIni)) {
            throw new PerkConversionException("Perk " + perkSaatIni.getName() + " tidak dimiliki oleh player.");
        }
        
        // Validasi level minimum untuk konversi
        if (perkSaatIni.getLevel() <= 0) {
            throw new PerkConversionException(
                "Perk " + perkSaatIni.getName() + " harus di-upgrade minimal ke level 1 sebelum dapat dikonversi."
            );
        }
        
        // Validasi apakah player sudah memiliki target perk
        if (player.hasPerk(targetType)) {
            throw new PerkConversionException(
                "Player sudah memiliki perk " + targetType.getNama() + ". Tidak dapat mengkonversi ke tipe yang sudah dimiliki."
            );
        }
          // Validasi conversion rules dengan pesan error yang lebih detail
        if (!perkSaatIni.canConvertTo(targetType)) {
            PerkType allowedTarget = perkSaatIni.getAllowedConversionTarget();
            String allowedConversionName = allowedTarget != null ? allowedTarget.getNama() : "Tidak ada";
            
            throw new PerkConversionException(
                "Konversi dari " + perkSaatIni.getPerkType().getNama() + " ke " + targetType.getNama() + " tidak diperbolehkan.\n" +
                "Perk " + perkSaatIni.getPerkType().getNama() + " hanya dapat dikonversi ke: " + allowedConversionName
            );
        }
        
        // Validasi uang
        if (player.getMoney() < biaya) {
            throw new PerkConversionException(
                "Uang tidak cukup untuk konversi. Dibutuhkan " + biaya + "G, tersedia " + player.getMoney() + "G."
            );
        }
        
        // Proses konversi
        player.kurangiMoney(biaya);
        perkTarget.resetUpgrade();
        player.removePerk(perkSaatIni);
        player.addPerk(perkTarget);
        return true;
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
