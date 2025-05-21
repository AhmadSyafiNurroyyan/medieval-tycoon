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

        JButton resumeButton = StyledButton.create("Resume");
        JButton pauseSettingsButton = StyledButton.create("Settings");
        JButton backToMenuButton = StyledButton.create("Back to Menu");

        resumeButton.addActionListener(e -> {
            cardLayout.show(cardsPanel, "GAME");
            for (Component comp : cardsPanel.getComponents()) {
                if (comp instanceof GamePanel) {
                    comp.requestFocusInWindow();
                    break;
                }
            }
        });        
        pauseSettingsButton.addActionListener(e -> {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 128)); 
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
