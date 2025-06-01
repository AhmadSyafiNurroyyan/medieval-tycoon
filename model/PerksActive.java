/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

import enums.PerkType;
import exceptions.PerkConversionException;

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
        return (double) level;
    }

    public int getAdditionalBuyersCount() {
        if (!isActive || level == 0) {
            System.out.println("[PERKS ACTIVE DEBUG] Perk not active or level 0 - no additional buyers");
            return 0;
        }

        double randomBase = 1 + (Math.random() * 4);
        double perkMultiplier = 0.5 * level;
        double multiplicationResult = randomBase * perkMultiplier;
        double finalResult = randomBase + multiplicationResult;
        int additionalBuyers = (int) finalResult;

        System.out.println("[PERKS ACTIVE DEBUG] ===== BUYER COUNT CALCULATION =====");
        System.out.println("[PERKS ACTIVE DEBUG] Perk Level: " + level);
        System.out.println("[PERKS ACTIVE DEBUG] Step 1 - Random Base: " + String.format("%.2f", randomBase));
        System.out.println("[PERKS ACTIVE DEBUG] Step 2 - Perk Multiplier: " + String.format("%.2f", perkMultiplier)
                + " (0.5 * " + level + ")");
        System.out.println("[PERKS ACTIVE DEBUG] Step 3 - Multiplication: " + String.format("%.2f", randomBase) + " * "
                + String.format("%.2f", perkMultiplier) + " = " + String.format("%.2f", multiplicationResult));
        System.out.println("[PERKS ACTIVE DEBUG] Step 4 - Final Addition: " + String.format("%.2f", randomBase) + " + "
                + String.format("%.2f", multiplicationResult) + " = " + String.format("%.2f", finalResult));
        System.out.println("[PERKS ACTIVE DEBUG] Final Additional Buyers: " + additionalBuyers);
        System.out.println("[PERKS ACTIVE DEBUG] Perk Impact: +" + String.format("%.2f", multiplicationResult)
                + " buyers (+" + String.format("%.1f", (multiplicationResult / randomBase) * 100) + "% increase)");
        System.out.println("[PERKS ACTIVE DEBUG] ==========================================");

        return additionalBuyers;
    }

    public Pembeli buatPembeliDenganPerkActive() {
        if (!isActive || level == 0) {
            return Pembeli.buatPembeliAcak();
        }

        double random = Math.random();
        double peluangTajir, peluangStandar;

        if (level == 1) {
            peluangTajir = 0.10;
            peluangStandar = 0.65;
        } else if (level >= 2 && level <= 3) {
            peluangTajir = 0.15;
            peluangStandar = 0.70;
        } else { // level 4-5
            peluangTajir = 0.20;
            peluangStandar = 0.75;
        }

        System.out.println("[PERKS ACTIVE DEBUG] Level " + level +
                " - Tajir: " + (peluangTajir * 100) + "%, " +
                "Standar: " + (peluangStandar * 100) + "%, " +
                "Miskin: " + ((1 - peluangTajir - peluangStandar) * 100) + "%");

        String buyerType;
        Pembeli result;

        if (random < peluangTajir) {
            buyerType = "PembeliTajir";
            result = new PembeliTajir();
        } else if (random < peluangTajir + peluangStandar) {
            buyerType = "PembeliStandar";
            result = new PembeliStandar();
        } else {
            buyerType = "PembeliMiskin";
            result = new PembeliMiskin();
        }

        System.out.println("[PERKS ACTIVE DEBUG] Generated: " + buyerType +
                " (random: " + String.format("%.3f", random) + ")");

        return result;
    }

    @Override
    public boolean upgradeLevel() {
        if (isMaxLevel()) {
            throw new PerkConversionException(
                    "Perk " + getName() + " sudah mencapai level maksimum (" + MAX_LEVEL + ").");
        }

        if (!isActive) {
            throw new PerkConversionException(
                    "Perk " + getName() + " harus diaktifkan terlebih dahulu sebelum dapat di-upgrade.");
        }

        level++;
        System.out.println("Level sekarang: " + level); // Tambahkan ini
        kesaktianSekarang += 0.5;
        return true;
    }

    @Override
    public void tampilkanDetail() {
        String buyerQualityInfo = "";
        String buyerQuantityInfo = "";

        if (level == 1) {
            buyerQualityInfo = " (10% Tajir, 65% Standar, 25% Miskin)";
            double perkMultiplier = 0.5 * level;
            buyerQuantityInfo = " (Formula: random(1-5) + random*" + String.format("%.1f", perkMultiplier) + ")";
        } else if (level >= 2 && level <= 3) {
            buyerQualityInfo = " (15% Tajir, 70% Standar, 20% Miskin)";
            double perkMultiplier = 0.5 * level;
            buyerQuantityInfo = " (Formula: random(1-5) + random*" + String.format("%.1f", perkMultiplier) + ")";
        } else if (level >= 4) {
            buyerQualityInfo = " (20% Tajir, 75% Standar, 15% Miskin)";
            double perkMultiplier = 0.5 * level;
            buyerQuantityInfo = " (Formula: random(1-5) + random*" + String.format("%.1f", perkMultiplier) + ")";
        }

        String detailLevel = buyerQualityInfo + buyerQuantityInfo;
        System.out.println("[ACTIVE] " + nama + " Lv." + level + ": " + deskripsi + detailLevel);
    }
}
