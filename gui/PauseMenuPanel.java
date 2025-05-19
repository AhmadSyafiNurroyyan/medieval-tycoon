package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PauseMenuPanel extends JPanel {

    public PauseMenuPanel(CardLayout cardLayout, JPanel cardsPanel) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel pauseButtonsPanel = new JPanel();
        pauseButtonsPanel.setLayout(new BoxLayout(pauseButtonsPanel, BoxLayout.Y_AXIS));
        pauseButtonsPanel.setOpaque(false);
        pauseButtonsPanel.setBorder(new EmptyBorder(0, 0, 100, 0));

        JButton resumeButton = createStyledButton("Resume");
        JButton pauseSettingsButton = createStyledButton("Settings");
        JButton backToMenuButton = createStyledButton("Back to Menu");

        resumeButton.addActionListener(e -> {
            cardLayout.show(cardsPanel, "GAME");
            // Request focus for the game panel so key events work
            for (Component comp : cardsPanel.getComponents()) {
                if (comp instanceof GamePanel) {
                    comp.requestFocusInWindow();
                    break;
                }
            }
        });        pauseSettingsButton.addActionListener(e -> {
            // Get the settings panel and set its previous screen to PAUSE_MENU
            Component[] components = cardsPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof SettingsPanel) {
                    ((SettingsPanel) comp).setPreviousScreen("PAUSE_MENU");
                    break;
                }
            }
            cardLayout.show(cardsPanel, "SETTINGS");
        });

        backToMenuButton.addActionListener(e -> {
            cardLayout.show(cardsPanel, "MENU");
        });

        pauseButtonsPanel.add(Box.createVerticalGlue());
        pauseButtonsPanel.add(resumeButton);
        pauseButtonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        pauseButtonsPanel.add(pauseSettingsButton);
        pauseButtonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        pauseButtonsPanel.add(backToMenuButton);
        pauseButtonsPanel.add(Box.createVerticalGlue());

        add(pauseButtonsPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 24));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 60));
        button.setPreferredSize(new Dimension(300, 60));
        button.setBackground(new Color(139, 69, 19));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(160, 82, 45));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(139, 69, 19));
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 128)); // 50% transparent black
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
