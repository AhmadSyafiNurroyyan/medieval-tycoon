package model;

public class Semproten extends Item {

    public Semproten() {
        super("Semproten", "Menambah kesan barang lebih fresh, harga bisa ditawar lebih mahal",
                3000, 800, "icons/semproten.png");
    }

    public void aktifkanSaatTransaksi() {
        activate();
    }
}