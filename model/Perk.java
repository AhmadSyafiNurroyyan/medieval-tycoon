package model;

import interfaces.Upgrade;
import interfaces.Showable;
import enums.PerkType;

public abstract class Perk implements Upgrade, Showable {

    protected String nama;
    protected String deskripsi;
    protected boolean isActive;
    protected int level;
    protected double kesaktian;
    protected int harga;
    protected int biayaUpgrade;
    protected PerkType type;

    public static final int MAX_LEVEL = 5; // maksimal level upgrade 5

    public Perk(String nama, String deskripsi, PerkType type, int harga, double kesaktian, int biayaUpgrade) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.type = type;
        this.harga = harga;
        this.isActive = false;
        this.level = 1;
        this.kesaktian = kesaktian;
        this.biayaUpgrade = biayaUpgrade;
    }

    public String getTypeName() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public PerkType getPerkType() {
        return type;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void resetUpgrade() {
        this.level = 1;
        this.kesaktian = 0.0;
    }

    public boolean isActive() {
        return isActive;
    }

    public double getKesaktian() {
        return kesaktian;
    }

    public int getHarga() {
        return harga;
    }

    public abstract double getPerkEffect();

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public abstract boolean upgradeLevel();

    public boolean canConvertTo(PerkType targetType) {
        if (this.type == PerkType.ELEGAN && targetType == PerkType.CHARMING) {
            return true;
        } else if (this.type == PerkType.CHARMING && targetType == PerkType.ACTIVE) {
            return true;
        } else if (this.type == PerkType.ACTIVE && targetType == PerkType.ELEGAN) {
            return true;
        }
        return false;
    }

    public void convertTo(PerkType targetType) {
        if (!canConvertTo(targetType)) {
            throw new IllegalArgumentException("Konversi dari " + type + " ke " + targetType + " tidak diperbolehkan.");
        }
        this.type = targetType;
        resetUpgrade();
    }

    @Override
    public abstract void tampilkanDetail();
}
