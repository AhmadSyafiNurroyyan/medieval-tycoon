package enums;

import interfaces.Showable;

public enum JenisBarang implements Showable {

    APEL("Buah", 5000, "apel.png"),
    PISANG("Buah", 4000, "pisang.png"),
    JERUK("Buah", 6000, "jeruk.png"),
    MELON("Buah", 25000, "melon.png"),
    MANGGA("Buah", 7000, "mangga.png"),
    PEAR("Buah", 8000, "pear.png"),
    SALAK("Buah", 3000, "salak.png"),
    NANAS("Buah", 10000, "nanas.png"),
    SEMANGKA("Buah", 30000, "semangka.png"),
    RAMBUTAN("Buah", 1500, "rambutan.png"),
    SAWI("Sayur", 3000, "sawi.png"),
    BAYAM("Sayur", 3500, "bayam.png"),
    WORTEL("Sayur", 4500, "wortel.png"),
    KOL("Sayur", 5000, "kol.png"),
    KANGKUNG("Sayur", 3000, "kangkung.png"),
    TOMAT("Sayur", 2000, "tomat.png"),
    CABAI("Sayur", 1000, "cabai.png"),
    TERONG("Sayur", 4000, "terong.png"),
    KACANG_PANJANG("Sayur", 2500, "kacang_panjang.png"),
    BAWANG_MERAH("Sayur", 1500, "bawang_merah.png");

    private final String kategori;
    private final int harga;
    private final String iconPath;

    JenisBarang(String kategori, int harga, String iconPath) {
        this.kategori = kategori;
        this.harga = harga;
        this.iconPath = iconPath;
    }

    public String getKategori() {
        return kategori;
    }

    public int getHarga() {
        return harga;
    }

    public String getIconPath() {
        return iconPath;
    }

    public boolean isBuah() {
        return "Buah".equals(kategori);
    }

    public boolean isSayur() {
        return "Sayur".equals(kategori);
    }

    @Override
    public void tampilkanDetail() {
        System.out.println(name());
        System.out.println(iconPath);
        System.out.println("Rp" + harga);
    }
}
