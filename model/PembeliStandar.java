package model;

public class PembeliStandar extends Pembeli {
    private int lastOffer = 0; // Menyimpan tawaran terakhir
    private int counterCount = 0;
    private static final int MAX_COUNTER = 5;
    private String lastRejectionReason = "";

    public PembeliStandar() {
        super("Standar", 0.8);
        peluangMuncul = 0.7;  // Very high chance to encounter
    }

    @Override
    public int tawarHarga(int hargaAwal) {
        counterCount++;
        // Standar: tawar antara 60%–90% dari hargaAwal
        double minRate = 0.6, maxRate = 0.9;
        int offer = (int)(hargaAwal * (minRate + Math.random() * (maxRate - minRate)));
        // Jika ini counter, jangan turun dari lastOffer
        if (lastOffer > 0 && offer < lastOffer) {
            offer = lastOffer;
        }
        // Batasi agar tidak pernah lebih dari hargaAwal
        if (offer > hargaAwal) {
            offer = hargaAwal;
            lastRejectionReason = "Pembeli Standar menolak karena tawaran terlalu tinggi.";
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
            lastRejectionReason = "Pembeli Standar menolak karena terlalu banyak negosiasi (>5x).";
            return false;
        }
        // Terima jika hargaFinal <= maxTawaran (hasil tawarHarga terakhir)
        return hargaFinal <= maxTawaran;
    }

    @Override
    public boolean chanceAcceptCounter(int hargaPlayer, int hargaPembeli) {
        if (hargaPlayer > hargaPembeli) {
            return Math.random() < 0.15;
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
