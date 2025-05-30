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
    private boolean isUsed;

    private static final int MAX_LEVEL = 5;

    public Item(String nama, String deskripsi, int harga, int biayaUpgrade, String iconPath) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.biayaUpgrade = biayaUpgrade;
        this.level = 1;
        this.isActive = false;
        this.iconPath = iconPath;
        this.isUsed = false;
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

    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void markAsUsed() {
        this.isUsed = true;
    }

    public void resetUsage() {
        this.isUsed = false;
    }

    // Efek spesifik setiap item berdasarkan level
    public double getHipnotisChance() {
        return 0.3 + (level * 0.1); // 30% + 10% per level (max 80% di level 5)
    }

    public double getJampiMultiplier() {
        return 1.5 + (level * 0.3); // 1.5x + 0.3x per level (max 3x di level 5)
    }

    public double getSemprotenPriceBoost() {
        return 0.15 + (level * 0.05); // 15% + 5% per level (max 40% di level 5)
    }

    public double getTipBonusRate() {
        return 0.08 + (level * 0.04); // 8% + 4% per level (max 28% di level 5)
    }

    public int getPeluitExtraBuyers() {
        return level; // 1 pembeli per level (max 5 pembeli di level 5)
    }

    // Method untuk cek tipe item berdasarkan nama
    public boolean isHipnotis() {
        return nama.equalsIgnoreCase("Hipnotis");
    }

    public boolean isJampi() {
        return nama.equalsIgnoreCase("Jampi");
    }

    public boolean isSemproten() {
        return nama.equalsIgnoreCase("Semproten");
    }

    public boolean isTip() {
        return nama.equalsIgnoreCase("Tip");
    }

    public boolean isPeluit() {
        return nama.equalsIgnoreCase("Peluit");
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
        String efekDetail = getEfekDetail();
        return String.format(
                "%s (Lv.%d/%d) - %s\n%s\nHarga: Rp%,d | Upgrade: Rp%,d",
                nama, level, MAX_LEVEL, deskripsi, efekDetail, harga, biayaUpgrade);
    }

    private String getEfekDetail() {
        if (isHipnotis()) {
            return String.format("Efek: %.0f%% chance langsung beli", getHipnotisChance() * 100);
        } else if (isJampi()) {
            return String.format("Efek: %.1fx multiplier penghasilan", getJampiMultiplier());
        } else if (isSemproten()) {
            return String.format("Efek: +%.0f%% harga jual", getSemprotenPriceBoost() * 100);
        } else if (isTip()) {
            return String.format("Efek: %.0f%% chance bonus tip", getTipBonusRate() * 100);
        } else if (isPeluit()) {
            return String.format("Efek: +%d pembeli tambahan", getPeluitExtraBuyers());
        }
        return "Efek tidak diketahui";
    }
}
