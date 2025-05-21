package model;

import enums.JenisBarang;
import interfaces.Transaksi;
import interfaces.Showable;

import java.util.*;

public class Supplier implements Transaksi<JenisBarang>, Showable {

    private Player player;
    private final Set<JenisBarang> stokHariIni;
    private final Random random = new Random();
    private final int jumlahMinStokHarian = 5;
    private final int jumlahMaxStokHarian = JenisBarang.values().length;

    public Supplier() {
        this.stokHariIni = new HashSet<>();
        generateStokHariIni();
    }

    public void generateStokHariIni() {
        stokHariIni.clear();
        List<JenisBarang> semuaJenis = new ArrayList<>(List.of(JenisBarang.values()));
        Collections.shuffle(semuaJenis);

        int jumlahTersedia = jumlahMinStokHarian
                + random.nextInt(jumlahMaxStokHarian - jumlahMinStokHarian + 1);

        for (int i = 0; i < jumlahTersedia; i++) {
            stokHariIni.add(semuaJenis.get(i));
        }
    }

    public boolean tersediaHariIni(JenisBarang barang) {
        return stokHariIni.contains(barang);
    }

    public Set<JenisBarang> getStokHariIni() {
        return new HashSet<>(stokHariIni);
    }

    @Override
    public boolean beli(Player player, JenisBarang jenis) {
        if (!tersediaHariIni(jenis)) {
            System.out.println("Barang tidak tersedia hari ini.");
            return false;
        }

        int harga = jenis.getHarga();
        if (player.getMoney() < harga) {
            System.out.println("Uang tidak cukup untuk membeli barang.");
            return false;
        }

        player.kurangiMoney(harga);
        player.tambahBarang(new Barang(jenis));

        System.out.println("Berhasil membeli: " + jenis.name());
        return true;
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("=== Stok Supplier Hari Ini ===");
        for (JenisBarang barang : stokHariIni) {
            System.out.printf("- %s (Harga: %d)\n", barang.name(), barang.getHarga());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Stok Hari Ini:\n");
        for (JenisBarang jenis : stokHariIni) {
            sb.append("- ").append(jenis.name()).append(" (Harga: ")
                    .append(jenis.getHarga()).append(")\n");
        }
        return sb.toString();
    }
}
