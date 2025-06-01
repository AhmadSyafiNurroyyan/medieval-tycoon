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
    }    public Pembeli buatPembeliDenganPerkElegan() {
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
    }
}
