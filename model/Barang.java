package model;

import interfaces.Showable;

public class Barang implements Showable {

    private final String nama;
    private final String kategori;
    private final int hargaBeli;
    private int kesegaran;
    private final String iconPath;

    public Barang(String nama, String kategori, int hargaBeli, String iconPath) {
        this.nama = nama;
        this.kategori = kategori;
        this.hargaBeli = hargaBeli;
        this.kesegaran = 100;
        this.iconPath = iconPath;
    }

    public Barang(Barang b) {
        this.nama = b.nama;
        this.kategori = b.kategori;
        this.hargaBeli = b.hargaBeli;
        this.kesegaran = b.kesegaran;
        this.iconPath = b.iconPath;
    }

    public void kurangiKesegaran() {
        kesegaran = kesegaran - 25;
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

    public String getNamaBarang() {
        return nama;
    }

    public int getHargaBeli() {
        return hargaBeli;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getKategori() {
        return kategori;
    }

    @Override
    public void tampilkanDetail() {
        System.out.println(nama);
        System.out.println(iconPath);
        System.out.println("Rp" + hargaBeli);
    }
}
