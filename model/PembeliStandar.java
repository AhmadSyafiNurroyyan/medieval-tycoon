package model;

public class PembeliStandar extends Pembeli {
    public PembeliStandar() {
        super("Standar", 0.8); 
        peluangMuncul = 0.6;
    }
    
    @Override
    public int tawarHarga(int hargaAwal) {
        return generateOffer(hargaAwal); 
    }
    
    @Override
    public boolean putuskanTransaksi(int hargaFinal) {
        return hargaFinal <= maxTawaran;
    }
}
