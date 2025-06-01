/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

import enums.PerkType;
import exceptions.PerkConversionException;
import interfaces.Showable;
import interfaces.Upgrade;

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

    public int getUpgradeCostForLevel(int targetLevel) {
        if (targetLevel <= level || targetLevel > MAX_LEVEL) {
            return 0;
        }

        int baseCost = getBiayaUpgrade();
        int cost = baseCost;

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
    public abstract boolean upgradeLevel();

    public boolean canConvertTo(PerkType targetType) {
        if (this.type == targetType) {
            return false;
        }

        if (this.type == PerkType.ELEGAN && targetType == PerkType.CHARMING) {
            return true;
        } else if (this.type == PerkType.CHARMING && targetType == PerkType.ACTIVE) {
            return true;
        } else if (this.type == PerkType.ACTIVE && targetType == PerkType.ELEGAN) {
            return true;
        }
        return false;
    }

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
        if (targetType == null) {
            throw new PerkConversionException("Target perk type tidak boleh null.");
        }

        if (this.type == targetType) {
            throw new PerkConversionException(
                    "Tidak dapat mengkonversi perk " + this.type.getNama() + " ke tipe yang sama.");
        }

        if (this.level <= 0) {
            throw new PerkConversionException(
                    "Perk " + this.nama + " harus di-upgrade minimal ke level 1 sebelum dapat dikonversi.");
        }

        if (!canConvertTo(targetType)) {
            String allowedConversions = getAllowedConversionsString();
            throw new PerkConversionException(
                    "Konversi dari " + this.type.getNama() + " ke " + targetType.getNama() + " tidak diperbolehkan.\n" +
                            "Konversi yang diizinkan: " + allowedConversions);
        }

        this.type = targetType;
    }

    private String getAllowedConversionsString() {
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
