package model;

import enums.PerkType;

public class PerksElegan extends Perk {

    public PerksElegan() {
        super(PerkType.ELEGAN.getNama(), "Meningkatkan peluang untuk bertemu pembeli tajir ", PerkType.ELEGAN, 550_000,
                1.0,
                250_000, "elegan.png");
    }

    public PerksElegan(PerksElegan other) {
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
            kesaktianSekarang += 0.5;
            return true;
        }
        return false;
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("[ELEGAN] " + nama + " Lv." + level + ": " + deskripsi);
    }
}
