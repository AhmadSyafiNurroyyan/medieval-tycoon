package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.Barang;
import model.Item;
import model.Pembeli;
import model.Perk;
import model.Player;

public class TransactionsGUI extends JPanel {
    // Base constants for scaling
    private Image tetoImage;
    private Image neuvilletteImage;
    private String currentMessage;
    private boolean isVisible;
    private JPanel parentPanel;
    private final Font dialogFont;
      private static final Color DIALOG_BG = new Color(255, 248, 220);
    private static final Color BORDER_COLOR = new Color(212, 175, 55);
    private static final Color TEXT_COLOR = new Color(60, 40, 10);
    private static final Color SHADOW_COLOR = new Color(120, 90, 30, 80);
    
    private Pembeli currentPembeli;
    private Image pembeliImage;
    private String pembeliName;
    private Player currentPlayer;
      // Trading system variables
    private boolean showTradingInterface = false;
    private Barang selectedBarang;
    private int offerPrice = 0;
    private boolean negotiationPhase = false;
      // Trading buttons
    private JButton sellButton;
    private JButton acceptButton;
    private JButton counterOfferButton;
    private JButton declineButton;
      // Trading input fields
    private JTextField priceField;
    private JPanel pricePanel; // Tambahkan field untuk pricePanel
    private int selectedQuantity = 1;

    // Top UI buttons for all dialogs
    private JButton useItemButton;
    private JButton cartButton;
    
    // --- Gerobak JInternalFrame integration ---
    private JInternalFrame gerobakFrame;
    private JTable gerobakWithPriceTable;
    
    private JDesktopPane desktopPane;

    private boolean transactionCompleted = false;
    private JLabel pembeliTitleLabel; // Label judul pembeli kiri atas

    public TransactionsGUI(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.isVisible = false;
        this.currentMessage = "";
        
        loadTetoImage();
        dialogFont = new Font("Serif", Font.PLAIN, 20);
        
        setOpaque(false);
        setLayout(null);
        // initialize desktop for internal frames
        desktopPane = new JDesktopPane();
        desktopPane.setOpaque(false);
        add(desktopPane);
        setComponentZOrder(desktopPane, 0);

        // Initialize top buttons
        useItemButton = StyledButton.create("Use Item", 16, 100, 40);
        cartButton = StyledButton.create("Gerobak", 16, 100, 40);
        add(useItemButton);
        add(cartButton);
        // Do not let these buttons grab focus so key events (E) still go to GamePanel
        useItemButton.setFocusable(false);
        cartButton.setFocusable(false);
        useItemButton.setVisible(false);
        cartButton.setVisible(false);
        useItemButton.addActionListener(e -> System.out.println("DEBUG: Use Item clicked"));
        cartButton.addActionListener(e -> showGerobakFrame());
        
        pembeliTitleLabel = new JLabel();
        pembeliTitleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        pembeliTitleLabel.setForeground(TEXT_COLOR);
        pembeliTitleLabel.setOpaque(false);
        pembeliTitleLabel.setVisible(false);
        setLayout(null); // Sudah ada, pastikan absolute
        add(pembeliTitleLabel);
    }
    
    private void loadTetoImage() {
        try {
            tetoImage = ImageIO.read(new File("assets/backgrounds/kasane_teto.png"));
        } catch (IOException e) {
            System.err.println("Could not load Kasane Teto image: " + e.getMessage());
            tetoImage = null;
        }
        
        try {
            neuvilletteImage = ImageIO.read(new File("assets/backgrounds/Neuvillette.png"));
        } catch (IOException e) {
            System.err.println("Could not load Neuvillette image: " + e.getMessage());
            neuvilletteImage = null;
        }    }
    
    public void showDialog(String message) {
        this.currentMessage = message;
        this.isVisible = true;
        
        if (parentPanel != null) {
            if (getParent() != parentPanel) {
                parentPanel.add(this);
                parentPanel.setComponentZOrder(this, 0);
            }
            
            setBounds(0, 0, parentPanel.getWidth(), parentPanel.getHeight());
            setVisible(true);
            // Layout and show top buttons
            layoutTopButtons();
            useItemButton.setVisible(true);
            cartButton.setVisible(true);
            parentPanel.repaint();
        }
    }
    
    public void hideDialog() {
        this.isVisible = false;
        setVisible(false);
        // Hide top buttons
        useItemButton.setVisible(false);
        cartButton.setVisible(false);
        
        if (parentPanel != null) {
            parentPanel.repaint();
        }
    }
    
    public boolean isDialogVisible() {
        return isVisible;
    }
    
    /**
     * Check if dialog is in a "no items" state - when there are no goods to sell
     * This allows the dialog to be closed even during trading mode
     */
    public boolean isNoItemsState() {
        if (!isVisible || currentMessage == null) {
            return false;
        }
        
        // Check if the current message indicates a "no items" state
        return currentMessage.contains("tidak membawa barang untuk dijual") ||
               currentMessage.contains("Tidak ada barang dengan harga jual yang ditetapkan");
    }
    
