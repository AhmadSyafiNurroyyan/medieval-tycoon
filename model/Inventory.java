package model;

import enums.JenisBarang;
import java.util.*;

public class Inventory {

    private final Map<Barang, Integer> itemDagangan;
    // private Map<Item, Integer> items; // Remove or implement if needed
    // private List<Perk> perks; // Remove or implement if needed
    private int kapasitasMaks = 100; // Default value, can be set via constructor

    public Inventory() {
        this.itemDagangan = new HashMap<>();
    }

    public void tambahBarang(Barang barang) {
        itemDagangan.put(barang, itemDagangan.getOrDefault(barang, 0) + 1);
    }

    public List<Barang> getitemDagangan() {
        List<Barang> result = new ArrayList<>();
        for (Map.Entry<Barang, Integer> entry : itemDagangan.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public void bersihkanBarangBusuk() {
        Iterator<Map.Entry<Barang, Integer>> iterator = itemDagangan.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Barang, Integer> entry = iterator.next();
            if (entry.getKey().isBusuk()) {
                iterator.remove();
            }
        }
    }

    public void kurangiKesegaranSemua() {
        for (Barang barang : itemDagangan.keySet()) {
            barang.kurangiKesegaran();
        }
    }

    public List<Barang> cariBarang(JenisBarang jenis) {
        List<Barang> hasil = new ArrayList<>();
        for (Barang barang : itemDagangan.keySet()) {
            if (barang.getJenis() == jenis) {
                int count = itemDagangan.get(barang);
                for (int i = 0; i < count; i++) {
                    hasil.add(barang);
                }
            }
        }
        return hasil;
    }

    public boolean hapusBarang(Barang barang) {
        if (itemDagangan.containsKey(barang)) {
            int count = itemDagangan.get(barang);
            if (count > 1) {
                itemDagangan.put(barang, count - 1);
            } else {
                itemDagangan.remove(barang);
            }
            return true;
        }
        return false;
    }

    public int getJumlahBarang() {
        int total = 0;
        for (int count : itemDagangan.values()) {
            total += count;
        }
        return total;
    }
}
