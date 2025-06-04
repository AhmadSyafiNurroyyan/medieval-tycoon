/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package gui;

import MapManager.RandomTriggerZoneManager;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.Barang;
import model.Item;
import model.ItemEffectManager;
import model.Pembeli;
import model.PerksManagement;
import model.Player;

public class TransactionsGUI extends JPanel {
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
    private ItemEffectManager itemEffectManager;
    private Barang selectedBarang;
    private int offerPrice = 0;
    private boolean negotiationPhase = false;
    private JButton sellButton;
    private JButton acceptButton;
    private JButton counterOfferButton;
    private JButton declineButton;
    private JButton closeButton;
    private JTextField priceField;
    private JPanel pricePanel;
    private int selectedQuantity = 1;
    private JButton useItemButton;
    private JButton cartButton;
    private JInternalFrame gerobakFrame;
    private JTable gerobakWithPriceTable;
    private JDesktopPane desktopPane;
    private boolean transactionCompleted = false;
    private JLabel pembeliTitleLabel;
    private MapManager.TriggerZoneManager triggerZoneManager;
    private String currentTriggerZoneId;    
    private JInternalFrame itemFrame;
    private JTable itemGerobakTable;
    private MapManager.RandomTriggerZoneManager randomTriggerZoneManager;
    private PerksManagement perksManagement;

