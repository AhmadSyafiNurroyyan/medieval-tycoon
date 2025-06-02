/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package debugger;

import gui.TransactionsGUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.*;
import model.Barang;
import model.Inventory;
import model.Pembeli;
import model.PembeliMiskin;
import model.PembeliStandar;
import model.PembeliTajir;
import model.Player;

public class TransactionsGUIDebugMain extends JFrame implements KeyListener {
    private TransactionsGUI transactionsGUI;
    private Player testPlayer;
    private Pembeli currentPembeli;    private JPanel gamePanel;
    private JPanel debugControlPanel;
    private JTextArea logArea;
    private JComboBox<String> pembeliTypeCombo;
    private JComboBox<String> pembeliImageCombo;
    private JButton refreshImagesButton;
    private JButton showDialogButton;
    private JButton hideDialogButton;
    private JButton startTradingButton;
    private JButton addItemsButton;
    private JButton clearInventoryButton;
    private JButton addPricedItemsButton;
    private JButton setHargaJualAllButton;    public TransactionsGUIDebugMain() {
        setupMainFrame();
        initializeTestData();
        createUI();
        styleAllButtonsInTransactionsGUI();
        setupEventHandlers();
        

        loadPembeliImages();
        
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
        
        setFocusable(true);
        addKeyListener(this);
    }

    private void initializeTestData() {
        testPlayer = new Player("DebugPlayer");
        testPlayer.kurangiMoney(testPlayer.getMoney());
        testPlayer.tambahMoney(50000);
        testPlayer.setInventory(new Inventory());
        
        addTestItemsToPlayer();
        
        currentPembeli = new PembeliStandar();
        
        log("Test player created: " + testPlayer.getUsername());
        log("Initial money: " + testPlayer.getMoney());
        log("Initial inventory size: " + testPlayer.getInventory().getJumlahBarang());
    }

    private void addTestItemsToPlayer() {
        Inventory inv = testPlayer.getInventory();
        
        inv.tambahBarang(new Barang("Apel", "", 100, ""));
        inv.tambahBarang(new Barang("Jeruk", "", 80, ""));
        inv.tambahBarang(new Barang("Pisang", "", 60, ""));
        inv.tambahBarang(new Barang("Tomat", "", 70, ""));
        inv.tambahBarang(new Barang("Wortel", "", 50, ""));
        moveAllToGerobakAndSetHarga(200);
    }

    private void moveAllToGerobakAndSetHarga(int hargaJual) {
        Inventory inv = testPlayer.getInventory();
        int kapasitas = inv.getGerobak().getKapasitasBarang();
        for (Barang b : new java.util.ArrayList<>(inv.getStokBarang())) {
            int jumlah = 1;
            inv.bawaBarang(b, jumlah, kapasitas);
        }
        for (Barang b : inv.getBarangDibawaMutable().keySet()) {
            inv.setHargaJual(b, hargaJual);
        }
    }

    private void createUI() {
        gamePanel = new JPanel();
        gamePanel.setLayout(null);
        gamePanel.setBackground(new Color(34, 139, 34));
        gamePanel.setPreferredSize(new Dimension(1000, 900));
        
        transactionsGUI = new TransactionsGUI(gamePanel);
        transactionsGUI.setPlayer(testPlayer);
        transactionsGUI.setPembeli(currentPembeli);
        
        createDebugControlPanel();
        
        add(gamePanel, BorderLayout.CENTER);
        add(debugControlPanel, BorderLayout.EAST);
    }

    private void createDebugControlPanel() {
        debugControlPanel = new JPanel();
        debugControlPanel.setLayout(new BorderLayout());
        debugControlPanel.setPreferredSize(new Dimension(400, 900));
        debugControlPanel.setBorder(BorderFactory.createTitledBorder("Debug Controls"));
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(0, 1, 5, 5));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel pembeliPanel = new JPanel(new FlowLayout());
        pembeliPanel.add(new JLabel("Buyer Type:"));
        pembeliTypeCombo = new JComboBox<>(new String[]{"Standard", "Rich", "Poor"});
        pembeliPanel.add(pembeliTypeCombo);
        buttonsPanel.add(pembeliPanel);

