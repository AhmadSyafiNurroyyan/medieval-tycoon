package model;

public class Peluit extends Item {

        private boolean sudahDipakai;

    public Peluit() { 
        super("Peluit", "Memanggil 1 pembeli tambahan secara instan", 6000, 1200, "icons/peluit.png");
        sudahDipakai = false;
    }

    public boolean gunakan() {
        if (!sudahDipakai) {
            sudahDipakai = true;
            return true;
        }
        return false;
    }
}