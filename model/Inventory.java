package model;

import java.util.*;

public class Inventory {

    private final Map<Barang, Integer> stokBarang;
    private final Map<Item, Integer> stokItem;
    private final Map<Barang, Integer> barangDibawa;
    private final Map<Barang, Integer> hargaJualBarang;

    public Inventory() {
        this.stokBarang = new HashMap<>();
        this.stokItem = new HashMap<>();
        this.barangDibawa = new HashMap<>();
        this.hargaJualBarang = new HashMap<>();
    }

    // Tambah barang ke stok
    public void tambahBarang(Barang barang) {
        stokBarang.put(barang, stokBarang.getOrDefault(barang, 0) + 1);
    }

    // Tambah item ke stok
    public void tambahItem(Item item) {
        stokItem.put(item, stokItem.getOrDefault(item, 0) + 1);
    }

    // Ambil semua barang dari stok
    public List<Barang> getStokBarang() {
        List<Barang> result = new ArrayList<>();
        for (Map.Entry<Barang, Integer> entry : stokBarang.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    // Ambil semua item dari stok
    public List<Item> getStokItem() {
        List<Item> result = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : stokItem.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    // Hapus barang busuk dari stok
    public void bersihkanBarangBusuk() {
        Iterator<Map.Entry<Barang, Integer>> iterator = stokBarang.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Barang, Integer> entry = iterator.next();
            if (entry.getKey().isBusuk()) {
                iterator.remove();
            }
        }
    }

    // Kurangi kesegaran semua barang
    public void kurangiKesegaranSemua() {
        for (Barang barang : stokBarang.keySet()) {
            barang.kurangiKesegaran();
        }
    }

    // Hapus 1 buah barang dari stok
    public boolean hapusBarang(Barang barang) {
        if (stokBarang.containsKey(barang)) {
            int count = stokBarang.get(barang);
            if (count > 1) {
                stokBarang.put(barang, count - 1);
            } else {
                stokBarang.remove(barang);
            }
            return true;
        }
        return false;
    }

    // Total semua barang
    public int getJumlahBarang() {
        int total = 0;
        for (int count : stokBarang.values()) {
            total += count;
        }
        return total;
    }

    // Set barang dan jumlah yang akan dibawa berjualan
    public void bawaBarang(Barang barang, int jumlah, int kapasitasGerobak) {
        if (jumlah <= kapasitasTersisa(kapasitasGerobak) && stokBarang.getOrDefault(barang, 0) >= jumlah) {
            barangDibawa.put(barang, barangDibawa.getOrDefault(barang, 0) + jumlah);
            stokBarang.put(barang, stokBarang.get(barang) - jumlah);
            if (stokBarang.get(barang) <= 0) {
                stokBarang.remove(barang);
            }
        }
    }

    // Kapasitas tersisa di gerobak
    public int kapasitasTersisa(int kapasitasGerobak) {
        int total = 0;
        for (int val : barangDibawa.values()) {
            total += val;
        }
        return kapasitasGerobak - total;
    }

    // Atur harga jual barang
    public void setHargaJual(Barang barang, int harga) {
        hargaJualBarang.put(barang, harga);
    }

    public int getHargaJual(Barang barang) {
        return hargaJualBarang.getOrDefault(barang, 0);
    }

    public Map<Barang, Integer> getBarangDibawa() {
        return Collections.unmodifiableMap(barangDibawa);
    }
}
