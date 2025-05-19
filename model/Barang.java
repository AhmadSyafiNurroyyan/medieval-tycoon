package model;

import enums.JenisBarang;

public class Barang implements Cloneable {

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
        kesegaran = Math.max(0, kesegaran - 25);
    }

    public boolean isBusuk() {
        return kesegaran <= 0;
    }

    public boolean isSegar() {
        return kesegaran > 50;
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

    public String getKategori() {
        return jenis.getKategori();
    }

    public String getNamaBarang() {
        return jenis.name();
    }

    @Override
    public String toString() {
        return String.format("%s (Kesegaran: %d%%, Harga Jual: %d)",
                jenis.name(), kesegaran, hargaJual);
    }

    @Override
    public Barang clone() throws CloneNotSupportedException {
        try {
            return (Barang) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Barang(this.jenis);
        }
    }
}
