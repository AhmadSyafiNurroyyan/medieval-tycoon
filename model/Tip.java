package model;

public class Tip extends Item {

    public Tip() {
        super("Tip", "Pembeli kadang memberi uang ekstra (+10% dari harga akhir)",
                4000, 1000, "icons/tip.png");
        activate(); // aktif otomatis
    }

    public boolean berikanTip() {
        double chance = getChance(); // berdasarkan level
        return Math.random() < chance;
    }

    public int hitungBonus(int hargaDeal) {
        return (int)(hargaDeal * 0.10);
    }
}