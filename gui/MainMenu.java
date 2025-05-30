package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.Inventory;
import model.Player;

public class MainMenu extends JFrame {
    private Font titleFont;
    private Player player;
    private Inventory inventory;

    public MainMenu(Player player) {
        setTitle("Medieval Tycoon");
        this.player = player;
        this.inventory = player.getInventory();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        try {
            File fontFile = new File("assets/fonts/medieval.otf");
            if (!fontFile.exists()) {
                fontFile = new File("../assets/fonts/medieval.otf");
            }
            titleFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(104f);
        } catch (Exception e) {
            System.err.println("Error loading font: " + e.getMessage());
            titleFont = new Font("Serif", Font.BOLD, 72);
        }

        JPanel background = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImg = new ImageIcon(getClass().getResource("../assets/backgrounds/MainMenu.png"))
                            .getImage();
                    g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(50, 30, 10));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        setContentPane(background);

        CardLayout cardLayout = new CardLayout();
        JPanel cardsPanel = new JPanel(cardLayout);
        cardsPanel.setOpaque(false);

        GamePanel gamePanel = new GamePanel(player);
        SettingsPanel settingsPanel = new SettingsPanel();
        PauseMenuPanel pauseMenuPanel = new PauseMenuPanel(cardLayout, cardsPanel);
        HomeBasePanel homeBasePanel = new HomeBasePanel();
        SupplierPanel supplierPanel = new SupplierPanel(gamePanel.getSupplier(), gamePanel.getPlayer());
        TokoItemPanel tokoItemPanel = new TokoItemPanel(gamePanel.getTokoItem(), gamePanel.getPlayer());
        TokoPerksPanel tokoPerksPanel = new TokoPerksPanel(gamePanel.getTokoPerks(), gamePanel.getPlayer());

        tokoItemPanel.setInventory(player.getInventory());
        homeBasePanel.initializeWithGerobak(gamePanel.getGerobak());

        supplierPanel.setBackToGameCallback(() -> {
            cardLayout.show(cardsPanel, "GAME");
            gamePanel.requestFocusInWindow();
        });

        tokoItemPanel.setBackToGameCallback(() -> {
            cardLayout.show(cardsPanel, "GAME");
            gamePanel.requestFocusInWindow();
        });

        tokoPerksPanel.setBackToGameCallback(() -> {
            cardLayout.show(cardsPanel, "GAME");
            gamePanel.requestFocusInWindow();
        });

        homeBasePanel.setInventory(player.getInventory());
        homeBasePanel.setBackToGameCallback(() -> {
            cardLayout.show(cardsPanel, "GAME");
            gamePanel.requestFocusInWindow();
        });

        gamePanel.setShowTokoItemPanelCallback(() -> {
            tokoItemPanel.refresh();
            cardLayout.show(cardsPanel, "GAME");
            tokoItemPanel.requestFocusInWindow();
        });

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setOpaque(false);

        JLabel titleLabel = StyledButton.createLabel("Medieval Tycoon", 104, new Color(218, 165, 32), Font.BOLD,
                JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setBorder(new EmptyBorder(100, 0, 50, 0));
        menuPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(0, 0, 100, 0));

        JButton newGameButton = StyledButton.create("New Game");
        JButton loadGameButton = StyledButton.create("Load Game");
        JButton settingsButton = StyledButton.create("Settings");
        JButton exitButton = StyledButton.create("Exit Game");
        JButton createGameButton = StyledButton.create("Create New Game");

        JPanel newGamePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImg = new ImageIcon(getClass().getResource("../assets/backgrounds/MainMenu.png"))
                            .getImage();
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

        JLabel userLabel = StyledButton.createLabel("Enter your username:", 28, Color.BLACK, Font.BOLD, JLabel.LEFT);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setBorder(new EmptyBorder(30, 0, 10, 0));
        inputContent.add(userLabel);

        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Serif", Font.PLAIN, 24));
        userField.setMaximumSize(new Dimension(300, 50));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContent.add(userField);
        inputContent.add(Box.createRigidArea(new Dimension(0, 30)));

        createGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContent.add(createGameButton);
        inputContent.add(Box.createRigidArea(new Dimension(0, 20)));

        inputBoxPanel.add(inputContent);
        newGamePanel.add(inputBoxPanel, BorderLayout.CENTER);

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

        newGameButton.addActionListener(e -> {
            userField.setText("");
            cardLayout.show(cardsPanel, "NEW_GAME");
            userField.requestFocusInWindow();
        });

        loadGameButton.addActionListener(e -> {
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

        JLabel versionLabel = StyledButton.createLabel("Alpha v1", 18, Color.LIGHT_GRAY, Font.PLAIN, JLabel.CENTER);
        menuPanel.add(versionLabel, BorderLayout.SOUTH);

        cardsPanel.add(menuPanel, "MENU");
        cardsPanel.add(gamePanel, "GAME");
        cardsPanel.add(settingsPanel, "SETTINGS");
        cardsPanel.add(pauseMenuPanel, "PAUSE_MENU");
        cardsPanel.add(newGamePanel, "NEW_GAME");
        cardsPanel.add(supplierPanel, "SUPPLIER");
        cardsPanel.add(tokoItemPanel, "TOKO ITEM");
        cardsPanel.add(tokoPerksPanel, "TOKO PERKS");
        cardsPanel.add(homeBasePanel, "HOME_BASE");
        background.add(cardsPanel, BorderLayout.CENTER);

        InputMap inputMap = cardsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = cardsPanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "showPauseMenu");
        actionMap.put("showPauseMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component comp : cardsPanel.getComponents()) {
                    if (comp.isVisible() && comp == gamePanel) {
                        cardLayout.show(cardsPanel, "PAUSE_MENU");
                        break;
                    }
                }
            }
        });

        gamePanel.setShowSupplierPanelCallback(() -> {
            cardLayout.show(cardsPanel, "SUPPLIER");
            supplierPanel.refresh();
            supplierPanel.requestFocusInWindow();
        });
        gamePanel.setShowHomeBasePanelCallback(() -> {
            cardLayout.show(cardsPanel, "HOME_BASE");
            homeBasePanel.requestFocusInWindow();
        });
        gamePanel.setShowTokoItemPanelCallback(() -> {
            cardLayout.show(cardsPanel, "TOKO ITEM");
            tokoItemPanel.refresh();
            tokoItemPanel.requestFocusInWindow();
        });

        gamePanel.setShowTokoPerksPanelCallback(() -> {
            cardLayout.show(cardsPanel, "TOKO PERKS");
            tokoItemPanel.refresh();
            tokoItemPanel.requestFocusInWindow();
        });

        setVisible(true);
    }
}
