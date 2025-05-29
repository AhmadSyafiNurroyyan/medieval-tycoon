package model;

import enums.PerkType;

public class PerksActive extends Perk {

    public PerksActive() {
        super(PerkType.ACTIVE.getNama(), "Meningkatkan peluang untuk bertemu pembeli.", PerkType.ACTIVE, 50_000, 1.0,
                20_000, "active.png");
    }

    public PerksActive(PerksActive other) {
        super(other.nama, other.deskripsi, other.type, other.harga, other.kesaktianAwal, other.biayaUpgrade,
                other.iconPath);
        this.level = other.level;
        this.kesaktianSekarang = other.kesaktianSekarang;
        this.isActive = other.isActive;
    }

    // @Override
    // public double getPerkEffect() {
    // // perlu implementasi dari kelas arena jual beli
    // }
    //

    @Override
    public boolean upgradeLevel() {
        if (!isMaxLevel()) {
            level++;
            System.out.println("Level sekarang: " + level); // Tambahkan ini
            kesaktianSekarang += 0.5;
            return true;
        }
        return false;
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("[ACTIVE] " + nama + " Lv." + level + ": " + deskripsi);
    }
}
