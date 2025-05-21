package model;

import enums.JenisItem;
import interfaces.Showable;
import interfaces.Upgrade;

public class Item implements Showable, Upgrade {

    private final JenisItem jenis;
    private boolean isActive;
    private int level;

    private static final int MAX_LEVEL = 5;
    private static final double CHANCE_PER_LEVEL = 0.1;
    private static final double MAX_CHANCE = 0.5;

    public Item(JenisItem jenis) {
        this.jenis = jenis;
        this.level = 1;
        this.isActive = false;
    }

    public String getNama() {
        return jenis.getNama();
    }

    public String getDeskripsi() {
        return jenis.getDeskripsi();
    }

    public int getHarga() {
        return jenis.getHarga();
    }
    
    public int getBiayaUpgrade() {
        return jenis.getBiayaUpgrade();
    }

    public JenisItem getJenis() {
        return jenis;
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
