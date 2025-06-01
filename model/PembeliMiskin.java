/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

public class PembeliMiskin extends Pembeli {
    private int lastOffer = 0;
    private boolean firstOffer = true;
    private int counterCount = 0;
    private static final int MAX_COUNTER = 2;
    private String lastRejectionReason = "";

    public PembeliMiskin() {
        super("Miskin", 0.45);
        peluangMuncul = 0.25;
    }

    @Override
    public int tawarHarga(int hargaAwal) {
        counterCount++;
        double minRate = 0.5, maxRate = 0.6;
        int offer = (int)(hargaAwal * (minRate + Math.random() * (maxRate - minRate)));
        if (lastOffer > 0 && offer < lastOffer) {
            offer = lastOffer;
        }
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
            return false;
        }
        return Math.random() < 0.1;
    }

    @Override
    public boolean chanceAcceptCounter(int hargaPlayer, int hargaPembeli) {
        if (hargaPlayer > hargaPembeli) {
            return Math.random() < 0.05;
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
