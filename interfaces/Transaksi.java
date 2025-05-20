package interfaces;
 
import enums.JenisItem;
import model.Player;

public interface Transaksi {

    public boolean transaksi(Player player, JenisItem item, int jumlah);
}
