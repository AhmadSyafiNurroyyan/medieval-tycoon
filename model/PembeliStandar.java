/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

public class PembeliStandar extends Pembeli {
    private int lastOffer = 0;
    private int counterCount = 0;
    private static final int MAX_COUNTER = 5;
    private String lastRejectionReason = "";

    public PembeliStandar() {
        super("Standar", 0.8);
        peluangMuncul = 0.7;
    }

    @Override
    public int tawarHarga(int hargaAwal) {
        counterCount++;
        double minRate = 0.6, maxRate = 0.9;
        int offer = (int)(hargaAwal * (minRate + Math.random() * (maxRate - minRate)));
        if (lastOffer > 0 && offer < lastOffer) {
            offer = lastOffer;
        }
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
