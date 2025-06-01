/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import model.Inventory;
import model.Player;
import model.TextFileManager;

public class MainMenu extends JFrame {
    private Font titleFont;
    private Player player;
    private Inventory inventory;
    private TextFileManager fileManager;

    private int currentDay = 1;
    private int gameScore = 0;
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private GamePanel gamePanel;

    private HomeBasePanel homeBasePanel;
    private SupplierPanel supplierPanel;
    private TokoItemPanel tokoItemPanel;
    private TokoPerksPanel tokoPerksPanel;

    public MainMenu(Player player) {
        setTitle("Medieval Tycoon");
        this.player = player;
        this.inventory = player.getInventory();
        this.fileManager = new TextFileManager();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleGameExit();
            }
        });
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
        this.cardLayout = new CardLayout();
        this.cardsPanel = new JPanel(cardLayout);
        cardsPanel.setOpaque(false);
        this.gamePanel = new GamePanel(player);
        SettingsPanel settingsPanel = new SettingsPanel();
        PauseMenuPanel pauseMenuPanel = new PauseMenuPanel(cardLayout, cardsPanel);

        pauseMenuPanel.setAutoSaveCallback(() -> performAutoSave());
        this.homeBasePanel = new HomeBasePanel(player);
        this.gamePanel = new GamePanel(player);
        this.homeBasePanel.setGamePanel(this.gamePanel);
        this.supplierPanel = new SupplierPanel(gamePanel.getSupplier(), player);
        this.tokoItemPanel = new TokoItemPanel(gamePanel.getTokoItem(), player);
        this.tokoPerksPanel = new TokoPerksPanel(gamePanel.getPerksManagement(), player);
        this.tokoItemPanel.setInventory(player.getInventory());
        this.homeBasePanel.initializeWithGerobak(gamePanel.getGerobak());
        this.homeBasePanel.setSupplier(gamePanel.getSupplier());
        this.supplierPanel.setBackToGameCallback(() -> {
            this.cardLayout.show(this.cardsPanel, "GAME");
            this.gamePanel.onPanelShown();
            this.gamePanel.requestFocusInWindow();
        });

        this.tokoItemPanel.setBackToGameCallback(() -> {
            this.cardLayout.show(this.cardsPanel, "GAME");
            this.gamePanel.onPanelShown();
            this.gamePanel.requestFocusInWindow();
        });
        this.tokoPerksPanel.setBackToGameCallback(() -> {
            this.cardLayout.show(this.cardsPanel, "GAME");
            this.gamePanel.onPanelShown();
            this.gamePanel.requestFocusInWindow();
        });
        this.homeBasePanel.setInventory(player.getInventory());
        this.homeBasePanel.setBackToGameCallback(() -> {
            System.out.println("MainMenu: Switching back to game from HomeBase");
            this.homeBasePanel.onPanelHidden();
            this.cardLayout.show(this.cardsPanel, "GAME");
            this.gamePanel.onPanelShown();
            this.gamePanel.requestFocusInWindow();
        });

        this.homeBasePanel.setOnSleepCallback(() -> {
            System.out.println("MainMenu: Sleep callback triggered - refreshing random zones");
            this.gamePanel.onPanelShown();
        });

        this.supplierPanel.setAutoSaveCallback(() -> performAutoSave());
        this.tokoItemPanel.setAutoSaveCallback(() -> performAutoSave());
        this.tokoPerksPanel.setAutoSaveCallback(() -> performAutoSave());

        gamePanel.setShowTokoItemPanelCallback(() -> {
            tokoItemPanel.refresh();
            this.cardLayout.show(this.cardsPanel, "GAME");
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
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fileManager.doesSaveFileExist(username)) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "A save file with the name '" + username + "' already exists.\n" +
                                "Do you want to overwrite it with a new game?",
                        "Save File Exists",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Player newPlayer = new Player(username);

            this.player = newPlayer;
            this.inventory = newPlayer.getInventory();

            this.currentDay = 1;
            this.gameScore = 0;

            if (this.gamePanel != null) {
                this.gamePanel.updatePlayerData(newPlayer);
            }
            if (this.homeBasePanel != null) {
                this.homeBasePanel.updatePlayerData(newPlayer);
                this.homeBasePanel.setInventory(newPlayer.getInventory());
                this.homeBasePanel.setSupplier(this.gamePanel.getSupplier());
            }
            if (this.tokoItemPanel != null) {
                this.tokoItemPanel.updatePlayerData(newPlayer);
                this.tokoItemPanel.setInventory(newPlayer.getInventory());
                this.tokoItemPanel.refresh();
            }
            if (this.tokoPerksPanel != null) {
                this.tokoPerksPanel.updatePlayerData(newPlayer);
                this.tokoPerksPanel.refresh();
            }
            if (this.supplierPanel != null) {
                this.supplierPanel.updatePlayerData(newPlayer);
                this.supplierPanel.refresh();
            }

            System.out.println("MainMenu: Starting new game with fresh player: " + username +
                    " (ID: " + newPlayer.getID() + ", Money: " + newPlayer.getMoney() + ")");

            BGMPlayer.getInstance().playMapBGM();
            this.cardLayout.show(this.cardsPanel, "GAME");
            this.gamePanel.onPanelShown();
            this.gamePanel.requestFocusInWindow();
        });
        newGameButton.addActionListener(e -> {
            userField.setText("");
            this.cardLayout.show(this.cardsPanel, "NEW_GAME");
            userField.requestFocusInWindow();
        });
        loadGameButton.addActionListener(e -> {
            showCustomSaveLoadDialog();
        });
        settingsButton.addActionListener(e -> {
            settingsPanel.setPreviousScreen("MENU");
            this.cardLayout.show(this.cardsPanel, "SETTINGS");
        });
        exitButton.addActionListener(e -> {
            handleGameExit();
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
        cardsPanel.add(this.supplierPanel, "SUPPLIER");
        cardsPanel.add(this.tokoItemPanel, "TOKO ITEM");
        cardsPanel.add(this.tokoPerksPanel, "TOKO PERKS");
        cardsPanel.add(this.homeBasePanel, "HOME_BASE");
        background.add(cardsPanel, BorderLayout.CENTER);
        InputMap inputMap = cardsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = cardsPanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "showPauseMenu");
        actionMap.put("showPauseMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component comp : MainMenu.this.cardsPanel.getComponents()) {
                    if (comp.isVisible() && comp == MainMenu.this.gamePanel) {
                        MainMenu.this.cardLayout.show(MainMenu.this.cardsPanel, "PAUSE_MENU");
                        break;
                    }
                }
            }
        });
        gamePanel.setShowSupplierPanelCallback(() -> {
            this.cardLayout.show(this.cardsPanel, "SUPPLIER");
            this.supplierPanel.refresh();
            this.supplierPanel.requestFocusInWindow();
        });
        gamePanel.setShowHomeBasePanelCallback(() -> {
            System.out.println("MainMenu: Switching to HomeBase panel");
            this.cardLayout.show(this.cardsPanel, "HOME_BASE");
            this.homeBasePanel.onPanelShown();
            this.homeBasePanel.requestFocusInWindow();
        });
        gamePanel.setShowTokoItemPanelCallback(() -> {
            this.cardLayout.show(this.cardsPanel, "TOKO ITEM");
            this.tokoItemPanel.refresh();
            this.tokoItemPanel.requestFocusInWindow();
        });

        gamePanel.setShowTokoPerksPanelCallback(() -> {
            this.cardLayout.show(this.cardsPanel, "TOKO PERKS");
            this.tokoItemPanel.refresh();
            this.tokoItemPanel.requestFocusInWindow();
        });
        setVisible(true);
    }

    private void performAutoSave() {
        try {
            String currentLocation = "HomeBase";

            if (this.gamePanel != null) {
                try {
                    java.lang.reflect.Field mapField = GamePanel.class.getDeclaredField("currentMap");
                    mapField.setAccessible(true);
                    String mapName = (String) mapField.get(this.gamePanel);
                    currentLocation = mapName != null ? mapName : "HomeBase";
                } catch (Exception e) {
                    currentLocation = "HomeBase";
                }
            }

            boolean saveSuccess = fileManager.saveGameWithContext(player, currentDay, currentLocation, gameScore);

            if (saveSuccess) {
                System.out.println("Auto-save successful after transaction.");
            } else {
                System.err.println("Auto-save failed after transaction.");
            }

        } catch (Exception e) {
            System.err.println("Error during auto-save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Anda yakin ingin keluar?",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            BGMPlayer.getInstance().cleanup();
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Error during game exit: " + e.getMessage());
            e.printStackTrace();

            int errorChoice = JOptionPane.showConfirmDialog(this,
                    "An error occurred during exit. Force exit anyway?",
                    "Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (errorChoice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    private void showCustomSaveLoadDialog() {
        JDialog dialog = new JDialog(this, "Load Game", true);
        dialog.setSize(650, 580);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(new Color(30, 20, 10));
        dialog.setLayout(new BorderLayout());

        Color darkBrown = new Color(79, 44, 29);
        Color mediumBrown = new Color(120, 80, 40);
        Color parchmentDark = new Color(227, 213, 176);
        Color parchmentLight = new Color(241, 233, 210);
        Color goldAccent = new Color(212, 175, 55);
        Color inkColor = new Color(60, 30, 15);

        Font medievalFont = new Font("Serif", Font.BOLD, 18);
        Font titleFont = new Font("Serif", Font.BOLD, 24);
        Font slotFont = new Font("Serif", Font.PLAIN, 16);
        Font smallFont = new Font("Serif", Font.ITALIC, 13);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(255, 252, 235));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(200, 190, 155, 30));
                for (int i = 0; i < getHeight(); i += 3) {
                    g2d.drawLine(0, i, getWidth(), i);
                }

                GradientPaint leftShadow = new GradientPaint(
                        0, 0, new Color(120, 100, 60, 70),
                        30, 0, new Color(120, 100, 60, 0));
                g2d.setPaint(leftShadow);
                g2d.fillRect(0, 0, 30, getHeight());

                GradientPaint rightShadow = new GradientPaint(
                        getWidth() - 30, 0, new Color(120, 100, 60, 0),
                        getWidth(), 0, new Color(120, 100, 60, 70));
                g2d.setPaint(rightShadow);
                g2d.fillRect(getWidth() - 30, 0, 30, getHeight());

                GradientPaint topShadow = new GradientPaint(
                        0, 0, new Color(120, 100, 60, 70),
                        0, 30, new Color(120, 100, 60, 0));
                g2d.setPaint(topShadow);
                g2d.fillRect(0, 0, getWidth(), 30);

                GradientPaint bottomShadow = new GradientPaint(
                        0, getHeight() - 30, new Color(120, 100, 60, 0),
                        0, getHeight(), new Color(120, 100, 60, 70));
                g2d.setPaint(bottomShadow);
                g2d.fillRect(0, getHeight() - 30, getWidth(), 30);
            }
        };

        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new Border() {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2d.setColor(darkBrown);
                        g2d.setStroke(new BasicStroke(4));
                        g2d.drawRect(x + 2, y + 2, width - 4, height - 4);

                        g2d.setColor(goldAccent);
                        g2d.setStroke(new BasicStroke(1));
                        g2d.drawRect(x + 6, y + 6, width - 12, height - 12);

                        int rivetSize = 12;
                        g2d.setColor(new Color(120, 100, 80));
                        g2d.fillOval(x + 8, y + 8, rivetSize, rivetSize);
                        g2d.fillOval(x + width - rivetSize - 8, y + 8, rivetSize, rivetSize);
                        g2d.fillOval(x + 8, y + height - rivetSize - 8, rivetSize, rivetSize);
                        g2d.fillOval(x + width - rivetSize - 8, y + height - rivetSize - 8, rivetSize, rivetSize);

                        g2d.setColor(goldAccent);
                        g2d.drawOval(x + 8, y + 8, rivetSize, rivetSize);
                        g2d.drawOval(x + width - rivetSize - 8, y + 8, rivetSize, rivetSize);
                        g2d.drawOval(x + 8, y + height - rivetSize - 8, rivetSize, rivetSize);
                        g2d.drawOval(x + width - rivetSize - 8, y + height - rivetSize - 8, rivetSize, rivetSize);
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        return new Insets(18, 18, 18, 18);
                    }

                    @Override
                    public boolean isBorderOpaque() {
                        return false;
                    }
                },
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("DATA LOAD", JLabel.CENTER);
        headerLabel.setFont(titleFont);
        headerLabel.setForeground(darkBrown);
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                new Border() {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2d.setColor(goldAccent);
                        g2d.setStroke(new BasicStroke(2));
                        int lineY = y + height - 5;
                        g2d.drawLine(x + 30, lineY, x + width - 30, lineY);

                        g2d.setColor(darkBrown);
                        g2d.fillOval(x + 30 - 3, lineY - 3, 6, 6);
                        g2d.fillOval(x + width - 30 - 3, lineY - 3, 6, 6);
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        return new Insets(5, 0, 12, 0);
                    }

                    @Override
                    public boolean isBorderOpaque() {
                        return false;
                    }
                },
                BorderFactory.createEmptyBorder(5, 0, 12, 0)));

        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        String[] saveFiles = fileManager.getAllSaveFiles();
        if (saveFiles.length == 0) {
            JLabel emptyLabel = new JLabel("No saved kingdoms found in the archives.", JLabel.CENTER);
            emptyLabel.setFont(new Font("Serif", Font.ITALIC, 16));
            emptyLabel.setForeground(inkColor);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(40));
            contentPanel.add(emptyLabel);
            contentPanel.add(Box.createVerticalStrut(40));
        } else {
            contentPanel.add(Box.createVerticalStrut(5));
            for (String save : saveFiles) {
                JPanel slot = createSaveSlotPanel(save, slotFont, mediumBrown, parchmentDark, contentPanel, dialog);
                slot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                contentPanel.add(slot);
                contentPanel.add(Box.createVerticalStrut(15));
            }
        }

        int minSlots = 6;
        int toAdd = Math.max(0, minSlots - saveFiles.length);
        for (int i = 0; i < toAdd; i++) {
            JPanel emptySlot = new JPanel(new BorderLayout());
            emptySlot.setOpaque(true);
            emptySlot.setBackground(parchmentLight);
            emptySlot.setBorder(BorderFactory.createCompoundBorder(
                    new Border() {
                        @Override
                        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                            Graphics2D g2d = (Graphics2D) g;
                            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                            g2d.setColor(new Color(180, 160, 120));
                            g2d.setStroke(new BasicStroke(1));
                            g2d.drawRoundRect(x, y, width - 1, height - 1, 5, 5);

                            g2d.setColor(new Color(120, 100, 60, 30));
                            g2d.drawLine(x + 2, y + height - 1, x + width - 2, y + height - 1);
                            g2d.drawLine(x + width - 1, y + 2, x + width - 1, y + height - 2);
                        }

                        @Override
                        public Insets getBorderInsets(Component c) {
                            return new Insets(12, 15, 12, 15);
                        }

                        @Override
                        public boolean isBorderOpaque() {
                            return false;
                        }
                    },
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)));
            JLabel empty = new JLabel("<empty scroll>", JLabel.CENTER);
            empty.setFont(new Font("Serif", Font.ITALIC, 14));
            empty.setForeground(new Color(150, 130, 100));
            emptySlot.add(empty, BorderLayout.CENTER);
            emptySlot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            contentPanel.add(emptySlot);
            contentPanel.add(Box.createVerticalStrut(15));
        }
        contentPanel.add(Box.createVerticalStrut(5));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        JButton closeButton = new JButton("Close Archives");
        closeButton.setFont(slotFont);
        closeButton.setForeground(parchmentLight);
        closeButton.setBackground(darkBrown);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(goldAccent, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(evt -> dialog.dispose());

        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeButton.setBackground(new Color(100, 60, 30));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                closeButton.setBackground(darkBrown);
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private JPanel createSaveSlotPanel(String saveFile, Font medievalFont, Color darkBrown, Color hoverColor,
            JPanel parent, JDialog dialog) {
        JPanel slot = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setColor(new Color(241, 233, 210));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(200, 190, 155, 20));
                for (int i = 0; i < getHeight(); i += 2) {
                    g2d.drawLine(0, i, getWidth(), i);
                }

                GradientPaint bottomShadow = new GradientPaint(
                        0, getHeight() - 10, new Color(120, 100, 60, 0),
                        0, getHeight(), new Color(120, 100, 60, 30));
                g2d.setPaint(bottomShadow);
                g2d.fillRect(0, getHeight() - 10, getWidth(), 10);
            }
        };

        slot.setBorder(new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(160, 140, 90));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(x, y, width - 1, height - 1, 5, 5);

                g2d.setColor(new Color(120, 100, 60, 30));
                g2d.drawLine(x + 2, y + height - 1, x + width - 2, y + height - 1);
                g2d.drawLine(x + width - 1, y + 2, x + width - 1, y + height - 2);
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(12, 15, 12, 15);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
        String name = saveFile;
        String uang = "-";
        String date = "-";
        try {
            Player p = fileManager.loadGameWithContext(saveFile);
            if (p != null) {
                name = p.getUsername() != null ? p.getUsername() : saveFile;
                uang = String.valueOf(p.getMoney());
                File f = new File("saves/" + saveFile);
                if (f.exists()) {
                    long lm = f.lastModified();
                    date = new java.text.SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(new java.util.Date(lm));
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading save metadata for " + saveFile + ": " + ex.getMessage());
        }

        JPanel leftInfo = new JPanel();
        leftInfo.setOpaque(false);
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel("<html><b>User:</b> " + name + "</html>");
        nameLabel.setFont(new Font("Serif", Font.BOLD, 16));
        nameLabel.setForeground(new Color(60, 30, 15));

        JLabel idLabel = new JLabel("<html><b>Gold:</b> " + uang + "</html>");
        idLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        idLabel.setForeground(new Color(60, 30, 15));

        JLabel dateLabel = new JLabel("Saved: " + date);
        dateLabel.setFont(new Font("Serif", Font.ITALIC, 14));
        dateLabel.setForeground(new Color(100, 70, 40));

        leftInfo.add(nameLabel);
        leftInfo.add(Box.createVerticalStrut(3));
        leftInfo.add(idLabel);
        leftInfo.add(Box.createVerticalStrut(3));
        leftInfo.add(dateLabel);

        slot.add(leftInfo, BorderLayout.CENTER);

        slot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        slot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Player loadedPlayer = fileManager.loadGameWithContext(saveFile);
                    if (loadedPlayer == null) {
                        JOptionPane.showMessageDialog(dialog,
                                "Failed to load the selected kingdom.",
                                "Archive Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    MainMenu.this.player = loadedPlayer;
                    MainMenu.this.inventory = loadedPlayer.getInventory();
                    if (MainMenu.this.gamePanel != null) {
                        dialog.dispose();
                        SwingUtilities.invokeLater(() -> {
                            try {
                                MainMenu.this.gamePanel.updatePlayerData(loadedPlayer);
                                if (MainMenu.this.homeBasePanel != null) {
                                    MainMenu.this.homeBasePanel.updatePlayerData(loadedPlayer);
                                    MainMenu.this.homeBasePanel.setInventory(loadedPlayer.getInventory());
                                    MainMenu.this.homeBasePanel.setSupplier(MainMenu.this.gamePanel.getSupplier());
                                }
                                if (MainMenu.this.tokoItemPanel != null) {
                                    MainMenu.this.tokoItemPanel.updatePlayerData(loadedPlayer);
                                    MainMenu.this.tokoItemPanel.setInventory(loadedPlayer.getInventory());
                                    MainMenu.this.tokoItemPanel.refresh();
                                }
                                if (MainMenu.this.tokoPerksPanel != null) {
                                    MainMenu.this.tokoPerksPanel.updatePlayerData(loadedPlayer);
                                    MainMenu.this.tokoPerksPanel.refresh();
                                }
                                if (MainMenu.this.supplierPanel != null) {
                                    MainMenu.this.supplierPanel.updatePlayerData(loadedPlayer);
                                    MainMenu.this.supplierPanel.refresh();
                                }
                                BGMPlayer.getInstance().playMapBGM();
                                MainMenu.this.cardLayout.show(MainMenu.this.cardsPanel, "GAME");
                                MainMenu.this.gamePanel.onPanelShown();
                                MainMenu.this.gamePanel.requestFocusInWindow();
                                JOptionPane.showMessageDialog(MainMenu.this,
                                        "Welcome back, " + loadedPlayer.getUsername()
                                                + "!");
                            } catch (Exception ex) {
                                System.err.println("Error updating game with loaded player: " + ex.getMessage());
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(MainMenu.this,
                                        "Error loading kingdom data. Some elements may not display correctly.",
                                        "Archive Error",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        });
                    } else {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(MainMenu.this,
                                "Game panel not initialized. Unable to load kingdom.",
                                "Archive Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading game: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                            "Error loading saved kingdom: " + ex.getMessage(),
                            "Archive Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                slot.setBackground(hoverColor);
                slot.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                slot.setBackground(new Color(241, 233, 210));
                slot.repaint();
            }
        });

        return slot;
    }
}
