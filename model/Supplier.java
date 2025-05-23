package model;

import interfaces.Showable;
import interfaces.Transaksi;
import java.util.*;

public class Supplier implements Transaksi<String>, Showable {

    private final List<Barang> listBarang = new ArrayList<>();
    private final List<Barang> stokHariIni = new ArrayList<>();
    private final Random random = new Random();
    private final int jumlahMinStokHarian = 5;
    private final int jumlahMaxStokHarian = 10;

    public static final String KATEGORI_BUAH = "Buah";
    public static final String KATEGORI_SAYUR = "Sayur";

    public Supplier() {
        inisialisasiBarang();
        generateStokHariIni();
    }

    private void inisialisasiBarang() {
        listBarang.add(new Barang("Apel", KATEGORI_BUAH, 5000, "apel.png"));
        listBarang.add(new Barang("Pisang", KATEGORI_BUAH, 4000, "pisang.png"));
        listBarang.add(new Barang("Jeruk", KATEGORI_BUAH, 6000, "jeruk.png"));
        listBarang.add(new Barang("Melon", KATEGORI_BUAH, 25000, "melon.png"));
        listBarang.add(new Barang("Mangga", KATEGORI_BUAH, 7000, "mangga.png"));
        listBarang.add(new Barang("Pear", KATEGORI_BUAH, 8000, "pear.png"));
        listBarang.add(new Barang("Salak", KATEGORI_BUAH, 3000, "salak.png"));
        listBarang.add(new Barang("Nanas", KATEGORI_BUAH, 10000, "nanas.png"));
        listBarang.add(new Barang("Semangka", KATEGORI_BUAH, 30000, "semangka.png"));
        listBarang.add(new Barang("Rambutan", KATEGORI_BUAH, 1500, "rambutan.png"));
        listBarang.add(new Barang("Sawi", KATEGORI_SAYUR, 3000, "sawi.png"));
        listBarang.add(new Barang("Bayam", KATEGORI_SAYUR, 3500, "bayam.png"));
        listBarang.add(new Barang("Wortel", KATEGORI_SAYUR, 4500, "wortel.png"));
        listBarang.add(new Barang("Kol", KATEGORI_SAYUR, 5000, "kol.png"));
        listBarang.add(new Barang("Kangkung", KATEGORI_SAYUR, 3000, "kangkung.png"));
        listBarang.add(new Barang("Tomat", KATEGORI_SAYUR, 2000, "tomat.png"));
        listBarang.add(new Barang("Cabai", KATEGORI_SAYUR, 1000, "cabai.png"));
        listBarang.add(new Barang("Terong", KATEGORI_SAYUR, 4000, "terong.png"));
        listBarang.add(new Barang("Kacang Panjang", KATEGORI_SAYUR, 2500, "kacang_panjang.png"));
        listBarang.add(new Barang("Bawang Merah", KATEGORI_SAYUR, 1500, "bawang_merah.png"));
    }

    public void generateStokHariIni() {
        stokHariIni.clear();
        List<Barang> salinanListBarang = new ArrayList<>(listBarang);
        Collections.shuffle(salinanListBarang);
        int jumlahTersedia = jumlahMinStokHarian
                + random.nextInt(jumlahMaxStokHarian - jumlahMinStokHarian + 1);
        for (int i = 0; i < jumlahTersedia; i++) {
            stokHariIni.add(salinanListBarang.get(i));
        }
    }

    public boolean tersediaHariIni(String namaBarang) {
        return stokHariIni.stream().anyMatch(b -> b.getNamaBarang().equalsIgnoreCase(namaBarang));
    }

    public List<Barang> getStokHariIni() {
        return new ArrayList<>(stokHariIni);
    }

    @Override
    public boolean beli(Player player, String namaBarang) {
        for (Barang b : stokHariIni) {
            if (b.getNamaBarang().equalsIgnoreCase(namaBarang)) {
                if (player.getMoney() < b.getHargaBeli()) {
                    System.out.println("Uang tidak cukup untuk membeli barang.");
                    return false;
                }

                player.kurangiMoney(b.getHargaBeli());
                //player.getInventory().tambahBarang(new Barang(b)); 
                System.out.println("Berhasil membeli: " + b.getNamaBarang());
                return true;
            }
        }
        System.out.println("Barang tidak tersedia hari ini.");
        return false;
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("=== Stok Supplier Hari Ini ===");
        for (Barang barang : stokHariIni) {
            System.out.printf("- %s (%s) - Rp %d\n",
                    barang.getNamaBarang(), barang.getKategori(), barang.getHargaBeli());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Stok Hari Ini:\n");
        for (Barang barang : stokHariIni) {
            sb.append("- ").append(barang.getNamaBarang())
                    .append(" (").append(barang.getKategori())
                    .append(", Harga: ").append(barang.getHargaBeli()).append(")\n");
        }
        return sb.toString();
    }
}
