package model;

import interfaces.Upgrade;
import interfaces.Showable;
import enums.PerkType;
import exceptions.PerkConversionException;

public abstract class Perk implements Upgrade, Showable {

    protected String nama;
    protected String deskripsi;
    protected boolean isActive;
    protected int level;
    protected double kesaktianAwal;
    protected double kesaktianSekarang;
    protected int harga;
    protected int biayaUpgrade;
    protected PerkType type;
    protected final String iconPath;

    public static final int MAX_LEVEL = 5;

    public Perk(String nama, String deskripsi, PerkType type, int harga, double kesaktianAwal, int biayaUpgrade,
            String iconPath) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.type = type;
        this.harga = harga;
        this.isActive = false;
        this.level = 0;
        this.kesaktianAwal = kesaktianAwal;
        this.kesaktianSekarang = kesaktianAwal;
        this.biayaUpgrade = biayaUpgrade;
        this.iconPath = iconPath;
    }

    public String getName() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public PerkType getPerkType() {
        return type;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void resetUpgrade() {
        this.level = 0;
        this.kesaktianSekarang = kesaktianAwal;
    }

    public boolean isActive() {
        return isActive;
    }

    public double getKesaktianAwal() {
        return kesaktianAwal;
    }

    public double getKesaktianSekarang() {
        return kesaktianSekarang;
    }

    public int getHarga() {
        return harga;
    }

    public int getBiayaUpgrade() {
        return biayaUpgrade;
    }

    /**
     * Menghitung biaya upgrade untuk level tertentu dengan progressive pricing
     * 
     * @param targetLevel level target yang ingin dicapai
     * @return biaya upgrade untuk mencapai level tersebut
     */
    public int getUpgradeCostForLevel(int targetLevel) {
        if (targetLevel <= level || targetLevel > MAX_LEVEL) {
            return 0;
        }

        int baseCost = getBiayaUpgrade();
        int cost = baseCost;

        // Calculate cost dengan 25% increase per level
        for (int i = level; i < targetLevel - 1; i++) {
            cost = (int) (cost * 1.25);
        }

        return cost;
    }

    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }

    public abstract double getPerkEffect();

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public abstract boolean upgradeLevel();    public boolean canConvertTo(PerkType targetType) {
        // Validasi sama type tidak boleh convert ke dirinya sendiri
        if (this.type == targetType) {
            return false;
        }
        
        // Aturan konversi berdasarkan spesifikasi:
        // • Elegan dapat diubah menjadi charming, tetapi tidak bisa menjadi active
        // • Charming dapat diubah menjadi active, tapi tidak bisa menjadi elegan  
        // • Active dapat diubah menjadi elegan, tetapi tidak bisa menjadi charming
        if (this.type == PerkType.ELEGAN && targetType == PerkType.CHARMING) {
            return true;
        } else if (this.type == PerkType.CHARMING && targetType == PerkType.ACTIVE) {
            return true;
        } else if (this.type == PerkType.ACTIVE && targetType == PerkType.ELEGAN) {
            return true;
        }
        return false;
    }

    /**
     * Mendapatkan perk type yang dapat dikonversi dari perk ini
     * @return PerkType yang dapat dikonversi, atau null jika tidak ada
     */
    public PerkType getAllowedConversionTarget() {
        switch (this.type) {
            case ELEGAN:
                return PerkType.CHARMING;
            case CHARMING:
                return PerkType.ACTIVE;
            case ACTIVE:
                return PerkType.ELEGAN;
            default:
                return null;
        }
    }

    public void convertTo(PerkType targetType) {
        // Validasi null check
        if (targetType == null) {
            throw new PerkConversionException("Target perk type tidak boleh null.");
        }
        
        // Validasi same type
        if (this.type == targetType) {
            throw new PerkConversionException("Tidak dapat mengkonversi perk " + this.type.getNama() + " ke tipe yang sama.");
        }
        
        // Validasi level requirement - hanya boleh convert jika level > 0
        if (this.level <= 0) {
            throw new PerkConversionException("Perk " + this.nama + " harus di-upgrade minimal ke level 1 sebelum dapat dikonversi.");
        }
        
        // Validasi conversion rules
        if (!canConvertTo(targetType)) {
            String allowedConversions = getAllowedConversionsString();
            throw new PerkConversionException(
                "Konversi dari " + this.type.getNama() + " ke " + targetType.getNama() + " tidak diperbolehkan.\n" +
                "Konversi yang diizinkan: " + allowedConversions
            );
        }
        
        this.type = targetType;
    }      private String getAllowedConversionsString() {
        switch (this.type) {
            case ELEGAN:
                return "Elegan hanya dapat dikonversi ke Charming";
            case CHARMING:
                return "Charming hanya dapat dikonversi ke Active";
            case ACTIVE:
                return "Active hanya dapat dikonversi ke Elegan";
            default:
                return "Tidak ada konversi yang diizinkan";
        }
    }

    @Override
    public String toString() {
        return nama + " (Lv. " + level + ")";
    }

    @Override
    public abstract void tampilkanDetail();
}