    /**
     * Set Pembeli to be displayed in dialog
     */
    public void setPembeli(Pembeli pembeli) {
        this.currentPembeli = pembeli;
        String displayName = null;
        String jenis = null;
        if (pembeli != null) {
            String tipe = pembeli.getKategori(); // "Tajir", "Miskin", "Standar"
            String folderPath = null;
            if ("Tajir".equalsIgnoreCase(tipe)) {
                folderPath = "assets/backgrounds/PembeliTajir/";
            } else if ("Miskin".equalsIgnoreCase(tipe)) {
                folderPath = "assets/backgrounds/PembeliMiskin/";
            } else if ("Standar".equalsIgnoreCase(tipe)) {
                folderPath = "assets/backgrounds/PembeliStandar/";
            }
            Image loadedImage = null;
            String fileName = null;
            if (folderPath != null) {
                File folder = new File(folderPath);
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg"));
                if (files != null && files.length > 0) {
                    int idx = (int) (Math.random() * files.length);
                    try {
                        loadedImage = ImageIO.read(files[idx]);
                        fileName = files[idx].getName();
                    } catch (IOException e) {
                        loadedImage = neuvilletteImage;
                    }
                } else {
                    loadedImage = neuvilletteImage;
                }
            } else {
                loadedImage = neuvilletteImage;
            }
            this.pembeliImage = loadedImage;
            // Ambil nama dari fileName jika ada, jika tidak fallback ke pembeliName
            if (fileName != null) {
                int dotIdx = fileName.lastIndexOf('.');
                if (dotIdx > 0) fileName = fileName.substring(0, dotIdx);
                String[] parts = fileName.split("_");
                StringBuilder sb = new StringBuilder();
                for (String part : parts) {
                    if (part.length() > 0) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
                }
                displayName = sb.toString().trim();
            } else {
                displayName = "Pembeli";
            }
            jenis = tipe.substring(0, 1).toUpperCase() + tipe.substring(1).toLowerCase();
            this.pembeliName = "Pembeli " + jenis;
        } else {
            this.pembeliImage = null;
            this.pembeliName = null;
            displayName = null;
            jenis = null;
        }
        // Update label judul
        if (displayName != null && jenis != null) {
            pembeliTitleLabel.setText(displayName + " (Pembeli " + jenis + ")");
            pembeliTitleLabel.setVisible(true);
        } else {
            pembeliTitleLabel.setVisible(false);
        }
        repaint();
    }    /**
     * Show dialog for current Pembeli with a proper message
     */
    public void showPembeliDialog() {
        System.out.println("DEBUG: showPembeliDialog() called");
        
        if (currentPembeli != null) {
            String message = "Kamu bertemu dengan " + pembeliName + "! Mereka terlihat tertarik untuk berbelanja.";
            showDialog(message);
            // Langsung mulai trading agar tombol muncul
            startTrading();
            System.out.println("DEBUG: showPembeliDialog completed with pembeli");
        } else {
            // Fallback if no pembeli is set
            showDialog("Kamu menemukan tempat yang menarik untuk berdagang!");
            System.out.println("DEBUG: showPembeliDialog completed without pembeli");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isVisible || currentMessage.isEmpty()) {
            pembeliTitleLabel.setVisible(false);
            return;
        }
          Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        // Dynamic sizing based on window dimensions
        int DIALOG_HEIGHT = Math.max(150, Math.min(300, panelHeight / 4));
        int DIALOG_MARGIN = Math.max(15, panelWidth / 50);
        int TEXT_PADDING = Math.max(10, DIALOG_HEIGHT / 15);
        
        int dialogWidth = panelWidth - (2 * DIALOG_MARGIN);
        int dialogX = DIALOG_MARGIN;
        int dialogY = panelHeight - DIALOG_HEIGHT - DIALOG_MARGIN;
        
        // Draw semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, panelWidth, panelHeight);
        // Calculate dynamic image sizes based on window size with center spacing
        int centerSpacing = Math.max(50, panelWidth / 8); // Dynamic center spacing (12.5% of width)
        int availableWidthPerSide = (panelWidth - centerSpacing) / 2; // Width available for each image side
        int imageMargin = Math.max(10, panelWidth / 80); // Dynamic margin
        int maxImageWidth = Math.max(80, availableWidthPerSide - (imageMargin * 2)); // Use available width minus margins
          // Draw Teto image (left side) - behind dialog
        if (tetoImage != null) {
            int iw = tetoImage.getWidth(this);
            int ih = tetoImage.getHeight(this);            if (iw > 0 && ih > 0) {
                // Dynamic scaling based on window size - 1.5x larger
                double maxW = maxImageWidth * 1.5;
                double maxH = (panelHeight - DIALOG_HEIGHT - 60) * 1.5; // Leave space for dialog
                double scale = Math.min(maxW / iw, maxH / ih);
                
                // Dynamic scale limits based on window size - 1.5x larger
                double minScale = Math.max(0.15, Math.min(panelWidth, panelHeight) / (Math.max(iw, ih) * 4.0)) * 1.5;
                double maxScale = Math.min(3.0, Math.max(panelWidth, panelHeight) / (Math.min(iw, ih) * 0.67)) * 1.5;
                scale = Math.max(scale, minScale);
                scale = Math.min(scale, maxScale);
                
                int w = (int) (iw * scale);
                int h = (int) (ih * scale);
                int x = imageMargin;
                int y = Math.max(10, (panelHeight - DIALOG_HEIGHT - h) / 4); // Dynamic Y positioning
                
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.drawImage(tetoImage, x, y, w, h, this);
            }
        }
          // Draw Pembeli image (right side) - behind dialog, ganti posisi neuvilletteImage
        if (pembeliImage != null) {
            int iw = pembeliImage.getWidth(this);
            int ih = pembeliImage.getHeight(this);            
            if (iw > 0 && ih > 0) {
                double maxW = maxImageWidth * 1.5;
                double maxH = (panelHeight - DIALOG_HEIGHT - 60) * 1.5;
                double scale = Math.min(maxW / iw, maxH / ih);
                double minScale = Math.max(0.15, Math.min(panelWidth, panelHeight) / (Math.max(iw, ih) * 4.0)) * 1.5;
                double maxScale = Math.min(3.0, Math.max(panelWidth, panelHeight) / (Math.min(iw, ih) * 0.67)) * 1.5;
                scale = Math.max(scale, minScale);
                scale = Math.min(scale, maxScale);
                int w = (int) (iw * scale);
                int h = (int) (ih * scale);
                int x = panelWidth - w - imageMargin; // align buyer image to the right edge with margin
                int y = Math.max(10, (panelHeight - DIALOG_HEIGHT - h) / 4); // Dynamic Y positioning
                
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.drawImage(pembeliImage, x, y, w, h, this);
            }
        }
        
        // NOW draw dialog box OVER the images - dialog takes full width
        int textAreaX = dialogX;
        int textAreaWidth = dialogWidth;
        int textAreaHeight = DIALOG_HEIGHT - (2 * TEXT_PADDING);
        
        // Draw dialog box shadow
        g2d.setColor(SHADOW_COLOR);
        g2d.fillRoundRect(textAreaX + 4, dialogY + 4, textAreaWidth, DIALOG_HEIGHT, 15, 15);
        
        // Draw dialog box background
        g2d.setColor(DIALOG_BG);
        g2d.fillRoundRect(textAreaX, dialogY, textAreaWidth, DIALOG_HEIGHT, 15, 15);
        
        // Draw dialog box border
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(BORDER_COLOR);
        g2d.drawRoundRect(textAreaX, dialogY, textAreaWidth, DIALOG_HEIGHT, 15, 15);
        
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(BORDER_COLOR.darker());
        g2d.drawRoundRect(textAreaX + 2, dialogY + 2, textAreaWidth - 4, DIALOG_HEIGHT - 4, 12, 12);
          // Draw text in dialog with dynamic font size
        g2d.setColor(TEXT_COLOR);
        
        // Dynamic font size based on dialog height
        int fontSize = Math.max(16, Math.min(24, DIALOG_HEIGHT / 10));
        Font scaledFont = dialogFont.deriveFont((float) fontSize);
        g2d.setFont(scaledFont);
        
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        int maxLines = Math.max(1, textAreaHeight / lineHeight);
        
        String[] words = currentMessage.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int currentLineCount = 0;
        int textY = dialogY + TEXT_PADDING + fm.getAscent();
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            
            if (fm.stringWidth(testLine) <= textAreaWidth - (2 * TEXT_PADDING)) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0 && currentLineCount < maxLines) {
                    g2d.drawString(currentLine.toString(), textAreaX + TEXT_PADDING, textY);
                    currentLineCount++;
                    textY += lineHeight;
                }
                currentLine = new StringBuilder(word);
                
