package model;

import interfaces.Showable;
import interfaces.Upgrade;

public class Gerobak implements Upgrade, Showable {

    private int level;
    private static final int KAPASITAS_PERKS = 2;
    private static final int MAX_LEVEL = 5;

    public Gerobak() {
        this.level = 0;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public int getKapasitasBarang() {
        switch (level) {
            case 0:
                return 20;
            case 1:
                return 30;
            case 2:
                return 45;
            case 3:
                return 60;
            case 4:
                return 75;
            case 5:
                return 90;
            default:
                return 20;
        }
    }

    public int getKapasitasItem() {
        if (level <= 1) {
            return 1;
        } else if (level <= 3) {
            return 2;
        } else {
            return 3;
        }
    }

    public int getKapasitasPerks() {
        return KAPASITAS_PERKS;
    }

    public int getBiayaUpgrade() {
        if (isMaxLevel()) {
            return -1;
        }
        return 100_000 + (level * level * 25_000);
    }

    public boolean isMaxLevel() {
        return this.level == MAX_LEVEL;
    }

    @Override
    public boolean upgradeLevel() {
        if (!isMaxLevel()) {
            level++;
            return true;
        }
        return false;
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("Level: " + level);
        System.out.println("Kapasitas Barang: " + getKapasitasBarang());
        System.out.println("Kapasitas Item: " + getKapasitasItem());
        System.out.println("Kapasitas Perks: " + KAPASITAS_PERKS);
        if (!isMaxLevel()) {
            System.out.println("Biaya Upgrade ke level " + (level + 1) + ": " + getBiayaUpgrade());
        } else {
            System.out.println("Gerobak sudah mencapai level maksimal.");
        }
    }
}
