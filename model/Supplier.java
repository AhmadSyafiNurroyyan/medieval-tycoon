package model;

import enums.JenisBarang;

import java.util.*;

public class Supplier {

    private Set<JenisBarang> stokHariIni;
    private Random random = new Random();

    public Supplier() {
        this.stokHariIni = new HashSet<>();
        generateStokHariIni();
    }

    public void generateStokHariIni() {
        stokHariIni.clear();
        List<JenisBarang> semuaJenis = new ArrayList<>(List.of(JenisBarang.values()));
        Collections.shuffle(semuaJenis);

        int jumlahTersedia = 2 + random.nextInt(semuaJenis.size() - 1); // minimal 2 jenis
        for (int i = 0; i < jumlahTersedia; i++) {
            stokHariIni.add(semuaJenis.get(i));
        }
    }

    public Set<JenisBarang> getStokHariIni() {
        return new HashSet<>(stokHariIni);
    }

    public void tampilkanStokHariIni() {
        System.out.println("=== Stok Supplier Hari Ini ===");
        for (JenisBarang barang : stokHariIni) {
            System.out.printf("%s - Harga: %d\n", barang.name(), barang.getHarga());
        }
    }

    public boolean tersediaHariIni(JenisBarang barang) {
        return stokHariIni.contains(barang);
    }
}
