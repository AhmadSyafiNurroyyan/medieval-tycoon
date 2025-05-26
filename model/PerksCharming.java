package model;

import enums.PerkType;

public class PerksCharming extends Perk {

    public PerksCharming() {
        super(PerkType.CHARMING.getNama(), "Meningkatkan keberhasilan proses tawar menawar", PerkType.CHARMING, 400_000,
                1.0,
                150_000);
    }

    public PerksCharming(PerksCharming other) {
        super(other.nama, other.deskripsi, other.type, other.harga, other.kesaktianAwal, other.biayaUpgrade);
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
        System.out.println("[CHARMING] " + nama + " Lv." + level + ": " + deskripsi);
    }
}
