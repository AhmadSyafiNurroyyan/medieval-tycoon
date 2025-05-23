package model;

import interfaces.Showable;
import interfaces.Upgrade;

public class Item implements Showable, Upgrade {

    private final String nama;
    private final String deskripsi;
    private final int harga;
    private final int biayaUpgrade;
    private boolean isActive;
    private int level;

    private static final int MAX_LEVEL = 5;
    private static final double CHANCE_PER_LEVEL = 0.1;
    private static final double MAX_CHANCE = 0.5;

    public Item(String nama, String deskripsi, int harga, int biayaUpgrade) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.biayaUpgrade = biayaUpgrade;
        this.level = 0;
        this.isActive = false;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public int getHarga() {
        return harga;
    }

    public int getBiayaUpgrade() {
        return biayaUpgrade;
    }

    public boolean isActive() {
        return isActive;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public double getChance() {
        return Math.min(level * CHANCE_PER_LEVEL, MAX_CHANCE);
    }

    @Override
    public boolean upgradeLevel() {
        if (level < MAX_LEVEL) {
            level++;
            return true;
        }
        return false;
    }

    @Override
    public void tampilkanDetail() {
        System.out.printf(
                "Potion: %s (Lv.%d) - %s\nChance: %.0f%% | Harga: %d\n",
                getNama(), level, getDeskripsi(), getChance() * 100, getHarga()
        );
    }
}
