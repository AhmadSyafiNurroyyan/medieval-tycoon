package model;

import java.util.Objects;
import interfaces.Showable;
import interfaces.Upgrade;

public class Item implements Showable, Upgrade {

    private final String nama;
    private final String deskripsi;
    private final int harga;
    private final int biayaUpgrade;
    private boolean isActive;
    private int level;
    private final String iconPath;

    private static final int MAX_LEVEL = 5;
    private static final double CHANCE_PER_LEVEL = 0.1;
    private static final double MAX_CHANCE = 0.5;

    public Item(String nama, String deskripsi, int harga, int biayaUpgrade, String iconPath) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.biayaUpgrade = biayaUpgrade;
        this.level = 0;
        this.isActive = false;
        this.iconPath = iconPath;
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

    public String getIconPath() {
        return iconPath;
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

    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public double getMaxChance() {
        return MAX_CHANCE;
    }

    public double getChancePerLevel() {
        return CHANCE_PER_LEVEL;
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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return nama.equals(item.nama);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nama);
    }

    @Override
    public String toString() {
        return String.format("Item[nama=%s, level=%d, aktif=%s]", nama, level, isActive);
    }

    @Override
    public void tampilkanDetail() {
        System.out.println(getDetail());
    }

    public String getDetail() {
        return String.format(
                "Potion: %s (Lv.%d) - %s\nChance: %.0f%% | Harga: %d",
                nama, level, deskripsi, getChance() * 100, harga);
    }
}
