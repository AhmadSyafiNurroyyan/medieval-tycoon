/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package gui;

import javax.swing.*;
import model.Player;

public class main {
    public static void main(String[] args) {
        Player player = new Player("Tauwus");

        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu(player);
            menu.setVisible(true);
        });
    }
}
