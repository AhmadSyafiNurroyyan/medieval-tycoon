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
            return 1.0; // No effect
        }

        // Setiap level meningkatkan multiplier sebesar 0.1
        // Level 1: 1.1x, Level 2: 1.2x, dst.
        return 1.0 + (level * 0.1);
    }

    /**
     * Meningkatkan peluang pembeli menerima harga yang ditawarkan
     * 
     * @param hargaFinal harga final yang ditawarkan
     * @param pembeli    pembeli yang sedang bernegosiasi
     * @return true jika pembeli menerima dengan bonus charming
     */
    public boolean applyCharmingEffect(int hargaFinal, Pembeli pembeli) {
        if (!isActive || level == 0) {
            return pembeli.putuskanTransaksi(hargaFinal);
        }

        // Base decision
        boolean baseDecision = pembeli.putuskanTransaksi(hargaFinal);

        if (baseDecision) {
            return true; // Sudah menerima, tidak perlu charming
        }

        // Jika pembeli menolak, berikan peluang kedua dengan charming
        double charmingBonus = level * 0.15; // 15% bonus per level
        return Math.random() < charmingBonus;
    }

    /**
     * Meningkatkan harga maksimal yang bersedia dibayar pembeli
     * 
     * @param maxTawaran tawaran maksimal original
     * @return tawaran maksimal setelah charming effect
     */
    public double applyCharmingToMaxOffer(double maxTawaran) {
        if (!isActive || level == 0) {
            return maxTawaran;
        }

        // Meningkatkan max tawaran sebesar 5% per level
        double bonus = 1.0 + (level * 0.05);
        return maxTawaran * bonus;
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
            int charmingBonus = level * 15;
            int priceBonus = level * 5;
            detailLevel = " (+" + charmingBonus + "% peluang terima, +" + priceBonus + "% max harga)";
        }
        System.out.println("[CHARMING] " + nama + " Lv." + level + ": " + deskripsi + detailLevel);
    }
}
