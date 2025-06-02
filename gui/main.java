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
        Player player = new Player("");

        SwingUtilities.invokeLater(() -> {
            System.out.println("=== MEDIEVAL TYCOON STARTING ===");
            System.out.println("Initializing game systems...");
            

            LoadingScreen loadingScreen = new LoadingScreen(() -> {
                System.out.println("Loading complete! Starting Medieval Tycoon...");
                
                try {
                    BGMPlayer.getInstance().playHomeBaseBGM();
                    System.out.println("Background music started.");
                    
                    MainMenu menu = new MainMenu(player);
                    menu.setVisible(true);
                    System.out.println("Main menu displayed. Game ready!");
                    
                } catch (Exception e) {
                    System.err.println("Error starting main menu: " + e.getMessage());

                    JOptionPane.showMessageDialog(null,
                        "An error occurred while starting the game:\n" + e.getMessage(),
                        "Medieval Tycoon - Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            loadingScreen.setVisible(true);
        });
    }
}
