/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package interfaces;

import model.Player;

public interface Transaksi<T> {
    boolean beli(Player player, T item);
    default boolean jual(Player player, T item) {
        throw new UnsupportedOperationException("Jual tidak didukung.");
    }
}
