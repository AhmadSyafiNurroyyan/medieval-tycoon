package model;

public class PembeliMiskin extends Pembeli {
    public PembeliMiskin() {
        super("Miskin", 0.45); 
        peluangMuncul = 0.3;
    }

    @Override
    public int tawarHarga(int hargaAwal) {
        return generateOffer(hargaAwal); 
    }

    @Override
    public boolean putuskanTransaksi(int hargaFinal) {
        if (hargaFinal <= maxTawaran) {
            return true; 
        }
        return Math.random() < 0.1;
    }
}
