/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

import enums.PerkType;

public class PerksCharming extends Perk {
    public PerksCharming() {
        super(PerkType.CHARMING.getNama(), "Meningkatkan keberhasilan proses tawar menawar", PerkType.CHARMING,
                1_200_000,
                1.0,
                750_000, "charming.png");
    }

    public PerksCharming(PerksCharming other) {
        super(other.nama, other.deskripsi, other.type, other.harga, other.kesaktianAwal, other.biayaUpgrade,
                other.iconPath);
        this.level = other.level;
        this.kesaktianSekarang = other.kesaktianSekarang;
        this.isActive = other.isActive;
    }

    @Override
    public double getPerkEffect() {
        if (!isActive || level == 0) {
            return 1.0;
        }
        return 1.0 + (level * 0.1);
    }

    public boolean applyCharmingEffect(int hargaFinal, Pembeli pembeli) {
        if (!isActive || level == 0) {
            return pembeli.putuskanTransaksi(hargaFinal);
        }
        boolean baseDecision = pembeli.putuskanTransaksi(hargaFinal);
        if (baseDecision) {
            System.out.println("[PERKS CHARMING DEBUG] Base decision: ACCEPT - no charming needed");
            return true;
        }
        double randomBase = 0.05 + Math.random() * 0.15;
        double perkMultiplier = 0.5 * level;
        double multipliedValue = randomBase * perkMultiplier;
        double finalCharmingChance = randomBase + multipliedValue;
        finalCharmingChance = Math.min(finalCharmingChance, 0.75);
        System.out.println("[PERKS CHARMING DEBUG] === CALCULATION BREAKDOWN ===");
        System.out.println("[PERKS CHARMING DEBUG] Level: " + level);
        System.out.println("[PERKS CHARMING DEBUG] Base decision: REJECT - applying charming");
        System.out.println("[PERKS CHARMING DEBUG] Random base: " + String.format("%.3f", randomBase) + " (" + String.format("%.1f", randomBase * 100) + "%)");
        System.out.println("[PERKS CHARMING DEBUG] Perk multiplier: " + String.format("%.1f", perkMultiplier));
        System.out.println("[PERKS CHARMING DEBUG] Multiplication: " + String.format("%.3f", randomBase) + " × " + String.format("%.1f", perkMultiplier) + " = " + String.format("%.3f", multipliedValue));
        System.out.println("[PERKS CHARMING DEBUG] Final addition: " + String.format("%.3f", randomBase) + " + " + String.format("%.3f", multipliedValue) + " = " + String.format("%.3f", finalCharmingChance));
        System.out.println("[PERKS CHARMING DEBUG] Final charming chance: " + String.format("%.1f", finalCharmingChance * 100) + "%");
        double improvementPercent = (multipliedValue / randomBase) * 100;
        System.out.println("[PERKS CHARMING DEBUG] Perk impact: +" + String.format("%.1f", improvementPercent) + "% improvement from base");
        boolean charmingSuccess = Math.random() < finalCharmingChance;
        String result = charmingSuccess ? "SUCCESS - buyer convinced!" : "FAILED - buyer still rejects";
        System.out.println("[PERKS CHARMING DEBUG] Charming result: " + result);
        return charmingSuccess;
    }    public double applyCharmingToMaxOffer(double maxTawaran) {
        if (!isActive || level == 0) {
            return maxTawaran;
        }
        double randomBase = 0.02 + Math.random() * 0.08;
        double perkMultiplier = 0.5 * level;
        double multipliedValue = randomBase * perkMultiplier;
        double finalBonus = randomBase + multipliedValue;
        finalBonus = Math.min(finalBonus, 0.3);
        double newMaxOffer = maxTawaran * (1.0 + finalBonus);
        System.out.println("[PERKS CHARMING PRICE DEBUG] === PRICE BOOST CALCULATION ===");
        System.out.println("[PERKS CHARMING PRICE DEBUG] Level: " + level);
        System.out.println("[PERKS CHARMING PRICE DEBUG] Original max offer: " + String.format("%.0f", maxTawaran));
        System.out.println("[PERKS CHARMING PRICE DEBUG] Random base: " + String.format("%.3f", randomBase) + " (" + String.format("%.1f", randomBase * 100) + "%)");
        System.out.println("[PERKS CHARMING PRICE DEBUG] Perk multiplier: " + String.format("%.1f", perkMultiplier));
        System.out.println("[PERKS CHARMING PRICE DEBUG] Multiplication: " + String.format("%.3f", randomBase) + " × " + String.format("%.1f", perkMultiplier) + " = " + String.format("%.3f", multipliedValue));
        System.out.println("[PERKS CHARMING PRICE DEBUG] Final addition: " + String.format("%.3f", randomBase) + " + " + String.format("%.3f", multipliedValue) + " = " + String.format("%.3f", finalBonus));
        System.out.println("[PERKS CHARMING PRICE DEBUG] Final bonus: " + String.format("%.1f", finalBonus * 100) + "%");
        System.out.println("[PERKS CHARMING PRICE DEBUG] New max offer: " + String.format("%.0f", newMaxOffer) + 
                          " (+" + String.format("%.0f", newMaxOffer - maxTawaran) + ")");
        double improvementPercent = (multipliedValue / randomBase) * 100;
        System.out.println("[PERKS CHARMING PRICE DEBUG] Perk impact: +" + String.format("%.1f", improvementPercent) + "% improvement from base");
        return newMaxOffer;
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
            detailLevel = " (Formula: randomBase(5-20%) + randomBase × " + String.format("%.1f", 0.5 * level) + " = enhanced negotiation)";
        }
        System.out.println("[CHARMING] " + nama + " Lv." + level + ": " + deskripsi + detailLevel);
    }
}
