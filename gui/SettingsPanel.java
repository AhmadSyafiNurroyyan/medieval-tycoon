package gui;

import java.awt.*;
import javax.swing.*;

public class SettingsPanel extends JPanel {    private String previousScreen = "MENU"; 
    
    public SettingsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel titleLabel = new JLabel("Settings", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(218, 165, 32));
        add(titleLabel, BorderLayout.NORTH);

        JCheckBox soundCheckbox = new JCheckBox("Enable Sound");
        soundCheckbox.setFont(new Font("Serif", Font.PLAIN, 24));
        soundCheckbox.setOpaque(true);
        soundCheckbox.setBackground(new Color(139, 69, 19));
        soundCheckbox.setForeground(Color.WHITE);
        soundCheckbox.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Resolution choices
        JLabel resolutionLabel = new JLabel("Resolution:");
        resolutionLabel.setFont(new Font("Serif", Font.BOLD, 24));
        resolutionLabel.setForeground(Color.WHITE);
        String[] resolutions = {"800 x 600", "1024 x 768", "1280 x 720", "1600 x 900", "1920 x 1080"};
        JComboBox<String> resolutionCombo = new JComboBox<>(resolutions);
        resolutionCombo.setFont(new Font("Serif", Font.PLAIN, 22));
        resolutionCombo.setMaximumSize(new Dimension(300, 50));
        resolutionCombo.setPreferredSize(new Dimension(300, 50));
        resolutionCombo.setSelectedIndex(0);

        // Fullscreen toggle button
        JLabel fullscreenLabel = new JLabel("Fullscreen:");
        fullscreenLabel.setFont(new Font("Serif", Font.BOLD, 24));
        fullscreenLabel.setForeground(Color.WHITE);
        JButton fullscreenToggle = createStyledButton("OFF");
        fullscreenToggle.setBackground(new Color(139, 69, 19));
        fullscreenToggle.setForeground(Color.WHITE);
        fullscreenToggle.setFocusPainted(false);
        fullscreenToggle.setBorder(BorderFactory.createRaisedBevelBorder());
        fullscreenToggle.setMaximumSize(new Dimension(300, 60));
        fullscreenToggle.setPreferredSize(new Dimension(300, 60));
        fullscreenToggle.putClientProperty("isOn", Boolean.FALSE);
        fullscreenToggle.addActionListener(e -> {
            boolean isOn = Boolean.TRUE.equals(fullscreenToggle.getClientProperty("isOn"));
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                if (!isOn) { // Turn ON fullscreen
                    fullscreenToggle.setText("ON");
                    fullscreenToggle.putClientProperty("isOn", Boolean.TRUE);
                    frame.dispose();
                    frame.setUndecorated(true);
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    frame.setVisible(true);
                } else { // Turn OFF fullscreen
                    fullscreenToggle.setText("OFF");
                    fullscreenToggle.putClientProperty("isOn", Boolean.FALSE);
                    frame.dispose();
                    frame.setUndecorated(false);
                    String selected = (String) resolutionCombo.getSelectedItem();
                    if (selected != null) {
                        String[] parts = selected.split(" x ");
                        if (parts.length == 2) {
                            try {
                                int w = Integer.parseInt(parts[0].trim());
                                int h = Integer.parseInt(parts[1].trim());
                                frame.setSize(w, h);
                                frame.setLocationRelativeTo(null);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    frame.setExtendedState(JFrame.NORMAL);
                    frame.setVisible(true);
                }
            }
        });

        // Use the same style as MainMenu for the back button
        JButton backButton = createStyledButton("Back to Menu");
        backButton.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent instanceof JPanel) {
                CardLayout layout = (CardLayout) parent.getLayout();
                layout.show(parent, previousScreen);
            }
        });
        backButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel outerBoxPanel = new JPanel(new GridBagLayout());
        outerBoxPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        outerBoxPanel.setOpaque(true);
        outerBoxPanel.setBackground(new Color(139, 69, 19, 100)); // Brown color

        JPanel innerContentPanel = new JPanel(new GridBagLayout());
        innerContentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Add resolution
        gbc.gridy = 0;
        innerContentPanel.add(resolutionLabel, gbc);
        gbc.gridy = 1;
        innerContentPanel.add(resolutionCombo, gbc);
        // Add fullscreen toggle
        gbc.gridy = 2;
        innerContentPanel.add(fullscreenLabel, gbc);
        gbc.gridy = 3;
        innerContentPanel.add(fullscreenToggle, gbc);
        // Add sound checkbox (keep only one)
        gbc.gridy = 4;
        innerContentPanel.add(soundCheckbox, gbc);
        // Add back button
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 0, 0);
        innerContentPanel.add(backButton, gbc);

        outerBoxPanel.add(innerContentPanel);
        add(outerBoxPanel, BorderLayout.CENTER);

        // Resolution change logic
        resolutionCombo.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame && !((JFrame) window).isUndecorated()) {
                String selected = (String) resolutionCombo.getSelectedItem();
                if (selected != null && !Boolean.TRUE.equals(fullscreenToggle.getClientProperty("isOn"))) {
                    String[] parts = selected.split(" x ");
                    if (parts.length == 2) {
                        try {
                            int w = Integer.parseInt(parts[0].trim());
                            int h = Integer.parseInt(parts[1].trim());
                            window.setSize(w, h);
                            window.setLocationRelativeTo(null);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        });
    }

    public void setPreviousScreen(String screen) {
        this.previousScreen = screen;
        
        Container container = this.getParent();
        if (container != null) {
            Component[] components = container.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;
                    for (Component c : panel.getComponents()) {
                        if (c instanceof JButton && ((JButton) c).getText().startsWith("Back to")) {
                            if ("PAUSE_MENU".equals(screen)) {
                                ((JButton) c).setText("Back to Pause Menu");
                            } else {
                                ((JButton) c).setText("Back to Menu");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    // Styled button method copied from MainMenu
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
}
