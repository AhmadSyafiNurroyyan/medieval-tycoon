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

public class DialogSystem extends JPanel {
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
    private static final Color BUTTON_BG = new Color(139, 69, 19);
    private static final Color BUTTON_HOVER = new Color(160, 82, 45);
    private static final Color BUTTON_TEXT = Color.WHITE;
    
    private Pembeli currentPembeli;
    private Image pembeliImage;
    private String pembeliName;
    private Player currentPlayer;
    
    // Trading system variables
    private boolean showTradingInterface = false;
    private Barang selectedBarang;
    private int offerPrice = 0;
    private int finalPrice = 0;
    private boolean negotiationPhase = false;
    private boolean transactionComplete = false;
    
    // Trading buttons
    private JButton sellButton;
    private JButton acceptButton;
    private JButton counterOfferButton;
    private JButton declineButton;
    private JTextField priceField;
    
    public DialogSystem(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.isVisible = false;
        this.currentMessage = "";
        
        loadTetoImage();
        dialogFont = new Font("Serif", Font.PLAIN, 20);
        
        setOpaque(false);
        setLayout(null);
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
            parentPanel.repaint();
        }
    }
    
    public void hideDialog() {
        this.isVisible = false;
        setVisible(false);
        
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
            if (folderPath != null) {
                File folder = new File(folderPath);
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg"));
                if (files != null && files.length > 0) {
                    int idx = (int) (Math.random() * files.length);
                    try {
                        loadedImage = ImageIO.read(files[idx]);
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
            this.pembeliName = "Pembeli " + tipe.substring(0, 1).toUpperCase() + tipe.substring(1).toLowerCase();
        } else {
            this.pembeliImage = null;
            this.pembeliName = null;
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
        
        if (currentPembeli != null && currentPlayer != null) {
            showTradingInterface = true;
            createTradingButtons();
            repaint();
            System.out.println("DEBUG: Trading interface started successfully");
        } else {
            System.out.println("DEBUG: Cannot start trading - missing pembeli or player");
        }
    }/**
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
        
        // Tambahkan deklarasi buttonWidth di sini
        int buttonWidth = (tradingWidth / 2) - (buttonGap / 2);
        
        // Calculate total height needed for all elements
        int totalElementsHeight = buttonHeight + buttonGap + fieldHeight + buttonGap + buttonHeight + buttonGap + buttonHeight;
        int startY = centerY - (totalElementsHeight / 2);
        
        // Sell Button - always centered
        sellButton = StyledButton.create("Start Selling", 20, tradingWidth, buttonHeight);
        sellButton.setBounds(centerX - tradingWidth / 2, startY, tradingWidth, buttonHeight);
        sellButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                startSelling();
            }
        });
        add(sellButton);
        System.out.println("DEBUG: sellButton created and added");

        // Price Field - centered
        priceField = new JTextField();
        priceField.setFont(new Font("Serif", Font.PLAIN, Math.max(16, Math.min(24, buttonHeight / 2))));
        priceField.setBounds(centerX - tradingWidth / 2, startY + buttonHeight + buttonGap, tradingWidth, fieldHeight);
        priceField.setVisible(false);
        add(priceField);
        System.out.println("DEBUG: priceField created and added");

        // Accept Button - left side of center
        acceptButton = StyledButton.create("Accept", 16, buttonWidth, buttonHeight);
        acceptButton.setBounds(centerX - tradingWidth / 2, startY + buttonHeight + buttonGap + fieldHeight + buttonGap, buttonWidth, buttonHeight);
        acceptButton.setVisible(false);
        acceptButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                acceptOffer();
            }
        });
        add(acceptButton);
        System.out.println("DEBUG: acceptButton created and added");

        // Counter Offer Button - right side of center
        counterOfferButton = StyledButton.create("Counter", 16, buttonWidth, buttonHeight);
        counterOfferButton.setBounds(centerX + (buttonGap / 2), startY + buttonHeight + buttonGap + fieldHeight + buttonGap, buttonWidth, buttonHeight);
        counterOfferButton.setVisible(false);
        counterOfferButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                counterOffer();
            }
        });
        add(counterOfferButton);
        System.out.println("DEBUG: counterOfferButton created and added");

        // Decline Button - full width, centered
        declineButton = StyledButton.create("Decline", 20, tradingWidth, buttonHeight);
        declineButton.setBackground(Color.RED.darker());
        declineButton.setBounds(centerX - tradingWidth / 2, startY + buttonHeight * 2 + buttonGap * 3 + fieldHeight, tradingWidth, buttonHeight);
        declineButton.setVisible(false);
        declineButton.addActionListener(new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                declineOffer();
            }
        });
        add(declineButton);
        System.out.println("DEBUG: declineButton created and added");
    }
    
    private void removeAllTradingButtons() {
        if (sellButton != null) remove(sellButton);
        if (acceptButton != null) remove(acceptButton);
        if (counterOfferButton != null) remove(counterOfferButton);
        if (declineButton != null) remove(declineButton);
        if (priceField != null) remove(priceField);
    }    
    private void startSelling() {
        System.out.println("DEBUG: startSelling() called");
        
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
        // Hitung harga dengan multiplier dari perk dan item
        System.out.println("DEBUG: Valid item found: " + selectedBarang.getNamaBarang());
        double multiplier = calculatePriceMultiplier();
        int basePrice = currentPlayer.getInventory().getHargaJual(selectedBarang);
        // Karena sudah dicek di atas, basePrice pasti > 0, tidak perlu fallback
        int adjustedPrice = (int) (basePrice * multiplier);
        
        // Pembeli tawar harga
        offerPrice = currentPembeli.tawarHarga(adjustedPrice);
        
        currentMessage = String.format("Pembeli tertarik dengan %s (Harga: %d).\nMereka menawar: %d", 
                selectedBarang.getNamaBarang(), adjustedPrice, offerPrice);
          // Show negotiation buttons
        System.out.println("DEBUG: About to hide sellButton and show negotiation buttons");
        
        if (sellButton != null) {
            sellButton.setVisible(false);
            System.out.println("DEBUG: sellButton hidden successfully");
        } else {
            System.out.println("DEBUG: sellButton is null!");
        }
        
        if (priceField != null) {
            priceField.setVisible(true);
            priceField.setText(String.valueOf(adjustedPrice));
            System.out.println("DEBUG: priceField shown");
        } else {
            System.out.println("DEBUG: priceField is null!");
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
    }    private double calculatePriceMultiplier() {
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
    }
      private void acceptOffer() {
        if (currentPembeli.putuskanTransaksi(offerPrice)) {
            // Transaksi berhasil
            currentPlayer.tambahMoney(offerPrice);
            
            // Hapus barang dari gerobak (barangDibawa), bukan dari inventory utama
            Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
            int jumlahSekarang = barangDiGerobak.getOrDefault(selectedBarang, 0);
            if (jumlahSekarang > 1) {
                barangDiGerobak.put(selectedBarang, jumlahSekarang - 1);
            } else {
                barangDiGerobak.remove(selectedBarang);
                // Hapus juga harga jual untuk barang ini
                currentPlayer.getInventory().setHargaJual(selectedBarang, 0);
            }
            
            currentMessage = String.format("Transaksi berhasil! Kamu mendapat %d koin.", offerPrice);
            transactionComplete = true;
            
            // Deaktifkan item consumable setelah digunakan
            deactivateConsumableItems();
        } else {
            currentMessage = "Pembeli membatalkan transaksi.";
        }
        
        hideNegotiationButtons();
        sellButton.setVisible(true);
        sellButton.setText("Sell More");
        repaint();
    }
      private void counterOffer() {
        try {
            int counterPrice = Integer.parseInt(priceField.getText());
            finalPrice = counterPrice;
              if (currentPembeli.putuskanTransaksi(counterPrice)) {
                currentPlayer.tambahMoney(counterPrice);
                
                // Hapus barang dari gerobak (barangDibawa), bukan dari inventory utama
                Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
                int jumlahSekarang = barangDiGerobak.getOrDefault(selectedBarang, 0);
                if (jumlahSekarang > 1) {
                    barangDiGerobak.put(selectedBarang, jumlahSekarang - 1);
                } else {
                    barangDiGerobak.remove(selectedBarang);
                    // Hapus juga harga jual untuk barang ini
                    currentPlayer.getInventory().setHargaJual(selectedBarang, 0);
                }
                
                currentMessage = String.format("Counter offer diterima! Kamu mendapat %d koin.", counterPrice);
                transactionComplete = true;
                
                deactivateConsumableItems();
            } else {
                currentMessage = "Pembeli menolak counter offer mu.";
            }
            
            hideNegotiationButtons();
            sellButton.setVisible(true);
            sellButton.setText("Sell More");
        } catch (NumberFormatException e) {
            currentMessage = "Masukkan harga yang valid!";
        }
        
        repaint();
    }
    
    private void declineOffer() {
        currentMessage = "Kamu menolak tawaran pembeli.";
        hideNegotiationButtons();
        sellButton.setVisible(true);
        repaint();
    }
    
    private void hideNegotiationButtons() {
        priceField.setVisible(false);
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
}
