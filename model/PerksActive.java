package model;

import enums.PerkType;

public class PerksActive extends Perk {
    public PerksActive() {
        super(PerkType.ACTIVE.getNama(), "Meningkatkan peluang untuk bertemu pembeli.", PerkType.ACTIVE, 1_000_000, 1.0,
                650_000, "active.png");
    }

    public PerksActive(PerksActive other) {
        super(other.nama, other.deskripsi, other.type, other.harga, other.kesaktianAwal, other.biayaUpgrade,
                other.iconPath);
        this.level = other.level;
        this.kesaktianSekarang = other.kesaktianSekarang;
        this.isActive = other.isActive;
    }

    @Override
    public double getPerkEffect() {
        // Return level sebagai indicator untuk sistem pembeli
        return (double) level;
    }

    /**
     * Membuat pembeli berdasarkan level PerksActive
     * Level 1: 10% tajir, 65% standar, 25% miskin
     * Level 2-3: 15% tajir, 70% standar, 20% miskin
     * Level 4-5: 20% tajir, 75% standar, 15% miskin
     */
    public Pembeli buatPembeliDenganPerkActive() {
        if (!isActive || level == 0) {
            return Pembeli.buatPembeliAcak(); // Default behavior
        }

        double random = Math.random();
        double peluangTajir, peluangStandar, peluangMiskin;

        if (level == 1) {
            peluangTajir = 0.10;
            peluangStandar = 0.65;
            peluangMiskin = 0.25;
        } else if (level >= 2 && level <= 3) {
            peluangTajir = 0.15;
            peluangStandar = 0.70;
            peluangMiskin = 0.20;
        } else { // level 4-5
            peluangTajir = 0.20;
            peluangStandar = 0.75;
            peluangMiskin = 0.15;
        }

        if (random < peluangTajir) {
            return new PembeliTajir();
        } else if (random < peluangTajir + peluangStandar) {
            return new PembeliStandar();
        } else {
            return new PembeliMiskin();
        }
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
        if (level == 1) {
            detailLevel = " (10% Tajir, 65% Standar, 25% Miskin)";
        } else if (level >= 2 && level <= 3) {
            detailLevel = " (15% Tajir, 70% Standar, 20% Miskin)";
        } else if (level >= 4) {
            detailLevel = " (20% Tajir, 75% Standar, 15% Miskin)";
        }
        System.out.println("[ACTIVE] " + nama + " Lv." + level + ": " + deskripsi + detailLevel);
    }
}
