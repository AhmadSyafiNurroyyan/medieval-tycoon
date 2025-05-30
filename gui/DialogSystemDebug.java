package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Debug main untuk testing DialogSystem
 * Menampilkan berbagai pesan dialog untuk testing scale dan posisi gambar
 */
public class DialogSystemDebug {
    private static DialogSystem dialogSystem;
    private static JFrame frame;
    private static JPanel gamePanel;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createDebugWindow();
        });
    }
    
    private static void createDebugWindow() {
        // Buat frame utama
        frame = new JFrame("DialogSystem Debug - Medieval Tycoon");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(1200, 800);
        
        // Buat panel game sebagai background
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Background gelap untuk simulasi game
                g.setColor(new Color(40, 60, 40));
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Grid pattern untuk referensi
                g.setColor(new Color(60, 80, 60));
                for (int i = 0; i < getWidth(); i += 50) {
                    g.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 50) {
                    g.drawLine(0, i, getWidth(), i);
                }
                
                // Info text
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("DialogSystem Debug - Press keys to test:", 20, 30);
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString("1 = Short message", 20, 55);
                g.drawString("2 = Long message", 20, 75);
                g.drawString("3 = Very long message", 20, 95);
                g.drawString("4 = Random zone message", 20, 115);
                g.drawString("E = Close dialog", 20, 135);
                g.drawString("ESC = Exit", 20, 155);
                
                // Show current dialog status
                if (dialogSystem != null && dialogSystem.isDialogVisible()) {
                    g.setColor(Color.YELLOW);
                    g.drawString("Dialog Status: VISIBLE", 20, 180);
                } else {
                    g.setColor(Color.GRAY);
                    g.drawString("Dialog Status: HIDDEN", 20, 180);
                }
            }
        };
        
        gamePanel.setLayout(null);
        gamePanel.setFocusable(true);
        
        // Inisialisasi DialogSystem
        dialogSystem = new DialogSystem(gamePanel);
        
        // Add key listeners untuk testing
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1:
                        dialogSystem.showDialog("Halo! Ini pesan pendek.");
                        break;
                    case KeyEvent.VK_2:
                        dialogSystem.showDialog("Ini adalah pesan yang cukup panjang untuk menguji word wrapping di dalam dialog box. Pesan ini seharusnya membungkus ke baris berikutnya dengan benar.");
                        break;
                    case KeyEvent.VK_3:
                        dialogSystem.showDialog("Ini adalah pesan yang sangat panjang sekali untuk menguji sistem dialog dengan banyak teks. Pesan ini akan menguji kemampuan word wrapping yang ekstensif dan memastikan bahwa dialog box dapat menampilkan banyak baris teks dengan baik. Semoga sistem dialog dapat menangani pesan yang panjang seperti ini dengan sempurna dan tetap terlihat rapi serta mudah dibaca oleh pemain.");
                        break;
                    case KeyEvent.VK_4:
                        // Simulasi pesan dari random trigger zone
                        String[] randomMessages = {
                            "Kamu menemukan koin emas!",
                            "Area kosong yang misterius...",
                            "Tempat yang tenang untuk beristirahat",
                            "Daerah yang tampak tidak biasa",
                            "Kamu merasakan energi aneh di sini",
                            "Tempat yang sempurna untuk berdagang",
                            "Area yang penuh dengan kemungkinan"
                        };
                        String randomMsg = randomMessages[(int)(Math.random() * randomMessages.length)];
                        dialogSystem.showDialog(randomMsg);
                        break;
                    case KeyEvent.VK_E:
                        if (dialogSystem.isDialogVisible()) {
                            dialogSystem.hideDialog();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                }
                gamePanel.repaint();
            }
        });
        
        // Window resize listener untuk update dialog bounds
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (dialogSystem != null && dialogSystem.isDialogVisible()) {
                    dialogSystem.setBounds(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
                }
                gamePanel.repaint();
            }
        });
        
        frame.add(gamePanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Focus pada panel untuk key events
        gamePanel.requestFocusInWindow();
        
        System.out.println("DialogSystem Debug started!");
        System.out.println("Use number keys (1-4) to test different messages");
        System.out.println("Press E to close dialog, ESC to exit");
    }
}
