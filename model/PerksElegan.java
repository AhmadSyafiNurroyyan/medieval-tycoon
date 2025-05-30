package model;

import enums.PerkType;

public class PerksElegan extends Perk {
    public PerksElegan() {
        super(PerkType.ELEGAN.getNama(), "Meningkatkan peluang untuk bertemu pembeli tajir ", PerkType.ELEGAN,
                1_500_000,
                1.0,
                900_000, "elegan.png");
    }

    public PerksElegan(PerksElegan other) {
        super(other.nama, other.deskripsi, other.type, other.harga, other.kesaktianAwal, other.biayaUpgrade,
                other.iconPath);
        this.level = other.level;
        this.kesaktianSekarang = other.kesaktianSekarang;
        this.isActive = other.isActive;
    }

    @Override
    public double getPerkEffect() {
        if (!isActive || level == 0) {
            return 0.0; // No bonus
        }

        // Setiap level menambah 8% peluang bertemu pembeli tajir
        return level * 0.08;
    }

    /**
     * Membuat pembeli dengan peluang tajir yang ditingkatkan
     * Level 1: +8% peluang tajir (jadi 18% dari 10%)
     * Level 2: +16% peluang tajir (jadi 26%)
     * Level 3: +24% peluang tajir (jadi 34%)
     * Level 4: +32% peluang tajir (jadi 42%)
     * Level 5: +40% peluang tajir (jadi 50%)
     */
    public Pembeli buatPembeliDenganPerkElegan() {
        if (!isActive || level == 0) {
            return Pembeli.buatPembeliAcak(); // Default behavior
        }

        double bonusTajir = getPerkEffect();
        double peluangTajir = 0.1 + bonusTajir; // Base 10% + bonus

        // Kurangi peluang miskin dan standar secara proporsional
        double sisaPersentase = 1.0 - peluangTajir;
        double peluangStandar = sisaPersentase * 0.67; // ~67% dari sisa
        double peluangMiskin = sisaPersentase * 0.33; // ~33% dari sisa

        double random = Math.random();

        if (random < peluangTajir) {
            return new PembeliTajir();
        } else if (random < peluangTajir + peluangStandar) {
            return new PembeliStandar();
        } else {
            return new PembeliMiskin();
        }
    }

    /**
     * Meningkatkan multiplier pembeli tajir yang sudah ada
     * 
     * @param pembeli pembeli yang akan dimodifikasi
     * @return multiplier baru jika pembeli adalah tajir
     */
    public double applyEleganBonus(Pembeli pembeli) {
        if (!isActive || level == 0 || !(pembeli instanceof PembeliTajir)) {
            return pembeli.getMultiplier();
        }

        // Pembeli tajir mendapat bonus 2% per level
        double bonus = 1.0 + (level * 0.02);
        return pembeli.getMultiplier() * bonus;
    }

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
        String detailLevel = "";
        if (level > 0) {
            int bonusPersentase = level * 8;
            int totalTajir = 10 + bonusPersentase;
            detailLevel = " (+" + bonusPersentase + "% peluang tajir, total " + totalTajir + "%)";
        }
        System.out.println("[ELEGAN] " + nama + " Lv." + level + ": " + deskripsi + detailLevel);
    }
}
