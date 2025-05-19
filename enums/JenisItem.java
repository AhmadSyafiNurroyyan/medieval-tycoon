
package enums;

import interfaces.Showable;

public enum JenisItem implements Showable {
    HIPNOTIS("Hipnotis", "Meningkatkan peluang pembeli tidak menawar", 100_000),
    RAYUAN("Rayuan", "Meningkatkan peluang pembeli membeli dengan harga tinggi", 150_000),
    BONUS_KESABARAN("Bonus Kesabaran", "Memperlama waktu tunggu pembeli", 120_000),
    SEGARKAN_DAGANGAN("Segarkan Dagangan", "Meningkatkan kesegaran barang dagangan", 200_000),
    MEMPERBESAR_PELUANG_BELI("Memperbesar Peluang Beli", "Meningkatkan peluang pembeli untuk JADI beli", 180_000);

    private final String nama;
    private final String deskripsi;
    private final int harga;

    JenisItem(String nama, String deskripsi, int harga) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public int getHarga() {
        return harga;
    }

    @Override
    public void tampilkanDetail() {
        System.out.printf("%s - %s (Harga: %d)\n", nama, deskripsi, harga);
    }
}
