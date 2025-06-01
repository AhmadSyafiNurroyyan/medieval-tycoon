/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

import interfaces.Menawar;
public abstract class Pembeli implements Menawar {
    protected String kategori;
    protected double tawarMultiplier;
    protected double maxTawaran;
    protected double peluangMuncul; 
    
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
        maxTawaran = hargaJual * tawarMultiplier;
        return (int) maxTawaran;
    }

    public double getPeluangMuncul() {
        return peluangMuncul;
    }

    public boolean chanceAcceptCounter(int hargaPlayer, int hargaPembeli) {
        if (hargaPlayer > hargaPembeli) {
            return Math.random() < 0.1;
        }
        return false;
    }

    public static Pembeli buatPembeliAcak() {
        double r = Math.random();
        Pembeli[] kemungkinan = {
            new PembeliTajir(),
            new PembeliMiskin(),
            new PembeliStandar()
        };

        double batas = 0.0;
        for (Pembeli p : kemungkinan) {
            batas += p.getPeluangMuncul();
            if (r < batas) {
                return p;
            }
        }
        return new PembeliStandar();
    }
}
