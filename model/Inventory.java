package model;

import interfaces.InventoryChangeListener;
import java.util.*;

public class Inventory {

    private final Map<Barang, Integer> stokBarang;
    private final Map<Barang, Integer> barangDibawa;
    private final Map<String, Item> stokItem;
    private final Set<String> itemDibawa;
    private final List<InventoryChangeListener> listeners = new ArrayList<>();
    private final Map<Barang, Integer> hargaJualBarang = new HashMap<>();
    private Gerobak gerobak;

    public Inventory() {
        this.stokBarang = new HashMap<>();
        this.stokItem = new HashMap<>();
        this.barangDibawa = new HashMap<>();
        this.itemDibawa = new HashSet<>();
        this.gerobak = new Gerobak();
    }

    public void addInventoryChangeListener(InventoryChangeListener listener) {
        listeners.add(listener);
    }

    public void removeInventoryChangeListener(InventoryChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyInventoryChanged() {
        for (InventoryChangeListener l : listeners)
            l.onInventoryChanged();
    }

    public void tambahItem(Item item) {
        stokItem.putIfAbsent(item.getNama().toLowerCase(), item);
        notifyInventoryChanged();
    }

    public void tambahBarang(Barang barang) {
        stokBarang.put(barang, stokBarang.getOrDefault(barang, 0) + 1);
        notifyInventoryChanged();
    }

    public List<Barang> getStokBarang() {
        List<Barang> result = new ArrayList<>();
        for (Map.Entry<Barang, Integer> entry : stokBarang.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public List<Item> getStokItem() {
        List<Item> result = new ArrayList<>();
        for (Map.Entry<String, Item> entry : stokItem.entrySet()) {
            // Only include items that are NOT in the gerobak
            if (!itemDibawa.contains(entry.getKey())) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public List<Item> getItemDibawa() {
        List<Item> itemsDibawa = new ArrayList<>();
        for (String namaItem : itemDibawa) {
            Item item = stokItem.get(namaItem);
            if (item != null) {
                itemsDibawa.add(item);
            }
        }
        return itemsDibawa;
    }

    // Tambahkan method untuk undo bawa item
    public boolean undoBawaItem(String namaItem) {
        String namaItemLower = namaItem.toLowerCase();
        if (itemDibawa.contains(namaItemLower)) {
            itemDibawa.remove(namaItemLower);
            notifyInventoryChanged();
            return true;
        }
        return false;
    }

    public Item getItem(String namaItem) {
        if (namaItem == null)
            return null;
        return stokItem.get(namaItem.toLowerCase());
    }

    public void bersihkanBarangBusuk() {
        Iterator<Map.Entry<Barang, Integer>> iterator = stokBarang.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Barang, Integer> entry = iterator.next();
            if (entry.getKey().isBusuk()) {
                iterator.remove();
            }
        }
        notifyInventoryChanged();
    }

    public void kurangiKesegaranSemua() {
        // Reduce freshness for items in main inventory
        for (Barang barang : stokBarang.keySet()) {
            barang.kurangiKesegaran();
        }
        // Reduce freshness for items in gerobak as well
        for (Barang barang : barangDibawa.keySet()) {
            barang.kurangiKesegaran();
        }
        notifyInventoryChanged();
    }

    public boolean hapusBarang(Barang barang) {
        if (stokBarang.containsKey(barang)) {
            int count = stokBarang.get(barang);
            if (count > 1) {
                stokBarang.put(barang, count - 1);
            } else {
                stokBarang.remove(barang);
            }
            notifyInventoryChanged();
            return true;
        }
        return false;
    }

    public int getJumlahBarang() {
        int total = 0;
        for (int count : stokBarang.values()) {
            total += count;
        }
        return total;
    }

    public boolean hapusItem(String namaItem) {
        boolean removed = stokItem.remove(namaItem.toLowerCase()) != null;
        if (removed)
            notifyInventoryChanged();
        return removed;
    }

    public int getJumlahItem() {
        return stokItem.size();
    }

    public void bawaBarang(Barang barang, int jumlah, int kapasitasGerobak) {
        System.out.println("Debug bawaBarang called:");
        System.out.println("  - Barang: " + barang.getNamaBarang() + " (kesegaran: " + barang.getKesegaran() + ")");
        System.out.println("  - Jumlah requested: " + jumlah);
        System.out.println("  - Kapasitas gerobak: " + kapasitasGerobak);
        System.out.println("  - Kapasitas tersisa: " + kapasitasBarangTersisa(kapasitasGerobak));
        System.out.println("  - Stok available: " + stokBarang.getOrDefault(barang, 0));

        boolean hasCapacity = jumlah <= kapasitasBarangTersisa(kapasitasGerobak);
        boolean hasStock = stokBarang.getOrDefault(barang, 0) >= jumlah;

        System.out.println("  - Has capacity: " + hasCapacity);
        System.out.println("  - Has stock: " + hasStock);

        if (hasCapacity && hasStock) {
            System.out.println("  - SUCCESS: Moving barang to gerobak");
            barangDibawa.put(barang, barangDibawa.getOrDefault(barang, 0) + jumlah);
            int sisa = stokBarang.get(barang) - jumlah;
            if (sisa > 0) {
                stokBarang.put(barang, sisa);
            } else {
                stokBarang.remove(barang);
            }
            notifyInventoryChanged();
        } else {
            System.out.println("  - FAILED: Cannot move barang to gerobak");
            System.out.println("    - Reason: " + (hasCapacity ? "Stock insufficient" : "Capacity full"));
        }
    }

    public boolean bawaItem(String namaItem, int kapasitasGerobak) {
        String namaItemLower = namaItem.toLowerCase();
        System.out.println("bawaItem called: " + namaItem + ", kapasitasGerobak=" + kapasitasGerobak);
        System.out.println("stokItem: " + stokItem.keySet());
        System.out.println("itemDibawa: " + itemDibawa);

        if (itemDibawa.size() >= kapasitasGerobak)
            return false;
        if (!stokItem.containsKey(namaItemLower))
            return false;
        if (itemDibawa.contains(namaItemLower))
            return false; // Sudah dibawa

        itemDibawa.add(namaItemLower);
        System.out.println("itemDibawa after add: " + itemDibawa);
        notifyInventoryChanged();
        return true;
    }

    public void bawaOtomatisSemua(Gerobak g) {
        int sisaBarang = g.getKapasitasBarang();
        int sisaItem = g.getKapasitasItem();

        for (Map.Entry<Barang, Integer> entry : stokBarang.entrySet()) {
            if (sisaBarang <= 0)
                break;
            int jumlah = Math.min(entry.getValue(), sisaBarang);
            bawaBarang(entry.getKey(), jumlah, g.getKapasitasBarang());
            sisaBarang -= jumlah;
        }

        for (String namaItem : stokItem.keySet()) {
            if (sisaItem <= 0)
                break;
            bawaItem(namaItem, g.getKapasitasItem());
            sisaItem--;
        }
        notifyInventoryChanged();
    }

    public int getJumlahItemDiGerobak() {
        return itemDibawa.size();
    }

    public int kapasitasBarangTersisa(int kapasitasGerobak) {
        int total = 0;
        for (int val : barangDibawa.values()) {
            total += val;
        }
        return kapasitasGerobak - total;
    }

    public int kapasitasItemTersisa(int kapasitasItem) {
        return kapasitasItem - itemDibawa.size();
    }

    public void setHargaJual(Barang barang, int harga) {
        hargaJualBarang.put(barang, harga);
    }

    public int getHargaJual(Barang barang) {
        return hargaJualBarang.getOrDefault(barang, 0);
    }

    // Backward compatibility methods (deprecated)
    @Deprecated
    public void setHargaJual(Barang barang, int jumlah, int harga) {
        setHargaJual(barang, harga);
    }

    @Deprecated
    public int getHargaJual(Barang barang, int jumlah) {
        return getHargaJual(barang);
    }

    public void undoBawaBarang(Barang barang, int jumlah) {
        if (barangDibawa.containsKey(barang)) {
            int dibawa = barangDibawa.get(barang);
            int pengurangan = Math.min(jumlah, dibawa);

            stokBarang.put(barang, stokBarang.getOrDefault(barang, 0) + pengurangan);

            if (dibawa > pengurangan) {
                barangDibawa.put(barang, dibawa - pengurangan);
            } else {
                barangDibawa.remove(barang);
            }
            notifyInventoryChanged();
            System.out.println("Undo bawa " + barang.getNamaBarang() + ": -" + pengurangan);
        }
    }

    // Tambah/kurangi barang di gerobak (barangDibawa) secara aman
    public void tambahBarangDibawa(Barang barang, int jumlah) {
        barangDibawa.put(barang, barangDibawa.getOrDefault(barang, 0) + jumlah);
    }

    public void kurangiBarangDibawa(Barang barang, int jumlah) {
        int sisa = barangDibawa.getOrDefault(barang, 0) - jumlah;
        if (sisa > 0) {
            barangDibawa.put(barang, sisa);
        } else {
            barangDibawa.remove(barang);
        }
    }

    public Map<Barang, Integer> getBarangDibawaMutable() {
        return barangDibawa;
    }

    public Gerobak getGerobak() {
        return gerobak;
    }

    public Gerobak setGerobak(Gerobak gerobak) {
        this.gerobak = gerobak;
        return this.gerobak;
    }
}
