package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JFrame {
    
    private Font titleFont;
    private Font buttonFont;
    private final Color BUTTON_COLOR = new Color(139, 69, 19); 
    private final Color HOVER_COLOR = new Color(160, 82, 45);  
    
    public MainMenu() {
        setTitle("Medieval Tycoon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        try {
            // Try multiple approaches to load the font
            File fontFile = new File("assets/fonts/medieval.otf");
            if (!fontFile.exists()) {
                fontFile = new File("../assets/fonts/medieval.otf");
            }
            titleFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(104f);
            buttonFont = new Font("Serif", Font.BOLD, 24);
        } catch (Exception e) {
            System.err.println("Error loading font: " + e.getMessage());
            e.printStackTrace();
            titleFont = new Font("Serif", Font.BOLD, 72);
            buttonFont = new Font("Serif", Font.BOLD, 24);
        }
        
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImg = new ImageIcon(getClass().getResource("../assets/background.png")).getImage();
                    g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(50, 30, 10)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        setContentPane(backgroundPanel);
        
        CardLayout cardLayout = new CardLayout();
        JPanel cardsPanel = new JPanel(cardLayout);
        cardsPanel.setOpaque(false);
        
        GamePanel gamePanel = new GamePanel();
        SettingsPanel settingsPanel = new SettingsPanel();
        PauseMenuPanel pauseMenuPanel = new PauseMenuPanel(cardLayout, cardsPanel);
        
        // Build menu card with title
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setOpaque(false);
        
        // Title at the top with shadow effect
        JLabel titleLabel = new JLabel("Medieval Tycoon", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(218, 165, 32)); // Golden color
        titleLabel.setBorder(new EmptyBorder(100, 0, 50, 0));
        menuPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(0, 0, 100, 0)); 
        
        // Create styled buttons
        JButton newGameButton = createStyledButton("New Game");
        JButton loadGameButton = createStyledButton("Load Game");
        JButton settingsButton = createStyledButton("Settings");
        JButton exitButton = createStyledButton("Exit Game");
        
        // New Game creation panel with background and styled input box
        JPanel newGamePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImg = new ImageIcon(getClass().getResource("../assets/background.png")).getImage();
                    g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(50, 30, 10));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        newGamePanel.setOpaque(false);

        JPanel inputBoxPanel = new JPanel(new GridBagLayout());
        inputBoxPanel.setOpaque(false);
        JPanel inputContent = new JPanel();
        inputContent.setLayout(new BoxLayout(inputContent, BoxLayout.Y_AXIS));
        inputContent.setBackground(Color.WHITE);
        inputContent.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
        inputContent.setOpaque(true);
        inputContent.setMaximumSize(new Dimension(400, 250));
        inputContent.setPreferredSize(new Dimension(400, 250));

        JLabel userLabel = new JLabel("Enter your username:");
        userLabel.setFont(new Font("Serif", Font.BOLD, 28));
        userLabel.setForeground(Color.BLACK);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setBorder(new EmptyBorder(30, 0, 10, 0));
        inputContent.add(userLabel);

        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Serif", Font.PLAIN, 24));
        userField.setMaximumSize(new Dimension(300, 50));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContent.add(userField);
        inputContent.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton createGameButton = createStyledButton("Create New Game");
        createGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContent.add(createGameButton);
        inputContent.add(Box.createRigidArea(new Dimension(0, 20)));

        inputBoxPanel.add(inputContent);
        newGamePanel.add(inputBoxPanel, BorderLayout.CENTER);

        // Action for Create New Game
        createGameButton.addActionListener(e -> {
            String username = userField.getText().trim();
            if (!username.isEmpty()) {
                gamePanel.getPlayer().setUsername(username);
                cardLayout.show(cardsPanel, "GAME");
                gamePanel.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // New Game button now shows the new game panel
        newGameButton.addActionListener(e -> {
            userField.setText("");
            cardLayout.show(cardsPanel, "NEW_GAME");
            userField.requestFocusInWindow();
        });
        
        loadGameButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Load game feature will be available soon!", 
                "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        });
        
        settingsButton.addActionListener(e -> {
            cardLayout.show(cardsPanel, "SETTINGS");
        });
        
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?", "Confirm Exit", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(newGameButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(loadGameButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(settingsButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(exitButton);
        buttonsPanel.add(Box.createVerticalGlue());
        
        menuPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        JLabel versionLabel = new JLabel("v0.1 Alpha", JLabel.CENTER);
        versionLabel.setForeground(Color.LIGHT_GRAY);
        menuPanel.add(versionLabel, BorderLayout.SOUTH);
        
        cardsPanel.add(menuPanel, "MENU");
        cardsPanel.add(gamePanel, "GAME");
        cardsPanel.add(settingsPanel, "SETTINGS");
        cardsPanel.add(pauseMenuPanel, "PAUSE_MENU");
        cardsPanel.add(newGamePanel, "NEW_GAME");
          backgroundPanel.add(cardsPanel, BorderLayout.CENTER);
        
        // Only enable the ESC key for pause menu when in GAME mode
        InputMap inputMap = cardsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = cardsPanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "showPauseMenu");
        actionMap.put("showPauseMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Only show pause menu if the GAME panel is currently visible
                for (Component comp : cardsPanel.getComponents()) {
                    if (comp.isVisible() && comp == gamePanel) {
                        cardLayout.show(cardsPanel, "PAUSE_MENU");
                        break;
                    }
                }
            }
        });
        
        setVisible(true);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 60));
        button.setPreferredSize(new Dimension(300, 60));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}