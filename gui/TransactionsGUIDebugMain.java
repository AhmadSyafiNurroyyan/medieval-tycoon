package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import model.Barang;
import model.Inventory;
import model.Player;
import model.Pembeli;
import model.PembeliStandar;
import model.PembeliTajir;
import model.PembeliMiskin;

/**
 * Standalone main class for debugging TransactionsGUI
 * Provides a testing environment for the transaction interface
 */
public class TransactionsGUIDebugMain extends JFrame implements KeyListener {
    private TransactionsGUI transactionsGUI;
    private Player testPlayer;
    private Pembeli currentPembeli;
    private JPanel gamePanel;
    private JPanel debugControlPanel;
    private JTextArea logArea;
    private JComboBox<String> pembeliTypeCombo;
    private JButton showDialogButton;
    private JButton hideDialogButton;
    private JButton startTradingButton;
    private JButton addItemsButton;
    private JButton clearInventoryButton;
    private JButton addPricedItemsButton;
    private JButton setHargaJualAllButton;

    public TransactionsGUIDebugMain() {
        setupMainFrame();
        initializeTestData();
        createUI();
        setupEventHandlers();
        
        log("=== TransactionsGUI Debug Environment Started ===");
        log("Use the control panel on the right to test different scenarios");
        log("Or use keyboard shortcuts:");
        log("SPACE - Show/hide dialog");
        log("S - Start trading");
        log("E - Close dialog");
        log("ESC - Exit");
    }

