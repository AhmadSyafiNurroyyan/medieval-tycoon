package model;

public abstract class Pembeli implements Menawar {
    protected String kategori;
    protected double tawarMultiplier;
    protected int maxTawaran;

    public Pembeli(String kategori, double tawarMultiplier) {
        this.kategori = kategori;
        this.tawarMultiplier = tawarMultiplier;
    }

    public abstract int tawarHarga(int hargaAwal);

    public abstract boolean putuskanTransaksi(int hargaFinal);

    public double getMultiplier() {
        return tawarMultiplier;
    }

    public String getKategori() {
        return kategori;
    }

    public int generateOffer(int hargaJual) {
        maxTawaran = (int) (hargaJual * tawarMultiplier);
        return maxTawaran;
    }
}
