/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

public class PembeliTajir extends Pembeli {
    private boolean hasBargained = false;
    private boolean firstOffer = true;
    private int counterCount = 0;
    private static final int MAX_COUNTER = 10;
    private String lastRejectionReason = "";

    public PembeliTajir() {
        super("Tajir", 0.9);
        peluangMuncul = 0.1;
    }

    @Override
    public int tawarHarga(int hargaAwal) {
        counterCount++;
        if (firstOffer) {
            firstOffer = false;
            if (Math.random() < 0.35) {
                hasBargained = true;
                double rate = 0.9 + Math.random() * 0.1;
                int offer = (int) (hargaAwal * rate);
                int maxOffer = (int) (hargaAwal * 1.2);
                if (offer > maxOffer) {
                    offer = maxOffer;
                    lastRejectionReason = "Pembeli Tajir menolak karena tawaran terlalu tinggi (>20% di atas harga awal).";
                } else {
                    lastRejectionReason = "";
                }
                maxTawaran = offer;
                return offer;
            } else {
                hasBargained = false;
                maxTawaran = hargaAwal;
                lastRejectionReason = "";
                return hargaAwal;
            }
        } else {
            int offer = (int) maxTawaran;
            int maxOffer = (int) (hargaAwal * 1.2);
            if (offer > maxOffer) {
                offer = maxOffer;
                lastRejectionReason = "Pembeli Tajir menolak karena tawaran terlalu tinggi (>20% di atas harga awal).";
            } else {
                lastRejectionReason = "";
            }
            maxTawaran = offer;
            return offer;
        }
    }

    @Override
    public boolean putuskanTransaksi(int hargaFinal) {
        if (counterCount > MAX_COUNTER) {
            lastRejectionReason = "Pembeli Tajir menolak karena terlalu banyak negosiasi (>10x).";
            return false;
        }
        return !hasBargained || hargaFinal <= maxTawaran;
    }

    @Override
    public boolean chanceAcceptCounter(int hargaPlayer, int hargaPembeli) {
        if (hargaPlayer > hargaPembeli) {
            return Math.random() < 0.6;
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
