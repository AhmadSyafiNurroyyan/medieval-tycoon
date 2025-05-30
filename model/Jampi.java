package model;

public class Jampi extends Item {

    private boolean sudahAktifHariIni;

    public Jampi() {
        super("Jampi", "Melipatgandakan penghasilan dari transaksi hari ini", 8000, 1500, "icons/jampi.png");
        sudahAktifHariIni = false;
    }

    public boolean aktifkanHariIni() {
        if (!sudahAktifHariIni) {
            sudahAktifHariIni = true;
            return true;
        }
        return false;
    }

    public void resetHarian() {
        sudahAktifHariIni = false;
    }
}