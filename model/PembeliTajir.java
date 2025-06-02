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
    private String lastRejectionReason = "";    private static final double CHANCE_DIRECT_DEAL = 0.4; // 40% chance for direct deal
    private static final double MIN_BARGAIN_RATE = 0.75; // Minimum 75% of original price when bargaining
    private static final double MAX_BARGAIN_RATE = 0.95; // Maximum 95% of original price when bargaining
    
    public PembeliTajir() {
        super("Tajir", 0.9);
        peluangMuncul = 0.1;
    }

    public boolean willAcceptDirectly(int hargaPlayer) {
        // Every PembeliTajir has a chance to accept directly on every interaction
        return Math.random() < CHANCE_DIRECT_DEAL;
    }

    @Override
    public int tawarHarga(int hargaAwal) {
        counterCount++;
        if (firstOffer) {
            firstOffer = false;
            // Always bargain in tawarHarga - direct acceptance is handled separately
            hasBargained = true;
            // Offer between 75% to 95% of the original price
            double rate = MIN_BARGAIN_RATE + Math.random() * (MAX_BARGAIN_RATE - MIN_BARGAIN_RATE);
            int offer = (int) (hargaAwal * rate);
            
            // Ensure offer doesn't exceed reasonable limits
            int maxOffer = (int) (hargaAwal * 1.1); // Max 110% of original price
            if (offer > maxOffer) {
                offer = maxOffer;
                lastRejectionReason = "Pembeli Tajir menolak karena tawaran terlalu tinggi.";
            } else {
                lastRejectionReason = "";
            }
            maxTawaran = offer;
            return offer;
        } else {
            // Subsequent offers - maintain the same offer
            int offer = (int) maxTawaran;
            int maxOffer = (int) (hargaAwal * 1.1);
            if (offer > maxOffer) {
                offer = maxOffer;
                lastRejectionReason = "Pembeli Tajir menolak karena tawaran terlalu tinggi.";
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
        firstOffer = true;  // Reset first offer flag for fresh negotiations
        hasBargained = false;  // Reset bargaining status
    }
}
