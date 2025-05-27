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
        // SwingUtilities.invokeLater(() -> {
        // JFrame frame = new JFrame("Medieval Tycoon");
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // frame.setSize(800, 600);
        // frame.add(new HomeBasePanel());
        // frame.setLocationRelativeTo(null);
        // frame.setVisible(true);
        // });
    }
}