    public TransactionsGUI(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.isVisible = false;
        this.currentMessage = "";

        loadTetoImage();
        dialogFont = new Font("Serif", Font.PLAIN, 20);

        setOpaque(false);
        setLayout(null);
        desktopPane = new JDesktopPane();
        desktopPane.setOpaque(false);
        add(desktopPane);
        setComponentZOrder(desktopPane, getComponentCount() - 1);

        useItemButton = StyledButton.create("Use Item", 16, 100, 40);
        cartButton = StyledButton.create("Gerobak", 16, 100, 40);
        add(useItemButton);
        add(cartButton);
        useItemButton.setFocusable(false);
        cartButton.setFocusable(false);
        useItemButton.setVisible(false);
        cartButton.setVisible(false);
        useItemButton.addActionListener(e -> showItemFrame());
        cartButton.addActionListener(e -> showGerobakFrame());

        pembeliTitleLabel = new JLabel();
        pembeliTitleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        pembeliTitleLabel.setForeground(TEXT_COLOR);
        pembeliTitleLabel.setOpaque(false);
        pembeliTitleLabel.setVisible(false);
        setLayout(null);
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
        }
    }

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
            layoutTopButtons();
            useItemButton.setVisible(true);
            cartButton.setVisible(true);
            parentPanel.repaint();
        }
    }

    public void hideDialog() {
        this.isVisible = false;
        setVisible(false);
        useItemButton.setVisible(false);
        cartButton.setVisible(false);

        if (parentPanel != null) {
            parentPanel.repaint();
        }
    }

    public boolean isDialogVisible() {
        return isVisible;
    }

    public boolean isNoItemsState() {
        if (!isVisible || currentMessage == null) {
            return false;
        }

        return currentMessage.contains("tidak membawa barang untuk dijual") ||
                currentMessage.contains("Tidak ada barang dengan harga jual yang ditetapkan");
    }

    public boolean isTransactionCompleted() {
        return transactionCompleted && closeButton != null && closeButton.isVisible();
    }

    public void setPembeli(Pembeli pembeli) {
        this.currentPembeli = pembeli;
        String displayName = null;
        String jenis = null;
        if (pembeli != null) {
            String tipe = pembeli.getKategori();
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
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png")
                        || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg"));
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
            if (fileName != null) {
                int dotIdx = fileName.lastIndexOf('.');
                if (dotIdx > 0)
                    fileName = fileName.substring(0, dotIdx);
                String[] parts = fileName.split("_");
                StringBuilder sb = new StringBuilder();
                for (String part : parts) {
                    if (part.length() > 0)
                        sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
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
        if (displayName != null && jenis != null) {
            pembeliTitleLabel.setText(displayName + " (Pembeli " + jenis + ")");
            pembeliTitleLabel.setVisible(true);
        } else {
            pembeliTitleLabel.setVisible(false);
        }
        repaint();
    }

    public void showPembeliDialog() {
        System.out.println("DEBUG: showPembeliDialog() called");

        if (currentPembeli != null) {
            String message = "Kamu bertemu dengan " + pembeliName + "! Mereka terlihat tertarik untuk berbelanja.";
            showDialog(message);
            startTrading();
            System.out.println("DEBUG: showPembeliDialog completed with pembeli");
        } else {
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

        int DIALOG_HEIGHT = Math.max(150, Math.min(300, panelHeight / 4));
        int DIALOG_MARGIN = Math.max(15, panelWidth / 50);
        int TEXT_PADDING = Math.max(10, DIALOG_HEIGHT / 15);

        int dialogWidth = panelWidth - (2 * DIALOG_MARGIN);
        int dialogX = DIALOG_MARGIN;
        int dialogY = panelHeight - DIALOG_HEIGHT - DIALOG_MARGIN;

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, panelWidth, panelHeight);
        int centerSpacing = Math.max(50, panelWidth / 8);
        int availableWidthPerSide = (panelWidth - centerSpacing) / 2;
        int imageMargin = Math.max(10, panelWidth / 80);
        int maxImageWidth = Math.max(80, availableWidthPerSide - (imageMargin * 2));
        if (tetoImage != null) {
            int iw = tetoImage.getWidth(this);
            int ih = tetoImage.getHeight(this);
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
                int x = imageMargin;
                int y = Math.max(10, (panelHeight - DIALOG_HEIGHT - h) / 4);

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                g2d.drawImage(tetoImage, x, y, w, h, this);
            }
        }
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
                int x = panelWidth - w - imageMargin;
                int y = Math.max(10, (panelHeight - DIALOG_HEIGHT - h) / 4);

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                g2d.drawImage(pembeliImage, x, y, w, h, this);
            }
        }

        int textAreaX = dialogX;
        int textAreaWidth = dialogWidth;
        int textAreaHeight = DIALOG_HEIGHT - (2 * TEXT_PADDING);

        g2d.setColor(SHADOW_COLOR);
        g2d.fillRoundRect(textAreaX + 4, dialogY + 4, textAreaWidth, DIALOG_HEIGHT, 15, 15);

        g2d.setColor(DIALOG_BG);
        g2d.fillRoundRect(textAreaX, dialogY, textAreaWidth, DIALOG_HEIGHT, 15, 15);

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(BORDER_COLOR);
        g2d.drawRoundRect(textAreaX, dialogY, textAreaWidth, DIALOG_HEIGHT, 15, 15);

        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(BORDER_COLOR.darker());
        g2d.drawRoundRect(textAreaX + 2, dialogY + 2, textAreaWidth - 4, DIALOG_HEIGHT - 4, 12, 12);
        g2d.setColor(TEXT_COLOR);

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
        }
        int hintFontSize = Math.max(14, Math.min(20, DIALOG_HEIGHT / 12));
        g2d.setFont(scaledFont.deriveFont((float) hintFontSize));
        g2d.setColor(TEXT_COLOR.brighter());
        String hintText = "Press E to close";
        FontMetrics hintFm = g2d.getFontMetrics();
        int hintWidth = hintFm.stringWidth(hintText);
        g2d.drawString(hintText, textAreaX + textAreaWidth - hintWidth - TEXT_PADDING,
                dialogY + DIALOG_HEIGHT - 8);

        if (pembeliTitleLabel.isVisible()) {
            Font labelFont = pembeliTitleLabel.getFont();
            FontMetrics labelFm = getFontMetrics(labelFont);
            String labelText = pembeliTitleLabel.getText();
            int textWidth = labelFm.stringWidth(labelText);
            int textHeight = labelFm.getHeight();
            int paddingX = 18;
            int paddingY = 8;
            int boxW = textWidth + paddingX * 2;
            int boxH = textHeight + paddingY;
            int boxX = dialogX + dialogWidth - boxW - TEXT_PADDING;
            int boxY = dialogY - boxH - 10;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(255, 255, 240, 240));
            g2d.fillRoundRect(boxX, boxY, boxW, boxH, 16, 16);
            g2d.setColor(BORDER_COLOR);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(boxX, boxY, boxW, boxH, 16, 16);
            g2d.setColor(new Color(255, 248, 220, 255));
            g2d.fillRoundRect(boxX + 2, boxY + 2, boxW - 4, boxH - 4, 12, 12);
            int labelX = boxX + paddingX;
            int labelY = boxY + (boxH - textHeight) / 2;
            pembeliTitleLabel.setBounds(labelX, labelY, textWidth, textHeight);
            pembeliTitleLabel.setFont(labelFont);
            pembeliTitleLabel.setVisible(true);
            setComponentZOrder(pembeliTitleLabel, 0);
        }

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return parentPanel != null ? parentPanel.getSize() : super.getPreferredSize();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (isVisible) {
            repaint();
            layoutTopButtons();
            useItemButton.setVisible(true);
            cartButton.setVisible(true);
        }
    }

    public void updateParent(JPanel newParent) {
        this.parentPanel = newParent;
        if (isVisible && parentPanel != null) {
            setBounds(0, 0, parentPanel.getWidth(), parentPanel.getHeight());
        }
    }

    public void setPlayer(Player player) {
        this.currentPlayer = player;
        this.itemEffectManager = new ItemEffectManager(player);
    }

    public void setTriggerZoneManager(MapManager.TriggerZoneManager triggerZoneManager) {
        this.triggerZoneManager = triggerZoneManager;
    }

    public void setCurrentTriggerZoneId(String zoneId) {
        this.currentTriggerZoneId = zoneId;
    }

    public void startTrading() {
        System.out.println("DEBUG: startTrading() called");
        System.out.println("DEBUG: currentPembeli != null: " + (currentPembeli != null));
        System.out.println("DEBUG: currentPlayer != null: " + (currentPlayer != null));

        transactionCompleted = false;
        negotiationPhase = false;

        if (currentPembeli != null && currentPlayer != null) {
            createTradingButtons();
            repaint();
            System.out.println("DEBUG: Trading interface started successfully");
        } else {
            System.out.println("DEBUG: Cannot start trading - missing pembeli or player");
        }
    }

    private void createTradingButtons() {
        System.out.println("DEBUG: createTradingButtons() called");
        removeAllTradingButtons();

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;

        int tradingWidth = Math.min(400, Math.max(300, panelWidth / 3));
        int buttonHeight = Math.max(40, Math.min(50, panelHeight / 15));
        int buttonGap = Math.max(10, Math.min(20, panelHeight / 40));
        int fieldHeight = Math.max(30, Math.min(40, panelHeight / 20));

        int buttonWidth = (tradingWidth / 2) - (buttonGap / 2);

        int totalElementsHeight = buttonHeight + buttonGap + buttonHeight + buttonGap + buttonHeight + buttonGap
                + buttonHeight;
        int startY = centerY - (totalElementsHeight / 2);
        int currentY = startY;

        int fontSize = Math.max(2, Math.min(8, panelHeight / 10));

        sellButton = StyledButton.create("Start Selling", 20, tradingWidth, buttonHeight);
        sellButton.setBounds(centerX - tradingWidth / 2, currentY, tradingWidth, buttonHeight);
        sellButton.addActionListener(e -> startSelling());
        sellButton.setEnabled(true);
        add(sellButton);
        currentY += buttonHeight + buttonGap;
        pricePanel = new JPanel();
        pricePanel.setLayout(null);
        pricePanel.setOpaque(false);
        pricePanel.setBounds(centerX - tradingWidth / 2, currentY, tradingWidth, fieldHeight);
        pricePanel.setVisible(false);
        add(pricePanel);

        int spacing = 3;
        int priceFieldWidth = Math.max(80, tradingWidth / 5);

        int totalButtons = 6;
        int availableForButtons = tradingWidth - priceFieldWidth - (spacing * 8);
        int smallButtonWidth = Math.max(35, availableForButtons / totalButtons);
        int smallButtonHeight = fieldHeight;

        int totalContentWidth = (smallButtonWidth * totalButtons) + priceFieldWidth + (spacing * 8);
        int startX = Math.max(0, (tradingWidth - totalContentWidth) / 2);

        JButton minus10000Button = StyledButton.create("-10000", fontSize, smallButtonWidth, smallButtonHeight);
        minus10000Button.setBounds(startX, 0, smallButtonWidth, smallButtonHeight);
        minus10000Button.addActionListener(e -> adjustPrice(-10000));
        pricePanel.add(minus10000Button);

        JButton minus1000Button = StyledButton.create("-1000", fontSize, smallButtonWidth, smallButtonHeight);
        minus1000Button.setBounds(startX + smallButtonWidth + spacing, 0, smallButtonWidth, smallButtonHeight);
        minus1000Button.addActionListener(e -> adjustPrice(-1000));
        pricePanel.add(minus1000Button);

        JButton minus1Button = StyledButton.create("-", fontSize + 2, smallButtonWidth, smallButtonHeight);
        minus1Button.setBounds(startX + (smallButtonWidth + spacing) * 2, 0, smallButtonWidth, smallButtonHeight);
        minus1Button.addActionListener(e -> adjustPrice(-1));
        pricePanel.add(minus1Button);

        priceField = new JTextField();
        priceField.setHorizontalAlignment(JTextField.CENTER);
        priceField.setFont(new Font("Serif", Font.PLAIN, Math.max(10, fieldHeight / 3)));
        priceField.setBounds(startX + (smallButtonWidth + spacing) * 3, 0, priceFieldWidth, fieldHeight);
        pricePanel.add(priceField);

        JButton plus1Button = StyledButton.create("+", fontSize + 2, smallButtonWidth, smallButtonHeight);
        plus1Button.setBounds(startX + (smallButtonWidth + spacing) * 3 + priceFieldWidth + spacing, 0,
                smallButtonWidth, smallButtonHeight);
        plus1Button.addActionListener(e -> adjustPrice(1));
        pricePanel.add(plus1Button);

        JButton plus1000Button = StyledButton.create("+1000", fontSize, smallButtonWidth, smallButtonHeight);
        plus1000Button.setBounds(
                startX + (smallButtonWidth + spacing) * 3 + priceFieldWidth + spacing + smallButtonWidth + spacing, 0,
                smallButtonWidth, smallButtonHeight);
        plus1000Button.addActionListener(e -> adjustPrice(1000));
        pricePanel.add(plus1000Button);

        JButton plus10000Button = StyledButton.create("+10000", fontSize, smallButtonWidth, smallButtonHeight);
        plus10000Button.setBounds(startX + (smallButtonWidth + spacing) * 3 + priceFieldWidth + spacing
                + (smallButtonWidth + spacing) * 2, 0, smallButtonWidth, smallButtonHeight);
        plus10000Button.addActionListener(e -> adjustPrice(10000));
        pricePanel.add(plus10000Button);

        currentY += fieldHeight + buttonGap;

        acceptButton = StyledButton.create("Accept", 16, buttonWidth, buttonHeight);
        acceptButton.setBounds(centerX - tradingWidth / 2, currentY, buttonWidth, buttonHeight);
        acceptButton.setVisible(false);
        acceptButton.setEnabled(true);
        acceptButton.addActionListener(e -> acceptOffer());
        add(acceptButton);
        counterOfferButton = StyledButton.create("Counter", 16, buttonWidth, buttonHeight);
        counterOfferButton.setBounds(centerX + (buttonGap / 2), currentY, buttonWidth, buttonHeight);
        counterOfferButton.setVisible(false);
        counterOfferButton.setEnabled(true);
        counterOfferButton.addActionListener(e -> counterOffer());
        add(counterOfferButton);
        currentY += buttonHeight + buttonGap;

        declineButton = StyledButton.create("Decline", 20, tradingWidth, buttonHeight);
        declineButton.setBackground(Color.RED.darker());
        declineButton.setBounds(centerX - tradingWidth / 2, currentY, tradingWidth, buttonHeight);
        declineButton.setVisible(false);
        declineButton.setEnabled(true);
        declineButton.addActionListener(e -> declineOffer());
        add(declineButton);
        currentY += buttonHeight + buttonGap;

        closeButton = StyledButton.create("Close", 20, tradingWidth, buttonHeight);
        closeButton.setBackground(Color.GRAY.darker());
        closeButton.setBounds(centerX - tradingWidth / 2, currentY, tradingWidth, buttonHeight);
        closeButton.setVisible(false);
        closeButton.setEnabled(true);
        closeButton.addActionListener(e -> closeTradingSession());
        add(closeButton);

        setComponentZOrder(desktopPane, getComponentCount() - 1);
        if (transactionCompleted) {
            sellButton.setVisible(false);
            closeButton.setVisible(true);
        } else {
            sellButton.setVisible(true);
            closeButton.setVisible(false);
        }
        if (negotiationPhase) {
            if (acceptButton != null)
                acceptButton.setVisible(true);
            if (counterOfferButton != null)
                counterOfferButton.setVisible(true);
            if (declineButton != null)
                declineButton.setVisible(true);
            if (pricePanel != null)
                pricePanel.setVisible(true);
        } else {
            if (acceptButton != null)
                acceptButton.setVisible(false);
            if (counterOfferButton != null)
                counterOfferButton.setVisible(false);
            if (declineButton != null)
                declineButton.setVisible(false);
            if (pricePanel != null)
                pricePanel.setVisible(false);
        }
        revalidate();
        repaint();
        System.out.println("DEBUG: All trading buttons and fields created");
    }

    private void removeAllTradingButtons() {
        if (sellButton != null)
            remove(sellButton);
        if (acceptButton != null)
            remove(acceptButton);
        if (counterOfferButton != null)
            remove(counterOfferButton);
        if (declineButton != null)
            remove(declineButton);
        if (closeButton != null)
            remove(closeButton);
        if (pricePanel != null)
            remove(pricePanel);
    }

    private void startSelling() {
        System.out.println("DEBUG: startSelling() called");
        if (transactionCompleted) {
            currentMessage = "Transaksi sudah selesai. Mulai trading baru untuk menjual lagi.";
            repaint();
            return;
        }
        Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
        if (barangDiGerobak.isEmpty()) {
            currentMessage = "Kamu tidak membawa barang untuk dijual! Pergi ke home base untuk mengisi gerobak. Tekan E untuk keluar.";
            if (sellButton != null) {
                sellButton.setVisible(false);
                System.out.println("DEBUG: sellButton hidden (no items in cart)");
            }
            repaint();
            return;
        }
        selectedBarang = null;

        System.out.println("[ITEM SELECTION DEBUG] === Simple item selection process ===");
        System.out.println("[ITEM SELECTION DEBUG] Items in gerobak: " + barangDiGerobak.size());

        for (Map.Entry<Barang, Integer> entry : barangDiGerobak.entrySet()) {
            Barang barang = entry.getKey();
            int hargaJual = currentPlayer.getInventory().getHargaJual(barang);
            System.out.println("[ITEM SELECTION DEBUG] Checking item: " + barang.getNamaBarang() +
                    " (freshness: " + barang.getKesegaran() +
                    ", sell price: " + hargaJual + ")");
            
            if (hargaJual > 0) {
                selectedBarang = barang;
                System.out.println("[ITEM SELECTION DEBUG] ✅ Selected: " + barang.getNamaBarang() + 
                        " (first item with sell price)");
                break; 
            } else {
                System.out.println("[ITEM SELECTION DEBUG] ❌ No sell price set - skipping");
            }
        }

        if (selectedBarang != null) {
            System.out.println("[ITEM SELECTION DEBUG] === SELECTION SUCCESSFUL ===");
            System.out.println("[ITEM SELECTION DEBUG] Selected item: " + selectedBarang.getNamaBarang() +
                    " (freshness: " + selectedBarang.getKesegaran() + ")");
        } else {
            System.out.println("[ITEM SELECTION DEBUG] === NO ITEM SELECTED ===");
            System.out.println("[ITEM SELECTION DEBUG] No items have sell prices set");
        }
        if (selectedBarang == null) {
            currentMessage = "Tidak ada barang dengan harga jual yang ditetapkan! Pergi ke home base untuk menetapkan harga jual barang. Tekan E untuk keluar.";
            if (sellButton != null) {
                sellButton.setVisible(false);
                System.out.println("DEBUG: sellButton hidden (no priced items)");
            }
            repaint();
            return;
        }
        int stokBarang = barangDiGerobak.get(selectedBarang);
        if (stokBarang > 1) {
            selectedQuantity = 1 + (int) (Math.random() * stokBarang);
        } else {
            selectedQuantity = 1;
        }
        if (pricePanel != null)
            pricePanel.setVisible(true);
        System.out.println("DEBUG: Valid item found: " + selectedBarang.getNamaBarang());
        int baseUnitPrice = currentPlayer.getInventory().getHargaJual(selectedBarang);
        int adjustedUnitPrice = (int) (baseUnitPrice);
        int totalPrice = adjustedUnitPrice * selectedQuantity;
        if (currentPembeli instanceof model.PembeliMiskin) {
            if (Math.random() < 0.3) {
                currentMessage = "Pembeli miskin menolak membeli barangmu.";
                transactionCompleted = true;
                if (sellButton != null)
                    sellButton.setVisible(false);
                if (closeButton != null)
                    closeButton.setVisible(true);
                repaint();
                return;
            }
        }
        double freshnessPenalty = calculateFreshnessPriceMultiplier(selectedBarang.getKesegaran());
        int freshnessAdjustedPrice = (int) (adjustedUnitPrice * freshnessPenalty);
        

        if (currentPembeli instanceof model.PembeliTajir) {
            model.PembeliTajir pembeliTajir = (model.PembeliTajir) currentPembeli;
            if (pembeliTajir.willAcceptDirectly(freshnessAdjustedPrice)) {

                int finalUnitPrice = freshnessAdjustedPrice;
                int finalTotalPrice = finalUnitPrice * selectedQuantity;
                
                if (itemEffectManager != null) {
                    finalTotalPrice = itemEffectManager.applySemproten(finalTotalPrice);
                    finalTotalPrice = itemEffectManager.applyJampi(finalTotalPrice);
                    finalTotalPrice = itemEffectManager.applyTip(finalTotalPrice);
                }
                
                currentPlayer.tambahMoney(finalTotalPrice);
                Map<Barang, Integer> playerGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
                int currentStock = playerGerobak.getOrDefault(selectedBarang, 0);
                if (currentStock > selectedQuantity) {
                    playerGerobak.put(selectedBarang, currentStock - selectedQuantity);
                } else {
                    playerGerobak.remove(selectedBarang);
                    currentPlayer.getInventory().setHargaJual(selectedBarang, 0);
                }
                
                currentMessage = String.format("Pembeli Tajir langsung menerima tawaranmu! Kamu menjual %s x%d seharga %d per unit (Total: %d koin).",
                        selectedBarang.getNamaBarang(), selectedQuantity, finalUnitPrice, finalTotalPrice);
                        
                deactivateConsumableItems();

                if (perksManagement != null) {
                    boolean perkReceived = perksManagement.tryGiveRandomPerk(currentPlayer);
                    if (perkReceived) {
                        currentMessage += "\n\n🎉 Keberuntungan! Kamu mendapat perk spesial!";
                    }
                }
                
                transactionCompleted = true;
                if (sellButton != null)
                    sellButton.setVisible(false);
                if (closeButton != null)
                    closeButton.setVisible(true);
                repaint();
                return;
            }
        }
        
        int offerUnitPrice = currentPembeli.tawarHarga(freshnessAdjustedPrice);
        int supplierUnitCost = selectedBarang.getHargaBeli();
        offerUnitPrice = Math.max(offerUnitPrice, supplierUnitCost);
        offerPrice = offerUnitPrice;
        currentMessage = String.format(
                "Pembeli tertarik dengan %s x%d (Harga Satuan: %d, Total: %d).\nMereka menawar: %d per unit (Total: %d)",
                selectedBarang.getNamaBarang(), selectedQuantity, adjustedUnitPrice, totalPrice, offerUnitPrice,
                offerUnitPrice * selectedQuantity);
        priceField.setText(String.valueOf(adjustedUnitPrice));
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
        if (acceptButton != null && !isAncestorOf(acceptButton))
            add(acceptButton);
        if (counterOfferButton != null && !isAncestorOf(counterOfferButton))
            add(counterOfferButton);
        if (declineButton != null && !isAncestorOf(declineButton))
            add(declineButton);
        if (pricePanel != null && !isAncestorOf(pricePanel))
            add(pricePanel);
        if (acceptButton != null)
            acceptButton.setVisible(true);
        if (counterOfferButton != null)
            counterOfferButton.setVisible(true);
        if (declineButton != null)
            declineButton.setVisible(true);
        if (pricePanel != null)
            pricePanel.setVisible(true);
        if (acceptButton != null)
            setComponentZOrder(acceptButton, 0);
        if (counterOfferButton != null)
            setComponentZOrder(counterOfferButton, 0);
        if (declineButton != null)
            setComponentZOrder(declineButton, 0);
        if (pricePanel != null)
            setComponentZOrder(pricePanel, 0);
        revalidate();
        repaint();
        System.out.println("DEBUG: repaint called");
    }

    private void adjustPrice(int adjustment) {
        try {
            int currentPrice = 0;
            String currentText = priceField.getText().trim();

            if (!currentText.isEmpty()) {
                currentPrice = Integer.parseInt(currentText);
            }
            int newPrice = currentPrice + adjustment;

            int minPrice = 0;
            int maxPrice = Integer.MAX_VALUE;

            if (selectedBarang != null) {
                minPrice = selectedBarang.getHargaBeli();
                int hargaJual = currentPlayer.getInventory().getHargaJual(selectedBarang);
                if (hargaJual > 0) {
                    maxPrice = hargaJual;
                }
            }

            newPrice = Math.max(minPrice, Math.min(maxPrice, newPrice));

            priceField.setText(String.valueOf(newPrice));

        } catch (NumberFormatException e) {
            int minPrice = 0;
            if (selectedBarang != null) {
                minPrice = selectedBarang.getHargaBeli();
            }
            int newPrice = Math.max(minPrice, adjustment);
            priceField.setText(String.valueOf(newPrice));
        }
    }

    private void acceptOffer() {
        int finalUnitPrice = offerPrice;
        int finalTotalPrice = finalUnitPrice * selectedQuantity;
        if (itemEffectManager != null) {
            finalTotalPrice = itemEffectManager.applySemproten(finalTotalPrice);
            finalTotalPrice = itemEffectManager.applyJampi(finalTotalPrice);
            finalTotalPrice = itemEffectManager.applyTip(finalTotalPrice);
        }
        currentPlayer.tambahMoney(finalTotalPrice);
        Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
        int jumlahSekarang = barangDiGerobak.getOrDefault(selectedBarang, 0);
        if (jumlahSekarang > selectedQuantity) {
            barangDiGerobak.put(selectedBarang, jumlahSekarang - selectedQuantity);
        } else {
            barangDiGerobak.remove(selectedBarang);
            currentPlayer.getInventory().setHargaJual(selectedBarang, 0);
        }
        currentMessage = String.format("Transaksi berhasil! Kamu menjual %s x%d seharga %d per unit (Total: %d koin).",
                selectedBarang.getNamaBarang(), selectedQuantity, finalUnitPrice, finalTotalPrice);
        deactivateConsumableItems();

        if (perksManagement != null) {
            boolean perkReceived = perksManagement.tryGiveRandomPerk(currentPlayer);
            if (perkReceived) {
                currentMessage += "\n\n🎉 Keberuntungan! Kamu mendapat perk spesial!";
            }
        }
        
        transactionCompleted = true;
        hideNegotiationButtons();
        closeButton.setVisible(true);
        sellButton.setVisible(false);

        repaint();
    }

    private void counterOffer() {
        int counterUnitPrice = 0;
        int supplierUnitCost = 0;
        try {
            counterUnitPrice = Integer.parseInt(priceField.getText());
            supplierUnitCost = selectedBarang.getHargaBeli();
            if (counterUnitPrice < supplierUnitCost) {
                currentMessage = String.format(
                        "Harga counter terlalu rendah! Minimal harga satuan: %d (harga beli: %d)",
                        supplierUnitCost, selectedBarang.getHargaBeli());
                if (acceptButton != null)
                    acceptButton.setEnabled(true);
                if (counterOfferButton != null)
                    counterOfferButton.setEnabled(true);
                if (declineButton != null)
                    declineButton.setEnabled(true);
                repaint();
                return;
            }

            boolean accepted = currentPembeli.putuskanTransaksi(counterUnitPrice);
            if (!accepted && currentPembeli.chanceAcceptCounter(counterUnitPrice, offerPrice)) {
                accepted = true;
            }
            if (accepted) {
                int counterTotalPrice = counterUnitPrice * selectedQuantity;
                if (itemEffectManager != null) {
                    counterTotalPrice = itemEffectManager.applySemproten(counterTotalPrice);
                    counterTotalPrice = itemEffectManager.applyJampi(counterTotalPrice);
                    counterTotalPrice = itemEffectManager.applyTip(counterTotalPrice);
                }
                currentPlayer.tambahMoney(counterTotalPrice);
                Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
                int jumlahSekarang = barangDiGerobak.getOrDefault(selectedBarang, 0);
                if (jumlahSekarang > selectedQuantity) {
                    barangDiGerobak.put(selectedBarang, jumlahSekarang - selectedQuantity);
                } else {
                    barangDiGerobak.remove(selectedBarang);
                    currentPlayer.getInventory().setHargaJual(selectedBarang, 0);
                }                
                currentMessage = String.format(
                        "Counter offer diterima! Kamu menjual %s x%d seharga %d per unit (Total: %d koin).",
                        selectedBarang.getNamaBarang(), selectedQuantity, counterUnitPrice, counterTotalPrice);
                
                if (perksManagement != null) {
                    boolean perkReceived = perksManagement.tryGiveRandomPerk(currentPlayer);
                    if (perkReceived) {
                        currentMessage += "\n\n🎉 Keberuntungan! Kamu mendapat perk spesial!";
                    }
                }
                
                deactivateConsumableItems();
                transactionCompleted = true;
                hideNegotiationButtons();
                if (closeButton != null)
                    closeButton.setVisible(true);
                if (sellButton != null)
                    sellButton.setVisible(false);
            } else {
                String reason = null;
                if (currentPembeli instanceof model.PembeliStandar) {
                    reason = ((model.PembeliStandar) currentPembeli).getLastRejectionReason();
                } else if (currentPembeli instanceof model.PembeliTajir) {
                    reason = ((model.PembeliTajir) currentPembeli).getLastRejectionReason();
                } else if (currentPembeli instanceof model.PembeliMiskin) {
                    reason = ((model.PembeliMiskin) currentPembeli).getLastRejectionReason();
                }
                if (reason != null && !reason.isEmpty()) {
                    currentMessage = reason;
                    transactionCompleted = true;
                    hideNegotiationButtons();
                    if (closeButton != null)
                        closeButton.setVisible(true);
                    if (sellButton != null)
                        sellButton.setVisible(false);
                } else {
                    int newOfferUnitPrice = currentPembeli.tawarHarga(counterUnitPrice);
                    if (newOfferUnitPrice != counterUnitPrice) {
                        offerPrice = Math.max(newOfferUnitPrice, supplierUnitCost);
                        currentMessage = String.format(
                                "Pembeli menolak offer-mu dan memberikan counter: %d per unit (Total: %d)", offerPrice,
                                offerPrice * selectedQuantity);
                        priceField.setText(String.valueOf(offerPrice));
                        if (acceptButton != null) {
                            acceptButton.setVisible(true);
                            acceptButton.setEnabled(true);
                        }
                        if (counterOfferButton != null) {
                            counterOfferButton.setVisible(true);
                            counterOfferButton.setEnabled(true);
                        }
                        if (declineButton != null) {
                            declineButton.setVisible(true);
                            declineButton.setEnabled(true);
                        }
                        if (pricePanel != null)
                            pricePanel.setVisible(true);
                        negotiationPhase = true;
                        repaint();
                        return;
                    } else {
                        currentMessage = "Pembeli menolak counter offer mu dan mengakhiri negosiasi.";
                        transactionCompleted = true;
                        hideNegotiationButtons();
                        if (closeButton != null)
                            closeButton.setVisible(true);
                        if (sellButton != null)
                            sellButton.setVisible(false);
                    }
                }
            }
        } catch (NumberFormatException e) {
            currentMessage = "Masukkan harga satuan yang valid!";
            if (acceptButton != null)
                acceptButton.setEnabled(true);
            if (counterOfferButton != null)
                counterOfferButton.setEnabled(true);
            if (declineButton != null)
                declineButton.setEnabled(true);
            repaint();
            return;
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
        transactionCompleted = true;
        hideNegotiationButtons();

        closeButton.setVisible(true);
        sellButton.setVisible(false);

        repaint();
    }

    private void hideNegotiationButtons() {
        acceptButton.setVisible(false);
        counterOfferButton.setVisible(false);
        declineButton.setVisible(false);
        negotiationPhase = false;
    }

    private void closeTradingSession() {
        removeAllTradingButtons();

        hideDialog();

        if (currentTriggerZoneId != null) {
            System.out.println("DEBUG: Attempting to remove zone: " + currentTriggerZoneId);
            System.out.println("DEBUG: randomTriggerZoneManager != null: " + (randomTriggerZoneManager != null));
            System.out.println("DEBUG: triggerZoneManager != null: " + (triggerZoneManager != null));

            if (randomTriggerZoneManager != null && triggerZoneManager != null) {
                boolean removed = randomTriggerZoneManager.removeZoneById(currentTriggerZoneId, triggerZoneManager);
                System.out.println("DEBUG: Zone removal result: " + removed + " for zone: " + currentTriggerZoneId);
            } else if (triggerZoneManager != null) {
                triggerZoneManager.removeZoneById(currentTriggerZoneId);
                System.out.println("DEBUG: Removed trigger zone directly: " + currentTriggerZoneId);
            } else {
                System.out.println("DEBUG: No trigger zone manager available for removal");
            }
            currentTriggerZoneId = null;
        } else {
            System.out.println("DEBUG: No currentTriggerZoneId set for removal");
        }

        if (gerobakFrame != null && gerobakFrame.isVisible()) {
            gerobakFrame.dispose();
        }

        if (parentPanel != null && this.getParent() == parentPanel) {
            parentPanel.remove(this);
            parentPanel.revalidate();
            parentPanel.repaint();
            parentPanel.requestFocusInWindow();
        }

        transactionCompleted = false;
        negotiationPhase = false;
        currentPembeli = null;
        selectedBarang = null;
        offerPrice = 0;
        selectedQuantity = 1;

        System.out.println("DEBUG: Trading session closed and trigger zone removal completed");
    }

    private void deactivateConsumableItems() {
        if (itemEffectManager != null) {
            itemEffectManager.deactivateConsumableItems();
        }
    }

    private boolean hasItemsWithSellingPrice() {
        if (currentPlayer == null || currentPlayer.getInventory() == null) {
            return false;
        }

        Map<Barang, Integer> barangDiGerobak = currentPlayer.getInventory().getBarangDibawaMutable();
        if (barangDiGerobak.isEmpty()) {
            return false;
        }

        for (Barang barang : barangDiGerobak.keySet()) {
            int hargaJual = currentPlayer.getInventory().getHargaJual(barang);
            if (hargaJual > 0) {
                return true;
            }
        }

        return false;
    }

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

    private void showGerobakFrame() {
        if (gerobakFrame != null && gerobakFrame.isVisible()) {
            gerobakFrame.toFront();
            return;
        }
        gerobakFrame = new JInternalFrame("Gerobak - Barang Sudah Ada Harga Jual", true, true, true, true);
        gerobakFrame.setSize(600, 350);
        gerobakFrame.setLayout(new BorderLayout());
        gerobakFrame.getContentPane().setBackground(DIALOG_BG);

        gerobakWithPriceTable = new JTable();
        JScrollPane scroll = new JScrollPane(gerobakWithPriceTable);
        scroll.getViewport().setBackground(new Color(255, 255, 240));
        scroll.setBorder(BorderFactory.createTitledBorder("Barang Sudah Ada Harga Jual"));
        gerobakFrame.add(scroll, BorderLayout.CENTER);

        JButton closeBtn = StyledButton.create("Tutup", 14, 100, 32);
        closeBtn.addActionListener(e -> gerobakFrame.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(closeBtn);
        gerobakFrame.add(btnPanel, BorderLayout.SOUTH);

        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null) {
            JLayeredPane layered = root.getLayeredPane();
            layered.add(gerobakFrame, JLayeredPane.POPUP_LAYER);
            gerobakFrame.setLocation((layered.getWidth() - gerobakFrame.getWidth()) / 2,
                    (layered.getHeight() - gerobakFrame.getHeight()) / 2);
            gerobakFrame.setVisible(true);
            gerobakFrame.toFront();
            updateGerobakTablesLocal();
        } else {
            System.err.println("[ERROR] Tidak dapat menemukan root pane untuk Gerobak frame");
        }
    }

    private void updateGerobakTablesLocal() {
        if (currentPlayer == null || currentPlayer.getInventory() == null)
            return;
        Map<Barang, Integer> map = currentPlayer.getInventory().getBarangDibawaMutable();
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[] { "Icon", "Nama", "Kategori", "Kesegaran", "Jumlah", "Harga Jual" }, 0);
        for (Map.Entry<Barang, Integer> entry : map.entrySet()) {
            Barang b = entry.getKey();
            int count = entry.getValue();
            int harga = currentPlayer.getInventory().getHargaJual(b);
            if (harga > 0) {
                ImageIcon icon = GamePanel.getIcon(b.getIconPath(), 32, 32);
                if (icon == null)
                    icon = GamePanel.getIcon(b.getNamaBarang().toLowerCase().replace(' ', '_'), 32, 32);
                model.addRow(new Object[] { icon, b.getNamaBarang(), b.getKategori(), b.getKesegaran(), count,
                        harga + "G" });
            }
        }
        gerobakWithPriceTable.setModel(model);
        gerobakWithPriceTable.setRowHeight(36);
        gerobakWithPriceTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        gerobakWithPriceTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        gerobakWithPriceTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        gerobakWithPriceTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        gerobakWithPriceTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        gerobakWithPriceTable.getColumnModel().getColumn(5).setPreferredWidth(80);
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

    private void showItemFrame() {
        if (itemFrame != null && itemFrame.isVisible()) {
            itemFrame.toFront();
            return;
        }
        itemFrame = new JInternalFrame("Use Item - Item di Gerobak", true, true, true, true);
        itemFrame.setSize(600, 350);
        itemFrame.setLayout(new BorderLayout());
        itemFrame.getContentPane().setBackground(DIALOG_BG);

        itemGerobakTable = new JTable();
        JScrollPane scroll = new JScrollPane(itemGerobakTable);
        scroll.getViewport().setBackground(new Color(255, 255, 240));
        scroll.setBorder(BorderFactory.createTitledBorder("Item di Gerobak"));
        itemFrame.add(scroll, BorderLayout.CENTER);

        JButton useBtn = StyledButton.create("Use", 14, 100, 32);
        JButton closeBtn = StyledButton.create("Tutup", 14, 100, 32);
        useBtn.addActionListener(e -> useSelectedItem());
        closeBtn.addActionListener(e -> itemFrame.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(useBtn);
        btnPanel.add(closeBtn);
        itemFrame.add(btnPanel, BorderLayout.SOUTH);

        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null) {
            JLayeredPane layered = root.getLayeredPane();
            layered.add(itemFrame, JLayeredPane.POPUP_LAYER);
            itemFrame.setLocation((layered.getWidth() - itemFrame.getWidth()) / 2,
                    (layered.getHeight() - itemFrame.getHeight()) / 2);
            itemFrame.setVisible(true);
            itemFrame.toFront();
            updateItemGerobakTableLocal();
        } else {
            System.err.println("[ERROR] Tidak dapat menemukan root pane untuk Item frame");
        }
    }

    private void updateItemGerobakTableLocal() {
        if (currentPlayer == null || currentPlayer.getInventory() == null)
            return;
        java.util.List<Item> itemsDiGerobak = currentPlayer.getInventory().getItemDibawa();
        String[] cols = { "Icon", "Nama", "Level", "Chance", "Status", "Deskripsi" };
        Object[][] data = new Object[itemsDiGerobak.size()][cols.length];
        for (int i = 0; i < itemsDiGerobak.size(); i++) {
            Item item = itemsDiGerobak.get(i);
            ImageIcon icon = GamePanel.getIcon(item.getIconPath(), 32, 32);
            if (icon == null)
                icon = GamePanel.getIcon(item.getNama().toLowerCase().replace(' ', '_'), 32, 32);
            data[i][0] = icon;
            data[i][1] = item.getNama();
            data[i][2] = "Level " + item.getLevel();
            data[i][3] = getItemEffectPercentage(item);
            data[i][4] = item.isActive() ? "Aktif" : "Non-aktif";
            data[i][5] = getItemEffectDescription(item);
        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, cols) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        itemGerobakTable.setModel(model);
        itemGerobakTable.setRowHeight(36);
        itemGerobakTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        itemGerobakTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        itemGerobakTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        itemGerobakTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        itemGerobakTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        itemGerobakTable.getColumnModel().getColumn(5).setPreferredWidth(240);
        itemGerobakTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 5; i++) {
            itemGerobakTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        itemGerobakTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);
    }

    private String getItemEffectPercentage(Item item) {
        if (item.isHipnotis()) {
            return String.format("%.0f%%", item.getHipnotisChance() * 100);
        } else if (item.isJampi()) {
            return String.format("%.1fx", item.getJampiMultiplier());
        } else if (item.isSemproten()) {
            return String.format("+%.0f%%", item.getSemprotenPriceBoost() * 100);
        } else if (item.isTip()) {
            return String.format("%.0f%%", item.getTipBonusRate() * 100);
        } else if (item.isPeluit()) {
            return String.format("+%d", item.getPeluitExtraBuyers());
        }
        return "N/A";
    }

    private String getItemEffectDescription(Item item) {
        if (item.isHipnotis()) {
            return "Efek: Meningkatkan peluang pembeli langsung membeli tanpa menawar (" +
                    String.format("%.0f%% chance langsung beli", item.getHipnotisChance() * 100) + ")";
        } else if (item.isJampi()) {
            return "Efek: Melipatgandakan penghasilan dari transaksi hari ini (" +
                    String.format("%.1fx multiplier penghasilan", item.getJampiMultiplier()) + ")";
        } else if (item.isSemproten()) {
            return "Efek: Menambah kesan barang lebih fresh, harga bisa ditawar lebih mahal (" +
                    String.format("+%.0f%% harga jual", item.getSemprotenPriceBoost() * 100) + ")";
        } else if (item.isTip()) {
            return "Efek: Pembeli kadang memberi uang ekstra (" +
                    String.format("%.0f%% chance bonus tip", item.getTipBonusRate() * 100) + ")";
        } else if (item.isPeluit()) {
            return "Efek: Memanggil pembeli tambahan secara instan (" +
                    String.format("+%d pembeli tambahan", item.getPeluitExtraBuyers()) + ")";
        }
        return "Efek tidak diketahui";
    }

    private void useSelectedItem() {
        int row = itemGerobakTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String namaItem = itemGerobakTable.getValueAt(row, 1).toString();
        if (namaItem.equalsIgnoreCase("Hipnotis") || namaItem.equalsIgnoreCase("Semproten")) {
            if (!negotiationPhase) {
                if (itemEffectManager != null && itemEffectManager.activateItemForTransaction(namaItem)) {
                    String detailedMessage = itemEffectManager.getItemActivationMessage(namaItem);
                    JOptionPane.showMessageDialog(this, detailedMessage, "Item Berhasil Diaktifkan",
                            JOptionPane.INFORMATION_MESSAGE);
                    updateItemGerobakTableLocal();
                } else {
                    JOptionPane.showMessageDialog(this, "Item sudah aktif atau tidak bisa digunakan sekarang!", "Info",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Item ini hanya bisa digunakan sebelum Start Selling!", "Info",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else if (namaItem.equalsIgnoreCase("Jampi")) {
            String detailedMessage = itemEffectManager.getItemActivationMessage(namaItem);
            JOptionPane.showMessageDialog(this, detailedMessage, "Info Jampi",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (namaItem.equalsIgnoreCase("Tip")) {
            String detailedMessage = itemEffectManager.getItemActivationMessage(namaItem);
            JOptionPane.showMessageDialog(this, detailedMessage, "Info Tip",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (namaItem.equalsIgnoreCase("Peluit")) {
            String detailedMessage = itemEffectManager.getItemActivationMessage(namaItem);
            JOptionPane.showMessageDialog(this, detailedMessage, "Info Peluit",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Item ini tidak dikenali!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateFreshnessMultiplier(int kesegaran) {
        if (kesegaran >= 76)
            return 1.0;
        if (kesegaran >= 51)
            return 0.8;
        if (kesegaran >= 26)
            return 0.6;
        if (kesegaran >= 11)
            return 0.45; 
        if (kesegaran >= 1)
            return 0.35;  
        return 0.15;      
    }

    private double calculateEnhancedFreshnessMultiplier(int kesegaran) {
        double baseFreshnessMultiplier = calculateFreshnessMultiplier(kesegaran);

        if (itemEffectManager != null) {
            Item semproten = getActiveSemprotenItem();
            if (semproten != null) {
                double semprotenBonus = 0.15 + (semproten.getLevel() * 0.05);
                double enhancedMultiplier = baseFreshnessMultiplier * (1.0 + semprotenBonus);

                System.out.println("[SEMPROTEN INTEREST EFFECT] Base interest: " +
                        String.format("%.2f", baseFreshnessMultiplier) +
                        ", Semproten Level " + semproten.getLevel() + " bonus: +" +
                        String.format("%.0f", semprotenBonus * 100) + "%" +
                        ", Enhanced interest: " + String.format("%.2f", enhancedMultiplier));

                return Math.min(enhancedMultiplier, 1.5);
            }
        }

        return baseFreshnessMultiplier;
    }

    private Item getActiveSemprotenItem() {
        if (currentPlayer == null || currentPlayer.getInventory() == null) {
            return null;
        }

        for (Item item : currentPlayer.getInventory().getStokItem()) {
            if (item.isSemproten() && item.isActive()) {
                return item;
            }
        }
        return null;
    }

    private double calculateFreshnessPriceMultiplier(int kesegaran) {
        if (kesegaran >= 76)
            return 1.0;
        if (kesegaran >= 51)
            return 0.8;
        if (kesegaran >= 26)
            return 0.6;
        if (kesegaran >= 1)
            return 0.3;
        return 0.1;
    }    
    public void setRandomTriggerZoneManager(RandomTriggerZoneManager mgr) {
        this.randomTriggerZoneManager = mgr;
    }

    public void setPerksManagement(PerksManagement perksManagement) {
        this.perksManagement = perksManagement;
    }
}