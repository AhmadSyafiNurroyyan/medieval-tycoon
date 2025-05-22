package gui;

import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
        // SwingUtilities.invokeLater(() -> {
        //     JFrame frame = new JFrame("Medieval Tycoon");
        //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //     frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //     frame.setSize(800, 600); 
        //     frame.add(new HomeBasePanel());
        //     frame.setLocationRelativeTo(null);
        //     frame.setVisible(true); 
        // });
    }
}