        JPanel pembeliImagePanel = new JPanel(new FlowLayout());
        pembeliImagePanel.add(new JLabel("Buyer Image:"));
        pembeliImageCombo = new JComboBox<>();
        pembeliImageCombo.setPreferredSize(new Dimension(150, 25));
        pembeliImagePanel.add(pembeliImageCombo);
        refreshImagesButton = new JButton("Refresh");
        refreshImagesButton.setPreferredSize(new Dimension(70, 25));
        pembeliImagePanel.add(refreshImagesButton);
        buttonsPanel.add(pembeliImagePanel);
        
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
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Debug Log"));
        logScrollPane.setPreferredSize(new Dimension(380, 400));
        
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

            
            loadPembeliImages();
            setSelectedPembeliImage();
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
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                requestFocusInWindow();
            }
        });

        
        refreshImagesButton.addActionListener(e -> {
            loadPembeliImages();
        });
 
        pembeliTypeCombo.addActionListener(e -> {
            String selectedType = (String) pembeliTypeCombo.getSelectedItem();
            log("Buyer type changed to: " + selectedType);

            
            loadPembeliImages();

            
            setSelectedPembeliImage();
        });
 
        pembeliImageCombo.addActionListener(e -> {
            setSelectedPembeliImage();
        });
    }    
    
    private void loadPembeliImages() {
        String selectedType = (String) pembeliTypeCombo.getSelectedItem();
        String folderPath = null;
        
        if ("Standard".equalsIgnoreCase(selectedType)) {
            folderPath = "/assets/backgrounds/PembeliStandar/";
        } else if ("Rich".equalsIgnoreCase(selectedType)) {
            folderPath = "/assets/backgrounds/PembeliTajir/";
        } else if ("Poor".equalsIgnoreCase(selectedType)) {
            folderPath = "/assets/backgrounds/PembeliMiskin/";
        }
        
        pembeliImageCombo.removeAllItems();
        pembeliImageCombo.addItem("Random (Default)");
        
        if (folderPath != null) {
            try {
                
                java.net.URL resourceUrl = getClass().getResource(folderPath);
                if (resourceUrl != null) {
                    File folder = new File(resourceUrl.toURI());
                    if (folder.exists() && folder.isDirectory()) {
                        File[] files = folder.listFiles((dir, name) -> 
                            name.toLowerCase().endsWith(".png") || 
                            name.toLowerCase().endsWith(".jpg") || 
                            name.toLowerCase().endsWith(".jpeg"));
                        
                        if (files != null && files.length > 0) {
                            for (File file : files) {
                                String fileName = file.getName();
                               
                                int dotIndex = fileName.lastIndexOf('.');
                                String displayName = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
                                pembeliImageCombo.addItem(displayName);
                            }
                            log("Loaded " + files.length + " images for " + selectedType + " buyer type");
                        } else {
                            log("No images found in " + folderPath);
                        }
                    } else {
                        log("Folder not found: " + folderPath);
                    }
                } else {
                    log("Resource URL not found: " + folderPath);
                }
            } catch (Exception e) {
                log("Error loading images: " + e.getMessage());
            }
        }
    }
    
    
    private void setSelectedPembeliImage() {
        String selectedImage = (String) pembeliImageCombo.getSelectedItem();
        if (selectedImage == null || "Random (Default)".equals(selectedImage)) {
            
            transactionsGUI.setPembeli(currentPembeli);
            log("Using random image for " + currentPembeli.getKategori() + " buyer");
            return;
        }
        
        String selectedType = (String) pembeliTypeCombo.getSelectedItem();
        String folderPath = null;
        
        if ("Standard".equalsIgnoreCase(selectedType)) {
            folderPath = "assets/backgrounds/PembeliStandar/";
        } else if ("Rich".equalsIgnoreCase(selectedType)) {
            folderPath = "assets/backgrounds/PembeliTajir/";
        } else if ("Poor".equalsIgnoreCase(selectedType)) {
            folderPath = "assets/backgrounds/PembeliMiskin/";
        }
        
        if (folderPath != null) {
            
            File folder = new File(folderPath);
            File[] files = folder.listFiles((dir, name) -> {
                String nameWithoutExt = name.substring(0, name.lastIndexOf('.'));
                return nameWithoutExt.equals(selectedImage);
            });

            if (files != null && files.length > 0) {
                try {
                    java.awt.Image selectedImageObj = javax.imageio.ImageIO.read(files[0]);
                    java.lang.reflect.Method setPembeliMethod = TransactionsGUI.class.getDeclaredMethod("setPembeli", Pembeli.class);
                    setPembeliMethod.setAccessible(true);
                    transactionsGUI.setPembeli(currentPembeli);
                    java.lang.reflect.Field pembeliImageField = TransactionsGUI.class.getDeclaredField("pembeliImage");
                    pembeliImageField.setAccessible(true);
                    pembeliImageField.set(transactionsGUI, selectedImageObj);
                    java.lang.reflect.Field pembeliNameField = TransactionsGUI.class.getDeclaredField("pembeliName");
                    pembeliNameField.setAccessible(true);
                    String displayName = selectedImage.replace("_", " ");
                    String[] parts = displayName.split(" ");
                    StringBuilder sb = new StringBuilder();
                    for (String part : parts) {
                        if (part.length() > 0) {
                            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
                        }
                    }
                    String finalName = sb.toString().trim();
                    pembeliNameField.set(transactionsGUI, "Pembeli " + currentPembeli.getKategori());
                    java.lang.reflect.Field pembeliTitleLabelField = TransactionsGUI.class.getDeclaredField("pembeliTitleLabel");
                    pembeliTitleLabelField.setAccessible(true);
                    JLabel titleLabel = (JLabel) pembeliTitleLabelField.get(transactionsGUI);
                    titleLabel.setText(finalName + " (Pembeli " + currentPembeli.getKategori() + ")");
                    titleLabel.setVisible(true);
                    transactionsGUI.repaint();
                    log("Set specific image: " + selectedImage + " for " + currentPembeli.getKategori() + " buyer");
                } catch (Exception e) {
                    log("Error setting specific image: " + e.getMessage());
                    transactionsGUI.setPembeli(currentPembeli);
                }
            } else {
                log("Image file not found: " + selectedImage);
                transactionsGUI.setPembeli(currentPembeli);
            }
        }
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            new TransactionsGUIDebugMain().setVisible(true);
        });
    }

    private void styleAllButtonsInTransactionsGUI() {
        Color styledButtonColor = new Color(139, 69, 19);
        Color hoverColor = new Color(160, 82, 45);
        for (Component c : getAllComponents(transactionsGUI)) {
            if (c instanceof JButton btn) {
                btn.setBackground(styledButtonColor);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createRaisedBevelBorder());
                btn.setContentAreaFilled(false);
                btn.setOpaque(true);
                
                for (var ml : btn.getMouseListeners()) btn.removeMouseListener(ml);
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        btn.setBackground(hoverColor);
                        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        btn.setBackground(styledButtonColor);
                        btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                });
                btn.repaint();
                btn.updateUI();
            }
        }
    }

    private java.util.List<Component> getAllComponents(Container c) {
        java.util.List<Component> compList = new java.util.ArrayList<>();
        for (Component comp : c.getComponents()) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }
}
