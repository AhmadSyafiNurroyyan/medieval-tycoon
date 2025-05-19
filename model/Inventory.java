package model;

import java.util.*;
import enums.JenisBarang;

public class Inventory {

    private final List<Barang> daftarBarang;

    public Inventory() {
        this.daftarBarang = new ArrayList<>();
    }

    public void tambahBarang(Barang barang) {
        daftarBarang.add(barang);
    }

    public List<Barang> getDaftarBarang() {
        return new ArrayList<>(daftarBarang);
    }

    public void bersihkanBarangBusuk() {
        Iterator<Barang> iterator = daftarBarang.iterator();
        while (iterator.hasNext()) {
            Barang barang = iterator.next();
            if (barang.isBusuk()) {
                iterator.remove();
            }
        }
    }

    public void kurangiKesegaranSemua() {
        for (Barang barang : daftarBarang) {
            barang.kurangiKesegaran();
        }
    }

    public List<Barang> cariBarang(JenisBarang jenis) {
        List<Barang> hasil = new ArrayList<>();
        for (Barang barang : daftarBarang) {
            if (barang.getJenis() == jenis) {
                hasil.add(barang);
            }
        }
        return hasil;
    }

    public boolean hapusBarang(Barang barang) {
        return daftarBarang.remove(barang);
    }

    public int getJumlahBarang() {
        return daftarBarang.size();
    }
}
