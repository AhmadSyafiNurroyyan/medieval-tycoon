package model;

import java.util.*;
import interfaces.InventoryChangeListener;

public class Inventory {

    private final Map<Barang, Integer> stokBarang;
    private final Map<Barang, Integer> barangDibawa;
    private final Map<String, Item> stokItem;
    private final Set<String> itemDibawa;
    private final Map<Map<Barang, Integer>, Integer> hargaJualBarang;
    private final List<InventoryChangeListener> listeners = new ArrayList<>();
    private final List<Perk> daftarPerk;


    public Inventory() {
        this.stokBarang = new HashMap<>();
        this.stokItem = new HashMap<>();
        this.barangDibawa = new HashMap<>();
        this.hargaJualBarang = new HashMap<>();
        this.itemDibawa = new HashSet<>();
        this.daftarPerk = new ArrayList<>();
    }

    public void setDaftarPerk(List<Perk> perks) {
        daftarPerk.clear();
        if (perks != null) {
            daftarPerk.addAll(perks);
        }
    }

    public List<Perk> getDaftarPerk() {
        return Collections.unmodifiableList(daftarPerk);
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
        return new ArrayList<>(stokItem.values());
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
    }

    public void kurangiKesegaranSemua() {
        for (Barang barang : stokBarang.keySet()) {
            barang.kurangiKesegaran();
        }
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
        if (jumlah <= kapasitasBarangTersisa(kapasitasGerobak) && stokBarang.getOrDefault(barang, 0) >= jumlah) {
            barangDibawa.put(barang, barangDibawa.getOrDefault(barang, 0) + jumlah);
            int sisa = stokBarang.get(barang) - jumlah;
            if (sisa > 0) {
                stokBarang.put(barang, sisa);
            } else {
                stokBarang.remove(barang);
            }
            notifyInventoryChanged();
        }
    }

    public void bawaItem(String namaItem, int kapasitasItem) {
        if (itemDibawa.size() < kapasitasItem && stokItem.containsKey(namaItem.toLowerCase())) {
            itemDibawa.add(namaItem.toLowerCase());
            notifyInventoryChanged();
        }
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

    public Map<Barang, Integer> getBarangDibawa() {
        return Collections.unmodifiableMap(barangDibawa);
    }

    public Set<String> getItemDibawa() {
        return Collections.unmodifiableSet(itemDibawa);
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
}
