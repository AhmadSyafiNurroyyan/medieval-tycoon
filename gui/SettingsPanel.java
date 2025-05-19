package gui;

import java.awt.*;
import javax.swing.*;

public class SettingsPanel extends JPanel {    private String previousScreen = "MENU"; 
    
    public SettingsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel titleLabel = StyledButton.createLabel("Settings", 36, new Color(218, 165, 32), Font.BOLD, JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);
        JCheckBox soundCheckbox = StyledButton.createCheckBox("Enable Sound");
        JLabel resolutionLabel = StyledButton.createLabel("Resolution:");
        JLabel fullscreenLabel = StyledButton.createLabel("Fullscreen:");
        JButton backButton = StyledButton.create("Back to Menu");


        
        resolutionLabel.setFont(new Font("Serif", Font.BOLD, 24));
        resolutionLabel.setForeground(Color.WHITE);
        String[] resolutions = {"800 x 600", "1024 x 768", "1280 x 720", "1920 x 1080"};
        JComboBox<String> resolutionCombo = new JComboBox<>(resolutions);
        resolutionCombo.setFont(new Font("Serif", Font.PLAIN, 22));
        resolutionCombo.setMaximumSize(new Dimension(300, 50));
        resolutionCombo.setPreferredSize(new Dimension(300, 50));
        resolutionCombo.setSelectedIndex(0);

        
        fullscreenLabel.setFont(new Font("Serif", Font.BOLD, 24));
        fullscreenLabel.setForeground(Color.WHITE);
        JButton fullscreenToggle = StyledButton.create("OFF");
        fullscreenToggle.putClientProperty("isOn", Boolean.FALSE);
        fullscreenToggle.addActionListener(e -> {
            boolean isOn = Boolean.TRUE.equals(fullscreenToggle.getClientProperty("isOn"));
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                if (!isOn) { 
                    fullscreenToggle.setText("ON");
                    fullscreenToggle.putClientProperty("isOn", Boolean.TRUE);
                    frame.dispose();
                    frame.setUndecorated(true);
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    frame.setVisible(true);
                } else { 
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

        gbc.gridy = 0;
        innerContentPanel.add(resolutionLabel, gbc);
        gbc.gridy = 1;
        innerContentPanel.add(resolutionCombo, gbc);
        gbc.gridy = 2;
        innerContentPanel.add(fullscreenLabel, gbc);
        gbc.gridy = 3;
        innerContentPanel.add(fullscreenToggle, gbc);
        gbc.gridy = 4;
        innerContentPanel.add(soundCheckbox, gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 0, 0);
        innerContentPanel.add(backButton, gbc);

        outerBoxPanel.add(innerContentPanel);
        add(outerBoxPanel, BorderLayout.CENTER);

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
}