                if (currentLineCount >= maxLines) {
                    break;
                }
            }
        }
        
        if (currentLine.length() > 0 && currentLineCount < maxLines) {
            g2d.drawString(currentLine.toString(), textAreaX + TEXT_PADDING, textY);
        }        // Draw hint text with dynamic font size
        int hintFontSize = Math.max(14, Math.min(20, DIALOG_HEIGHT / 12));
        g2d.setFont(scaledFont.deriveFont((float) hintFontSize));
        g2d.setColor(TEXT_COLOR.brighter());
        String hintText = "Press E to close";
        FontMetrics hintFm = g2d.getFontMetrics();
        int hintWidth = hintFm.stringWidth(hintText);
        g2d.drawString(hintText, textAreaX + textAreaWidth - hintWidth - TEXT_PADDING, 
                      dialogY + DIALOG_HEIGHT - 8);
        
        g2d.dispose();

        // --- Gambar kotak khusus untuk pembeliTitleLabel di kanan atas dialog ---
        if (pembeliTitleLabel.isVisible()) {
            // Hitung posisi dan ukuran kotak berdasarkan panjang teks label
            Font labelFont = pembeliTitleLabel.getFont();
            FontMetrics labelFm = getFontMetrics(labelFont);
            String labelText = pembeliTitleLabel.getText();
            int textWidth = labelFm.stringWidth(labelText);
            int textHeight = labelFm.getHeight();
            int paddingX = 18;
            int paddingY = 8;
            int boxW = textWidth + paddingX * 2;
            int boxH = textHeight + paddingY;
            int boxX = dialogX + dialogWidth - boxW - TEXT_PADDING; // align ke kanan dialog
            int boxY = dialogY - boxH - 10; // 10px di atas dialog
            // Gambar kotak background
            Graphics2D g2 = (Graphics2D) this.getGraphics();
            if (g2 == null) g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 240, 240)); // warna krem semi transparan
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 16, 16);
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(boxX, boxY, boxW, boxH, 16, 16);
            // Gambar background di belakang teks label (agar tidak transparan)
            g2.setColor(new Color(255, 248, 220, 255));
            g2.fillRoundRect(boxX + 2, boxY + 2, boxW - 4, boxH - 4, 12, 12);
            g2.dispose();
            // Atur posisi label di tengah kotak
            int labelX = boxX + paddingX;
            int labelY = boxY + (boxH - textHeight) / 2;
            pembeliTitleLabel.setBounds(labelX, labelY, textWidth, textHeight);
            pembeliTitleLabel.setFont(labelFont);
            pembeliTitleLabel.setVisible(true);
            setComponentZOrder(pembeliTitleLabel, 0);
        }
    }
      @Override
    public Dimension getPreferredSize() {
        return parentPanel != null ? parentPanel.getSize() : super.getPreferredSize();
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Trigger repaint when bounds change to update dynamic scaling
        if (isVisible) {
            repaint();
            // Update top buttons layout
            layoutTopButtons();
            useItemButton.setVisible(true);
            cartButton.setVisible(true);
            // Recreate trading buttons to keep them centered if trading interface is active (only outside negotiation)
            if (showTradingInterface && !negotiationPhase) {
                createTradingButtons();
            }
        }
    }
      /**
     * Update the parent panel reference and bounds
     */
    public void updateParent(JPanel newParent) {
        this.parentPanel = newParent;
        if (isVisible && parentPanel != null) {
            setBounds(0, 0, parentPanel.getWidth(), parentPanel.getHeight());
        }
    }
    
    /**
     * Set Player untuk trading system
     */
    public void setPlayer(Player player) {
        this.currentPlayer = player;
    }
      /**
     * Mulai trading interface
     */
    public void startTrading() {
        System.out.println("DEBUG: startTrading() called");
        System.out.println("DEBUG: currentPembeli != null: " + (currentPembeli != null));
        System.out.println("DEBUG: currentPlayer != null: " + (currentPlayer != null));
        transactionCompleted = false;
        if (currentPembeli != null && currentPlayer != null) {
            showTradingInterface = true;
            createTradingButtons();
            repaint();
            System.out.println("DEBUG: Trading interface started successfully");
        } else {
            System.out.println("DEBUG: Cannot start trading - missing pembeli or player");
        }
    }    /**
     * Buat button-button untuk trading
     */
    private void createTradingButtons() {
        System.out.println("DEBUG: createTradingButtons() called");
        removeAllTradingButtons();
        
        // Calculate dynamic sizes based on current panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;
        
        // Dynamic sizing with proper centering
        int tradingWidth = Math.min(400, Math.max(300, panelWidth / 3));
        int buttonHeight = Math.max(40, Math.min(50, panelHeight / 15));
        int buttonGap = Math.max(10, Math.min(20, panelHeight / 40));
        int fieldHeight = Math.max(30, Math.min(40, panelHeight / 20));
        
        // Button width for side-by-side buttons
        int buttonWidth = (tradingWidth / 2) - (buttonGap / 2);
        
        // Calculate total height needed for all elements
        int totalElementsHeight = buttonHeight + buttonGap + buttonHeight + buttonGap + buttonHeight + buttonGap + buttonHeight;
        int startY = centerY - (totalElementsHeight / 2);
        int currentY = startY;
        
        // Sell Button - always centered
        sellButton = StyledButton.create("Start Selling", 20, tradingWidth, buttonHeight);
        sellButton.setBounds(centerX - tradingWidth / 2, currentY, tradingWidth, buttonHeight);
        sellButton.addActionListener(e -> startSelling());
        add(sellButton);
        currentY += buttonHeight + buttonGap;

        // Price input field for counter offers (label removed)
        pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // Simpan ke field
        pricePanel.setOpaque(false);
        pricePanel.setBounds(centerX - tradingWidth / 2, currentY, tradingWidth, fieldHeight);
        pricePanel.setVisible(false);

        priceField = new JTextField(20);
        priceField.setHorizontalAlignment(JTextField.CENTER);
        priceField.setFont(new Font("Serif", Font.PLAIN, 14));
        priceField.setPreferredSize(new Dimension(100, fieldHeight));
        pricePanel.add(priceField);

        add(pricePanel);
        currentY += fieldHeight + buttonGap;

        // Accept Button - left side of center
        acceptButton = StyledButton.create("Accept", 16, buttonWidth, buttonHeight);
        acceptButton.setBounds(centerX - tradingWidth / 2, currentY, buttonWidth, buttonHeight);
        acceptButton.setVisible(false);
        acceptButton.addActionListener(e -> acceptOffer());
        add(acceptButton);

        // Counter Offer Button - right side of center
        counterOfferButton = StyledButton.create("Counter", 16, buttonWidth, buttonHeight);
        counterOfferButton.setBounds(centerX + (buttonGap / 2), currentY, buttonWidth, buttonHeight);
        counterOfferButton.setVisible(false);
        counterOfferButton.addActionListener(e -> counterOffer());
        add(counterOfferButton);
        currentY += buttonHeight + buttonGap;

        // Decline Button - full width, centered
        declineButton = StyledButton.create("Decline", 20, tradingWidth, buttonHeight);
        declineButton.setBackground(Color.RED.darker());
        declineButton.setBounds(centerX - tradingWidth / 2, currentY, tradingWidth, buttonHeight);
        declineButton.setVisible(false);
        declineButton.addActionListener(e -> declineOffer());
        add(declineButton);
        
        System.out.println("DEBUG: All trading buttons and fields created");
    }      private void removeAllTradingButtons() {
        if (sellButton != null) remove(sellButton);
        if (acceptButton != null) remove(acceptButton);
        if (counterOfferButton != null) remove(counterOfferButton);
        if (declineButton != null) remove(declineButton);
        if (pricePanel != null) remove(pricePanel); // Ganti dari priceField.getParent() ke pricePanel
    }
      private void startSelling() {
        System.out.println("DEBUG: startSelling() called");
        if (transactionCompleted) {
            currentMessage = "Transaksi sudah selesai. Mulai trading baru untuk menjual lagi.";
            repaint();
            return;
        }
        // Cek barang di gerobak terlebih dahulu, bukan di inventory utama
        Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
        if (barangDiGerobak.isEmpty()) {
            currentMessage = "Kamu tidak membawa barang untuk dijual! Pergi ke home base untuk mengisi gerobak. Tekan E untuk keluar.";
            // Hide selling button and show message only
            if (sellButton != null) {
                sellButton.setVisible(false);
                System.out.println("DEBUG: sellButton hidden (no items in cart)");
            }
            repaint();
            return;
        }
        
        // HANYA pilih barang yang ada di hargaJualBarang (sudah ditetapkan harga jualnya)
        selectedBarang = null;
        for (Map.Entry<Barang, Integer> entry : barangDiGerobak.entrySet()) {
            Barang barang = entry.getKey();
            int hargaJual = currentPlayer.getInventory().getHargaJual(barang);
            if (hargaJual > 0) {  // HANYA barang yang sudah ada harga jualnya
                selectedBarang = barang;
                break;
            }
        }
        // Jika tidak ada barang dengan harga jual, beri pesan error
        if (selectedBarang == null) {
            currentMessage = "Tidak ada barang dengan harga jual yang ditetapkan! Pergi ke home base untuk menetapkan harga jual barang. Tekan E untuk keluar.";
            // Hide selling button and show message only
            if (sellButton != null) {
                sellButton.setVisible(false);
                System.out.println("DEBUG: sellButton hidden (no priced items)");
            }
            repaint();
            return;
        }
        // Pilih jumlah barang yang akan dibeli pembeli secara random (minimal 1, maksimal stok di gerobak)
        int stokBarang = barangDiGerobak.get(selectedBarang);
        if (stokBarang > 1) {
            selectedQuantity = 1 + (int)(Math.random() * stokBarang); // random antara 1 sampai stokBarang
        } else {
            selectedQuantity = 1;
        }
        // Show price panel for counter offers
        if (pricePanel != null) pricePanel.setVisible(true); // Ganti akses ke field
        
        // Hitung harga dengan multiplier dari perk dan item
        System.out.println("DEBUG: Valid item found: " + selectedBarang.getNamaBarang());
        double multiplier = calculatePriceMultiplier();
        int basePrice = currentPlayer.getInventory().getHargaJual(selectedBarang);
        // Karena sudah dicek di atas, basePrice pasti > 0, tidak perlu fallback
        int adjustedUnitPrice = (int) (basePrice * multiplier);
        int totalPrice = adjustedUnitPrice * selectedQuantity;
        
        // Initial refusal chance for miskin
        if (currentPembeli instanceof model.PembeliMiskin) {
            // 30% chance langsung menolak
            if (Math.random() < 0.3) {
                currentMessage = "Pembeli miskin menolak membeli barangmu.";
                repaint();
                return;
            }
        }
          // Buyer makes an offer - consider quantity
        offerPrice = currentPembeli.tawarHarga(totalPrice);
        
        // Ensure not below supplier purchase price (for total quantity)
        int supplierCost = selectedBarang.getHargaBeli() * selectedQuantity;
        offerPrice = Math.max(offerPrice, supplierCost);

        // AUTO-DEAL SCENARIO: If buyer offers more than or equal to player's asking price, auto-complete at player's price
        if (offerPrice >= totalPrice) {
            // Transaction automatically completes at player's price
            currentPlayer.tambahMoney(totalPrice);
            
            // Remove items from cart based on selected quantity
            int jumlahSekarang = barangDiGerobak.getOrDefault(selectedBarang, 0);
            
            if (jumlahSekarang > selectedQuantity) {
                // Still have remaining items
                barangDiGerobak.put(selectedBarang, jumlahSekarang - selectedQuantity);
            } else {
                // No remaining items, remove from cart and reset selling price
                barangDiGerobak.remove(selectedBarang);
                currentPlayer.getInventory().setHargaJual(selectedBarang, 0);
            }
            
            currentMessage = String.format("DEAL! Pembeli menyetujui harga jualmu dan langsung membayar %d koin untuk %s x%d!", 
                totalPrice, selectedBarang.getNamaBarang(), selectedQuantity);
            
            // Deactivate consumable items after transaction
            deactivateConsumableItems();
            transactionCompleted = true;
            
            // Hide selling button and disable further transactions
            if (sellButton != null) {
                sellButton.setVisible(false);
                sellButton.setEnabled(false);
            }
            
            repaint();
            return; // Exit early, no negotiation needed
        }

        currentMessage = String.format("Pembeli tertarik dengan %s x%d (Total Harga: %d).\nMereka menawar: %d", 
                selectedBarang.getNamaBarang(), selectedQuantity, totalPrice, offerPrice);
        
        // Set suggested counter offer price
        priceField.setText(String.valueOf(totalPrice));
          
        // Show negotiation buttons
        System.out.println("DEBUG: About to hide sellButton and show negotiation buttons");
        
        if (sellButton != null) {
            sellButton.setVisible(false);
            System.out.println("DEBUG: sellButton hidden successfully");
        } else {
            System.out.println("DEBUG: sellButton is null!");
        }
        
        if (acceptButton != null) {
            acceptButton.setVisible(true);
            System.out.println("DEBUG: acceptButton shown");
        } else {
            System.out.println("DEBUG: acceptButton is null!");
        }
        
        if (counterOfferButton != null) {
            counterOfferButton.setVisible(true);
            System.out.println("DEBUG: counterOfferButton shown");
        } else {
            System.out.println("DEBUG: counterOfferButton is null!");
        }
        
        if (declineButton != null) {
            declineButton.setVisible(true);
            System.out.println("DEBUG: declineButton shown");
        } else {
            System.out.println("DEBUG: declineButton is null!");
        }
        
        negotiationPhase = true;
        
        // Force a repaint and revalidate
        revalidate();
        repaint();
        System.out.println("DEBUG: repaint called");
    }private double calculatePriceMultiplier() {
        double multiplier = 1.0;
        
        // Multiplier dari Perk yang aktif dibawa untuk jualan
        for (Perk perk : currentPlayer.getPerkDipilihUntukJualan()) {
            if (perk.isActive()) {
                multiplier += perk.getKesaktianSekarang();
            }
        }
        
        // Multiplier dari Item/Potion yang aktif dan dibawa di gerobak
        for (Item item : currentPlayer.getInventory().getItemDibawa()) {
            if (item.isActive()) {
                // Item memberikan bonus berdasarkan tipe item
                if (item.isSemproten()) {
                    multiplier += item.getSemprotenPriceBoost();
                } else if (item.isHipnotis()) {
                    multiplier += item.getHipnotisChance() * 0.1; // Convert chance to small multiplier
                } else if (item.isTip()) {
                    multiplier += item.getTipBonusRate() * 0.5; // Convert tip rate to small multiplier
                }
                // Jampi dan Peluit tidak mempengaruhi multiplier harga
            }
        }
        
        return multiplier;
    }    private void acceptOffer() {
        if (currentPembeli.putuskanTransaksi(offerPrice)) {
            // Transaksi berhasil
            currentPlayer.tambahMoney(offerPrice);
            
            // Hapus barang dari gerobak (barangDibawa) berdasarkan jumlah yang dipilih
            Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
            int jumlahSekarang = barangDiGerobak.getOrDefault(selectedBarang, 0);
            
            if (jumlahSekarang > selectedQuantity) {
                // Masih ada sisa barang
                barangDiGerobak.put(selectedBarang, jumlahSekarang - selectedQuantity);
            } else {
                // Tidak ada sisa barang, hapus dari gerobak dan reset harga jual
                barangDiGerobak.remove(selectedBarang);
                currentPlayer.getInventory().setHargaJual(selectedBarang, 0);
            }
              currentMessage = String.format("Transaksi berhasil! Kamu menjual %s x%d seharga %d koin.", 
                selectedBarang.getNamaBarang(), selectedQuantity, offerPrice);
            
            // Deaktifkan item consumable setelah digunakan
            deactivateConsumableItems();
            transactionCompleted = true;
        } else {
            currentMessage = "Pembeli membatalkan transaksi.";
        }
        hideNegotiationButtons();
        sellButton.setVisible(true);
        sellButton.setText("Sell More");
        // Setelah transaksi selesai, disable sellButton jika transaksi sukses
        if (transactionCompleted && sellButton != null) {
            sellButton.setEnabled(false);
        }
        repaint();
    }    private void counterOffer() {
        try {
            int counterPrice = Integer.parseInt(priceField.getText());
            // Validate counter price is not below supplier cost
            int supplierCost = selectedBarang.getHargaBeli() * selectedQuantity;
            if (counterPrice < supplierCost) {
                currentMessage = String.format("Harga counter terlalu rendah! Minimal harga: %d (harga beli: %d x %d)", 
                    supplierCost, selectedBarang.getHargaBeli(), selectedQuantity);
                repaint();
                return;
            }
            boolean accepted = currentPembeli.putuskanTransaksi(counterPrice);
            if (!accepted && currentPembeli.chanceAcceptCounter(counterPrice, offerPrice)) {
                accepted = true;
            }
            if (accepted) {
                currentPlayer.tambahMoney(counterPrice);
                // Hapus barang dari gerobak berdasarkan jumlah yang dipilih
                Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
                int jumlahSekarang = barangDiGerobak.getOrDefault(selectedBarang, 0);
                if (jumlahSekarang > selectedQuantity) {
                    barangDiGerobak.put(selectedBarang, jumlahSekarang - selectedQuantity);
                } else {
                    barangDiGerobak.remove(selectedBarang);
                    currentPlayer.getInventory().setHargaJual(selectedBarang, 0);
                }
                currentMessage = String.format("Counter offer diterima! Kamu menjual %s x%d seharga %d koin.", 
                    selectedBarang.getNamaBarang(), selectedQuantity, counterPrice);
                deactivateConsumableItems();
                transactionCompleted = true;
            } else {
                // Buyer rejected, implement multi-round bargaining
                String reason = null;
                if (currentPembeli instanceof model.PembeliStandar) {
                    reason = ((model.PembeliStandar)currentPembeli).getLastRejectionReason();
                } else if (currentPembeli instanceof model.PembeliTajir) {
                    reason = ((model.PembeliTajir)currentPembeli).getLastRejectionReason();
                } else if (currentPembeli instanceof model.PembeliMiskin) {
                    reason = ((model.PembeliMiskin)currentPembeli).getLastRejectionReason();
                }
                if (reason != null && !reason.isEmpty()) {
                    currentMessage = reason;
                } else {
                    int newOffer = currentPembeli.tawarHarga(counterPrice);
                    if (newOffer != counterPrice) {
                        offerPrice = Math.max(newOffer, supplierCost); // Ensure not below cost
                        currentMessage = String.format("Pembeli menolak offer-mu dan memberikan counter: %d", offerPrice);
                        priceField.setText(String.valueOf(offerPrice)); // Tampilkan harga counter pembeli (bisa lebih tinggi dari counter player)
                        repaint();
                        return;
                    } else {
                        currentMessage = "Pembeli menolak counter offer mu dan mengakhiri negosiasi.";
                    }
                }
            }
            hideNegotiationButtons();
            sellButton.setVisible(true);
            sellButton.setText("Sell More");
            if (transactionCompleted && sellButton != null) {
                sellButton.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            currentMessage = "Masukkan harga yang valid!";
        }
        repaint();
    }
    
    private void declineOffer() {
        if (transactionCompleted) {
            currentMessage = "Transaksi sudah selesai. Tidak bisa menolak lagi.";
            repaint();
            return;
        }
        currentMessage = "Kamu menolak tawaran pembeli.";
        hideNegotiationButtons();
        sellButton.setVisible(true);
        repaint();
    }
    
    private void hideNegotiationButtons() {
        acceptButton.setVisible(false);
        counterOfferButton.setVisible(false);
        declineButton.setVisible(false);
        negotiationPhase = false;
    }    
    
    private void deactivateConsumableItems() {
        // Deaktifkan item consumable yang dibawa di gerobak setelah transaksi
        for (Item item : currentPlayer.getInventory().getItemDibawa()) {
            if (item.isActive()) {
                item.deactivate();
                System.out.println("Item " + item.getNama() + " telah dinonaktifkan setelah transaksi.");
            }
        }
    }
    
    /**
     * Layout the top-center buttons for Use Item and Gerobak
     */
    private void layoutTopButtons() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int margin = Math.max(15, panelWidth / 50);
        int buttonHeight = Math.max(30, panelHeight / 20);
        int buttonWidth = Math.max(80, panelWidth / 8);
        int spacing = Math.max(10, panelWidth / 100);
        int totalWidth = buttonWidth * 2 + spacing;
        int startX = (panelWidth - totalWidth) / 2;
        int y = margin;
        useItemButton.setBounds(startX, y, buttonWidth, buttonHeight);
        cartButton.setBounds(startX + buttonWidth + spacing, y, buttonWidth, buttonHeight);
    }
    
    // Revised Gerobak window: only 'with price' list, styled like HomeBasePanel
    private void showGerobakFrame() {
        if (gerobakFrame != null && gerobakFrame.isVisible()) {
            gerobakFrame.toFront();
            return;
        }
        // Create internal frame
        gerobakFrame = new JInternalFrame("Gerobak - Barang Sudah Ada Harga Jual", true, true, true, true);
        gerobakFrame.setSize(600, 350);
        gerobakFrame.setLayout(new BorderLayout());
        gerobakFrame.getContentPane().setBackground(DIALOG_BG);

        // Table for items with price
        gerobakWithPriceTable = new JTable();
        JScrollPane scroll = new JScrollPane(gerobakWithPriceTable);
        scroll.getViewport().setBackground(new Color(255, 255, 240));
        scroll.setBorder(BorderFactory.createTitledBorder("Barang Sudah Ada Harga Jual"));
        gerobakFrame.add(scroll, BorderLayout.CENTER);

        // Close button
        JButton closeBtn = StyledButton.create("Tutup", 14, 100, 32);
        closeBtn.addActionListener(e -> gerobakFrame.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(closeBtn);
        gerobakFrame.add(btnPanel, BorderLayout.SOUTH);

        // Add to parent layered pane
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null) {
            JLayeredPane layered = root.getLayeredPane();
            layered.add(gerobakFrame, JLayeredPane.POPUP_LAYER);
            // center
            gerobakFrame.setLocation((layered.getWidth() - gerobakFrame.getWidth()) / 2,
                                     (layered.getHeight() - gerobakFrame.getHeight()) / 2);
            gerobakFrame.setVisible(true);
            gerobakFrame.toFront();
            // Populate data
            updateGerobakTablesLocal();
        } else {
            System.err.println("[ERROR] Tidak dapat menemukan root pane untuk Gerobak frame");
        }
    }

    // Update Gerobak 'with price' table only
    private void updateGerobakTablesLocal() {
        if (currentPlayer == null || currentPlayer.getInventory() == null) return;
        Map<Barang,Integer> map = currentPlayer.getInventory().getBarangDibawaMutable();
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object[]{"Icon","Nama","Kategori","Kesegaran","Jumlah","Harga Jual"}, 0);
        for (Map.Entry<Barang,Integer> entry : map.entrySet()) {
            Barang b = entry.getKey();
            int count = entry.getValue();
            int harga = currentPlayer.getInventory().getHargaJual(b);
            if (harga > 0) {
                ImageIcon icon = GamePanel.getIcon(b.getIconPath(), 32, 32);
                if (icon == null) icon = GamePanel.getIcon(b.getNamaBarang().toLowerCase().replace(' ', '_'), 32, 32);
                model.addRow(new Object[]{icon, b.getNamaBarang(), b.getKategori(), b.getKesegaran(), count, harga + "G"});
            }
        }
        gerobakWithPriceTable.setModel(model);
        gerobakWithPriceTable.setRowHeight(36);
        // Set column widths
        gerobakWithPriceTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        gerobakWithPriceTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        gerobakWithPriceTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        gerobakWithPriceTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        gerobakWithPriceTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        gerobakWithPriceTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        // Renderers
        gerobakWithPriceTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
             JLabel label = new JLabel();
             label.setHorizontalAlignment(SwingConstants.CENTER);
             label.setIcon(value instanceof Icon ? (Icon) value : null);
             return label;
         });
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 6; i++) {
            gerobakWithPriceTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