    private void setupMainFrame() {
        setTitle("TransactionsGUI Debug Environment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Make frame focusable for key events
        setFocusable(true);
        addKeyListener(this);
    }

    private void initializeTestData() {
        // Create test player with some initial money and items
        testPlayer = new Player("DebugPlayer");
        // Set test money to 50000
        testPlayer.kurangiMoney(testPlayer.getMoney());
        testPlayer.tambahMoney(50000);
        // Reset inventory
        testPlayer.setInventory(new Inventory());
        
        // Add some test items to inventory
        addTestItemsToPlayer();
        
        // Create initial test buyer
        currentPembeli = new PembeliStandar();
        
        log("Test player created: " + testPlayer.getUsername());
        log("Initial money: " + testPlayer.getMoney());
        log("Initial inventory size: " + testPlayer.getInventory().getJumlahBarang());
    }

    private void addTestItemsToPlayer() {
        Inventory inv = testPlayer.getInventory();
        
        // Add various fruits and vegetables for testing
        inv.tambahBarang(new Barang("Apel", "", 100, ""));
        inv.tambahBarang(new Barang("Jeruk", "", 80, ""));
        inv.tambahBarang(new Barang("Pisang", "", 60, ""));
        inv.tambahBarang(new Barang("Tomat", "", 70, ""));
        inv.tambahBarang(new Barang("Wortel", "", 50, ""));
        // Setelah menambah barang, langsung bawa semua ke gerobak dan set harga jual
        moveAllToGerobakAndSetHarga(200); // harga jual default 200
    }

    // Helper: pindahkan semua barang ke gerobak dan set harga jual
    private void moveAllToGerobakAndSetHarga(int hargaJual) {
        Inventory inv = testPlayer.getInventory();
        int kapasitas = inv.getGerobak().getKapasitasBarang();
        // Pindahkan semua stok ke gerobak
        for (Barang b : new java.util.ArrayList<>(inv.getStokBarang())) {
            int jumlah = 1; // asumsikan 1 per entry (karena getStokBarang duplikat per quantity)
            inv.bawaBarang(b, jumlah, kapasitas);
        }
        // Set harga jual untuk semua barang di gerobak
        for (Barang b : inv.getBarangDibawaMutable().keySet()) {
            inv.setHargaJual(b, hargaJual);
        }
    }

    private void createUI() {
        // Create main game panel (simulates the actual game environment)
        gamePanel = new JPanel();
        gamePanel.setLayout(null);
        gamePanel.setBackground(new Color(34, 139, 34)); // Forest green background
        gamePanel.setPreferredSize(new Dimension(1000, 900));
        
        // Create and add TransactionsGUI to game panel
        transactionsGUI = new TransactionsGUI(gamePanel);
        transactionsGUI.setPlayer(testPlayer);
        transactionsGUI.setPembeli(currentPembeli);
        
        // Create debug control panel
        createDebugControlPanel();
        
        // Add panels to frame
        add(gamePanel, BorderLayout.CENTER);
        add(debugControlPanel, BorderLayout.EAST);
    }

    private void createDebugControlPanel() {
        debugControlPanel = new JPanel();
        debugControlPanel.setLayout(new BorderLayout());
        debugControlPanel.setPreferredSize(new Dimension(400, 900));
        debugControlPanel.setBorder(BorderFactory.createTitledBorder("Debug Controls"));
        
        // Create control buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(0, 1, 5, 5));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Pembeli type selector
        JPanel pembeliPanel = new JPanel(new FlowLayout());
        pembeliPanel.add(new JLabel("Buyer Type:"));
        pembeliTypeCombo = new JComboBox<>(new String[]{"Standard", "Rich", "Poor"});
        pembeliPanel.add(pembeliTypeCombo);
        buttonsPanel.add(pembeliPanel);
        
        // Control buttons
        showDialogButton = new JButton("Show Dialog");
        hideDialogButton = new JButton("Hide Dialog");
        startTradingButton = new JButton("Start Trading");
        addItemsButton = new JButton("Add Test Items");
        clearInventoryButton = new JButton("Clear Inventory");
        addPricedItemsButton = new JButton("Add Priced Items");
        setHargaJualAllButton = new JButton("Set Harga Jual All");
        
        JButton changePembeliButton = new JButton("Change Buyer Type");
        JButton addMoneyButton = new JButton("Add 10000 Money");
        JButton resetPlayerButton = new JButton("Reset Player");
        
        buttonsPanel.add(showDialogButton);
        buttonsPanel.add(hideDialogButton);
        buttonsPanel.add(startTradingButton);
        buttonsPanel.add(new JSeparator());
        buttonsPanel.add(addItemsButton);
        buttonsPanel.add(clearInventoryButton);
        buttonsPanel.add(new JSeparator());
        buttonsPanel.add(changePembeliButton);
        buttonsPanel.add(addMoneyButton);
        buttonsPanel.add(resetPlayerButton);
        buttonsPanel.add(addPricedItemsButton);
        buttonsPanel.add(setHargaJualAllButton);
        
        // Create log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Debug Log"));
        logScrollPane.setPreferredSize(new Dimension(380, 400));
        
        // Setup button actions
        setupButtonActions(changePembeliButton, addMoneyButton, resetPlayerButton, addPricedItemsButton, setHargaJualAllButton);
        
        debugControlPanel.add(buttonsPanel, BorderLayout.NORTH);
        debugControlPanel.add(logScrollPane, BorderLayout.CENTER);
    }

    private void setupButtonActions(JButton changePembeliButton, JButton addMoneyButton, JButton resetPlayerButton, JButton addPricedItemsButton, JButton setHargaJualAllButton) {
        showDialogButton.addActionListener(e -> {
            transactionsGUI.showDialog("Debug: This is a test dialog message from the debug environment.");
            log("Dialog shown");
        });
        
        hideDialogButton.addActionListener(e -> {
            transactionsGUI.hideDialog();
            log("Dialog hidden");
        });
        
        startTradingButton.addActionListener(e -> {
            if (testPlayer.getInventory().getStokBarang().isEmpty()) {
                transactionsGUI.showDialog("Tidak ada barang untuk dijual!");
                log("No items in inventory for trading");
            } else {
                transactionsGUI.startTrading();
                log("Trading started with " + currentPembeli.getKategori() + " buyer");
            }
        });
        
        addItemsButton.addActionListener(e -> {
            addTestItemsToPlayer();
            log("Test items added to player inventory, dibawa ke gerobak, dan harga jual diset");
        });
        clearInventoryButton.addActionListener(e -> {
            // Reset test player data
            initializeTestData();
            transactionsGUI.setPlayer(testPlayer);
            transactionsGUI.setPembeli(currentPembeli);
            log("Player data reset (clear)");
        });
        
        changePembeliButton.addActionListener(e -> {
            String selectedType = (String) pembeliTypeCombo.getSelectedItem();
            if ("Standard".equalsIgnoreCase(selectedType)) {
                currentPembeli = new PembeliStandar();
            } else if ("Rich".equalsIgnoreCase(selectedType)) {
                currentPembeli = new PembeliTajir();
            } else if ("Poor".equalsIgnoreCase(selectedType)) {
                currentPembeli = new PembeliMiskin();
            }
            transactionsGUI.setPembeli(currentPembeli);
            log("Buyer changed to: " + currentPembeli.getKategori());
        });
        
        addMoneyButton.addActionListener(e -> {
            testPlayer.tambahMoney(10000);
            log("Added 10000 money. Current: " + testPlayer.getMoney());
        });
        
        resetPlayerButton.addActionListener(e -> {
            initializeTestData();
            transactionsGUI.setPlayer(testPlayer);
            transactionsGUI.setPembeli(currentPembeli);
            log("Player data reset");
        });
        
        addPricedItemsButton.addActionListener(e -> {
            Inventory inv = testPlayer.getInventory();
            boolean found = false;
            for (Barang b : inv.getStokBarang()) {
                // Assume harga jual is set if hargaBeli > 0 (or adapt if you have a hargaJual field)
                if (b.getHargaBeli() > 0) {
                    log("Test item with price: " + b.getNamaBarang() + " (Harga: " + b.getHargaBeli() + ")");
                    found = true;
                }
            }
            if (!found) {
                log("No items with set price found in inventory.");
            }
        });
        
        setHargaJualAllButton.addActionListener(e -> {
            moveAllToGerobakAndSetHarga(200);
            log("Set harga jual 200 untuk semua barang di gerobak");
        });
    }

    private void setupEventHandlers() {
        // Focus management to ensure key events work
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                requestFocusInWindow();
            }
        });
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + java.time.LocalTime.now().toString().substring(0, 8) + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (transactionsGUI.isDialogVisible()) {
                    transactionsGUI.hideDialog();
                    log("Dialog hidden (SPACE key)");
                } else {
                    transactionsGUI.showDialog("Debug: Dialog toggled with SPACE key");
                    log("Dialog shown (SPACE key)");
                }
                break;
                
            case KeyEvent.VK_S:
                if (!transactionsGUI.isDialogVisible()) {
                    if (testPlayer.getInventory().getStokBarang().isEmpty()) {
                        transactionsGUI.showDialog("Tidak ada barang untuk dijual!");
                        log("No items for trading (S key)");
                    } else {
                        transactionsGUI.startTrading();
                        log("Trading started (S key)");
                    }
                }
                break;
                
            case KeyEvent.VK_E:
                if (transactionsGUI.isDialogVisible()) {
                    transactionsGUI.hideDialog();
                    log("Dialog closed (E key)");
                }
                break;
                
            case KeyEvent.VK_ESCAPE:
                log("Exiting debug environment...");
                System.exit(0);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            new TransactionsGUIDebugMain().setVisible(true);
        });
    }
}
