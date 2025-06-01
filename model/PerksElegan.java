/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

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
    }    @Override
    public double getPerkEffect() {
        if (!isActive || level == 0) {
            return 0.0;
        }
        return level * 0.10;
    }    
    public Pembeli buatPembeliDenganPerkElegan() {
        if (!isActive || level == 0) {
            return Pembeli.buatPembeliAcak();
        }
        double randomBase = 0.02 + Math.random() * 0.08;
        double perkMultiplier = 0.5 * level;
        double multipliedValue = randomBase * perkMultiplier;
        double finalTajirChance = randomBase + multipliedValue;
        finalTajirChance = Math.min(finalTajirChance, 0.5);
        System.out.println("[PERKS ELEGAN DEBUG] === CALCULATION BREAKDOWN ===");
        System.out.println("[PERKS ELEGAN DEBUG] Level: " + level);
        System.out.println("[PERKS ELEGAN DEBUG] Random base: " + String.format("%.3f", randomBase) + " (" + String.format("%.1f", randomBase * 100) + "%)");
        System.out.println("[PERKS ELEGAN DEBUG] Perk multiplier: " + String.format("%.1f", perkMultiplier));
        System.out.println("[PERKS ELEGAN DEBUG] Multiplication: " + String.format("%.3f", randomBase) + " × " + String.format("%.1f", perkMultiplier) + " = " + String.format("%.3f", multipliedValue));
        System.out.println("[PERKS ELEGAN DEBUG] Final addition: " + String.format("%.3f", randomBase) + " + " + String.format("%.3f", multipliedValue) + " = " + String.format("%.3f", finalTajirChance));
        System.out.println("[PERKS ELEGAN DEBUG] Final tajir chance: " + String.format("%.1f", finalTajirChance * 100) + "%");
        double improvementPercent = (multipliedValue / randomBase) * 100;
        System.out.println("[PERKS ELEGAN DEBUG] Perk impact: +" + String.format("%.1f", improvementPercent) + "% improvement from base");
        double sisaPersentase = 1.0 - finalTajirChance;
        double peluangStandar = sisaPersentase * 0.67;
        double peluangMiskin = sisaPersentase - peluangStandar;
        double random = Math.random();
        String buyerType;
        Pembeli result;
        if (random < finalTajirChance) {
            buyerType = "PembeliTajir";
            result = new PembeliTajir();
        } else if (random < finalTajirChance + peluangStandar) {
            buyerType = "PembeliStandar";
            result = new PembeliStandar();
        } else {
            buyerType = "PembeliMiskin";
            result = new PembeliMiskin();
        }
        System.out.println("[PERKS ELEGAN DEBUG] Probabilities - Tajir: " + String.format("%.1f", finalTajirChance * 100) + 
                          "%, Standar: " + String.format("%.1f", peluangStandar * 100) + 
                          "%, Miskin: " + String.format("%.1f", peluangMiskin * 100) + "%");
        System.out.println("[PERKS ELEGAN DEBUG] Generated: " + buyerType + 
                          " (random: " + String.format("%.3f", random) + ")");
        return result;
    }

    public double applyEleganBonus(Pembeli pembeli) {
        if (!isActive || level == 0 || pembeli == null || !(pembeli instanceof PembeliTajir)) {
            return pembeli != null ? pembeli.getMultiplier() : 1.0;
        }
        double bonus = 1.0 + (level * 0.02);
        return pembeli.getMultiplier() * bonus;
    }

    @Override
    public boolean upgradeLevel() {
        if (!isMaxLevel()) {
            level++;
            System.out.println("Level sekarang: " + level);
            kesaktianSekarang += 0.5;
            return true;
        }
        return false;
    }    @Override
    public void tampilkanDetail() {
        String detailLevel = "";
        if (level > 0) {
            detailLevel = " (Formula: randomBase(2-10%) + randomBase × " + String.format("%.1f", 0.5 * level) + " = enhanced tajir chance)";
        }
        System.out.println("[ELEGAN] " + nama + " Lv." + level + ": " + deskripsi + detailLevel);
    }
}
