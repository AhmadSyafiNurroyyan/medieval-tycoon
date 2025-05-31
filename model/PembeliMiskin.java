package model;

public class PembeliMiskin extends Pembeli {
    private int lastOffer = 0;
    private boolean firstOffer = true;
    private int counterCount = 0;
    private static final int MAX_COUNTER = 2;
    private String lastRejectionReason = "";

    public PembeliMiskin() {
        super("Miskin", 0.45);
        peluangMuncul = 0.25;  // Moderate chance to encounter
    }

    @Override
    public int tawarHarga(int hargaAwal) {
        counterCount++;
        // Miskin: tawar antara 50%–60% dari hargaAwal
        double minRate = 0.5, maxRate = 0.6;
        int offer = (int)(hargaAwal * (minRate + Math.random() * (maxRate - minRate)));
        // Jangan turun dari lastOffer jika ini counter
        if (lastOffer > 0 && offer < lastOffer) {
            offer = lastOffer;
        }
        // Batasi agar tidak pernah lebih dari hargaAwal
        if (offer > hargaAwal) {
            offer = hargaAwal;
            lastRejectionReason = "Pembeli Miskin menolak karena tawaran terlalu tinggi.";
        } else {
            lastRejectionReason = "";
        }
        lastOffer = offer;
        maxTawaran = offer;
        return offer;
    }

    @Override
    public boolean putuskanTransaksi(int hargaFinal) {
        if (counterCount > MAX_COUNTER) {
            lastRejectionReason = "Pembeli Miskin menolak karena terlalu banyak negosiasi (>2x).";
            return false;
        }
        if (firstOffer) {
            firstOffer = false;
            return false; // Always refuse first offer
        }
        // After at least one round, rarely completes the purchase (~10% chance)
        return Math.random() < 0.1;
    }

    @Override
    public boolean chanceAcceptCounter(int hargaPlayer, int hargaPembeli) {
        if (hargaPlayer > hargaPembeli) {
            return Math.random() < 0.05; // 5% chance
        }
        return false;
    }

    public String getLastRejectionReason() {
        return lastRejectionReason;
    }

    public void resetCounter() {
        counterCount = 0;
        lastRejectionReason = "";
    }
}
