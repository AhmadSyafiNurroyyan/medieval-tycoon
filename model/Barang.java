package model;

import enums.JenisBarang;

public class Barang {

    private final JenisBarang jenis;
    private final int hargaBeli;
    private int hargaJual;
    private int kesegaran;

    public Barang(JenisBarang jenis) {
        this.jenis = jenis;
        this.hargaBeli = jenis.getHarga();
        this.kesegaran = 100;
        this.hargaJual = hargaBeli;
    }

    public void kurangiKesegaran() {
        kesegaran -= 25;
        if (kesegaran < 0) {
            kesegaran = 0;
        }
    }

    public boolean isBusuk() {
        return kesegaran <= 0;
    }

    public int getKesegaran() {
        return kesegaran;
    }

    public JenisBarang getJenis() {
        return jenis;
    }

    public int getHargaBeli() {
        return hargaBeli;
    }

    public int getHargaJual() {
        return hargaJual;
    }

    public boolean setHargaJual(int hargaJualBaru) {
        if (hargaJualBaru >= hargaBeli && hargaJualBaru <= 2 * hargaBeli) {
            this.hargaJual = hargaJualBaru;
            return true;
        }
        return false;
    }
}
