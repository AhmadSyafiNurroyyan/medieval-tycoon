/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

import interfaces.Showable;
import java.util.Objects;

public class Barang implements Showable {

    private final String nama;
    private final String kategori;
    private final int hargaBeli;
    private int kesegaran;
    private final String iconPath;

    public Barang(String nama) {
        this.nama = nama;
        this.kategori = "";
        this.hargaBeli = 0;
        this.kesegaran = 0;
        this.iconPath = "";
    }

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
        int oldKesegaran = kesegaran;
        kesegaran = kesegaran - 25;
        System.out.println("Debug: " + nama + " (kategori: " + kategori + ") freshness reduced from " +
                oldKesegaran + " to " + kesegaran);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Barang barang = (Barang) obj;
        return Objects.equals(nama, barang.nama) &&
                Objects.equals(kategori, barang.kategori) &&
                hargaBeli == barang.hargaBeli &&
                Objects.equals(iconPath, barang.iconPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nama, kategori, hargaBeli, iconPath);
    }

}
