package interfaces;

import model.Player;

public interface Transaksi<T> {

    boolean beli(Player player, T item);

    default boolean jual(Player player, T item) {
        // Default-nya tidak semua transaksi bisa menjual
        throw new UnsupportedOperationException("Jual tidak didukung.");
    }
}
