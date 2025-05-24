package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import model.Barang;
import model.Inventory;

public class HomeBasePanel extends JPanel {
    private JButton btn1, btn2, btn3, btn4, btn5;
    private Runnable backToGameCallback;
    private JButton backButton;
    private Inventory inventory;
    private JDesktopPane desktopPane; // For internal frames
    private JInternalFrame inventoryFrame; // Replaces gerobakDialog
    private JTabbedPane tabbedPane;
    private JTable goodsTable;
    private Image bgImage;
    private Image tetoImage;

    public HomeBasePanel() {
        setLayout(null);

        // new Color(245, 222, 179)

        // JPanel upperPanel = new JPanel();
        // upperPanel.setBackground(new Color(245, 222, 179));
        // upperPanel.setBounds(0, 0, getWidth(), 50); // Adjust height as needed
        // upperPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // add(upperPanel);

        // // Create bottom panel
        // JPanel bottomPanel = new JPanel();
        // bottomPanel.setBackground(new Color(245, 222, 179));
        // bottomPanel.setBounds(0, getHeight() - 50, getWidth(), 50); // Adjust height
        // as needed
        // bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // add(bottomPanel);

        Font customFont;
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/medieval.ttf"));

        } catch (FontFormatException | IOException e) {
            customFont = new Font("Serif", Font.BOLD, 24);
        }

        JLabel titleLabel = new JLabel("Home Base");
        titleLabel.setFont(customFont.deriveFont(80f));
        titleLabel.setBounds(20, 15, 100000, 200);

        add(titleLabel);

        // Inisialisasi inventory (sementara, nanti bisa di-set dari luar)
        btn1 = StyledButton.create("Inventory");
        btn2 = StyledButton.create("Supplier");
        btn3 = StyledButton.create("Perks");
        btn4 = StyledButton.create("Settings");
        btn5 = StyledButton.create("Gerobak");
        add(btn1);
        add(btn2);
        add(btn3);
        add(btn4);
        add(btn5);

        backButton = StyledButton.create("Kembali", 20, 120, 40);
        backButton.addActionListener(e -> {
            if (backToGameCallback != null)
                backToGameCallback.run();
        });
        add(backButton);

        // Add a JDesktopPane to host internal frames
        desktopPane = new JDesktopPane();
        desktopPane.setOpaque(false); // Let background show through
        add(desktopPane);
        setComponentZOrder(desktopPane, 0); // Ensure desktopPane is always on top

        btn1.addActionListener(e -> showInventoryFrame());
        btn2.addActionListener(e -> {

        });
        btn3.addActionListener(e -> {
        });
        btn4.addActionListener(e -> {
        });
        btn5.addActionListener(e -> {
        });

        try {
            bgImage = ImageIO.read(new File("assets/backgrounds/HomeBase.png"));
        } catch (IOException e) {
            bgImage = null;
        }
        try {
            tetoImage = ImageIO.read(new File("assets/backgrounds/kasane_teto.png"));
        } catch (IOException e) {
            tetoImage = null;
        }
    }

    private void showInventoryFrame() {
        if (inventoryFrame == null) {
            inventoryFrame = new JInternalFrame("Inventory", true, true, true, true);
            inventoryFrame.setSize(600, 400);
            inventoryFrame.setVisible(true);
            inventoryFrame.setLayout(new BorderLayout());
            // Thematic border: gold with shadow, parchment background
            inventoryFrame.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)), // shadow brown
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 4) // gold
            ));
            inventoryFrame.setOpaque(true);
            inventoryFrame.getContentPane().setBackground(new Color(255, 248, 220)); // parchment

            tabbedPane = new JTabbedPane();
            tabbedPane.setBackground(new Color(255, 248, 220));
            tabbedPane.setForeground(new Color(120, 90, 30));
            tabbedPane.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));

            // Tab Goods
            JPanel goodsPanel = new JPanel(new BorderLayout());
            goodsPanel.setBackground(new Color(255, 248, 220));
            goodsTable = new JTable();
            goodsTable.getTableHeader().setBackground(new Color(212, 175, 55));
            goodsTable.getTableHeader().setForeground(new Color(60, 40, 10));
            goodsTable.setBackground(new Color(255, 255, 240));
            goodsTable.setForeground(new Color(60, 40, 10));
            JScrollPane goodsScroll = new JScrollPane(goodsTable);
            goodsScroll.getViewport().setBackground(new Color(255, 255, 240));
            goodsScroll.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
            // Sort options
            JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            sortPanel.setOpaque(false);
            String[] sortOptions = { "Nama", "Kategori", "Kesegaran", "Harga Beli" };
            JComboBox<String> sortCombo = new JComboBox<>(sortOptions);
            sortPanel.add(new JLabel("Sort by: "));
            sortPanel.add(sortCombo);
            String[] orderOptions = { "Ascending", "Descending" };
            JComboBox<String> orderCombo = new JComboBox<>(orderOptions);
            sortPanel.add(new JLabel("Order: "));
            sortPanel.add(orderCombo);
            goodsPanel.add(sortPanel, BorderLayout.NORTH);
            goodsPanel.add(goodsScroll, BorderLayout.CENTER);
            tabbedPane.addTab("Goods", goodsPanel);
            // Tab Items (placeholder)
            JPanel itemsPanel = new JPanel();
            itemsPanel.setBackground(new Color(255, 248, 220));
            tabbedPane.addTab("Items", itemsPanel);
            // Tab Perks (placeholder)
            JPanel perksPanel = new JPanel();
            perksPanel.setBackground(new Color(255, 248, 220));
            tabbedPane.addTab("Perks", perksPanel);
            inventoryFrame.add(tabbedPane, BorderLayout.CENTER);
            sortCombo.addActionListener(
                    e -> updateGoodsTable(sortCombo.getSelectedIndex(), orderCombo.getSelectedIndex()));
            orderCombo.addActionListener(
                    e -> updateGoodsTable(sortCombo.getSelectedIndex(), orderCombo.getSelectedIndex()));
            inventoryFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
                    inventoryFrame = null;
                }
            });
            desktopPane.add(inventoryFrame);
        }
        updateGoodsTable(0, 0);
        inventoryFrame.setVisible(true);
        inventoryFrame.toFront();
    }

    private void updateGoodsTable(int sortBy, int order) {
        List<Barang> list = inventory.getStokBarang();
        Map<String, Object[]> map = new LinkedHashMap<>();
        for (Barang b : list) {
            String key = b.getNamaBarang() + "|" + b.getKategori() + "|" + b.getHargaBeli() + "|" + b.getKesegaran();
            map.compute(key, (k, v) -> v == null ? new Object[] { b, 1 } : new Object[] { b, (int) v[1] + 1 });
        }
        List<Object[]> rows = new ArrayList<>(map.values());
        rows.sort((a, b) -> {
            Barang ba = (Barang) a[0], bb = (Barang) b[0];
            int cmp = switch (sortBy) {
                case 0 -> ba.getNamaBarang().compareToIgnoreCase(bb.getNamaBarang());
                case 1 -> ba.getKategori().compareToIgnoreCase(bb.getKategori());
                case 2 -> Integer.compare(ba.getKesegaran(), bb.getKesegaran());
                case 3 -> Integer.compare(ba.getHargaBeli(), bb.getHargaBeli());
                default -> 0;
            };
            return order == 1 ? -cmp : cmp;
        });

        System.out.println("Jumlah barang di inventory: " + list.size());

        String[] cols = { "Icon", "Nama", "Kategori", "Kesegaran", "Harga Beli", "Jumlah" };
        Object[][] data = new Object[rows.size()][cols.length];
        for (int i = 0; i < rows.size(); i++) {
            Barang b = (Barang) rows.get(i)[0];
            int jml = (int) rows.get(i)[1];
            // icon
            ImageIcon icon = null;
            try {
                Image img = ImageIO.read(new File(
                        "assets/icons/" + b.getNamaBarang().toLowerCase().replace(' ', '_') + ".png"));
                icon = new ImageIcon(img.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
            } catch (Exception ignored) {
            }
            data[i][0] = icon != null ? new JLabel(icon) : new JLabel();
            data[i][1] = b.getNamaBarang();
            data[i][2] = b.getKategori();
            data[i][3] = b.getKesegaran();
            data[i][4] = b.getHargaBeli();
            data[i][5] = jml;
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? JLabel.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        goodsTable.setModel(model);
        goodsTable.setRowHeight(36);
        goodsTable.getColumnModel().getColumn(0).setPreferredWidth(40);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < goodsTable.getColumnCount(); i++) {
            goodsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void setBackToGameCallback(Runnable cb) {
        this.backToGameCallback = cb;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int panelTop = 150;
        int panelBottom = 100;
        int marginLeft = (int) (getWidth() * 0.02);
        int buttonWidth = (int) (getWidth() * 0.3);
        int areaHeight = getHeight() - panelTop - panelBottom;
        int buttonHeight = (int) (areaHeight * 0.15);
        int numButtons = 5;
        int spacing = (areaHeight - (numButtons * buttonHeight)) / (numButtons + 1);
        int startY = panelTop + spacing;
        JButton[] buttons = { btn1, btn2, btn3, btn4, btn5 };
        for (int i = 0; i < numButtons; i++) {
            int y = startY + i * (buttonHeight + spacing);
            buttons[i].setBounds(marginLeft, y, buttonWidth, buttonHeight);
        }
        // Layout backButton di kanan bawah
        if (backButton != null) {
            backButton.setBounds(getWidth() - 140, getHeight() - 80, 120, 40);
        }
        // Layout desktopPane to fill the panel
        if (desktopPane != null) {
            desktopPane.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        // Draw background image if available
        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        // Draw upper panel
        g2d.setColor(new Color(245, 222, 179));
        g2d.fillRect(0, 0, getWidth(), 150);
        // Draw character image if available
        if (tetoImage != null) {
            int maxW = (int) (getWidth() * 0.35);
            int maxH = (int) (getHeight() - 120);
            int iw = tetoImage.getWidth(this), ih = tetoImage.getHeight(this);
            double scale = Math.max((double) maxW / iw, (double) maxH / ih);
            int w = (int) (iw * scale), h = (int) (ih * scale);
            int x = getWidth() - w;
            int y = 25;
            g2d.drawImage(tetoImage, x, y, w, h, this);
        }
        // Draw bottom panel
        g2d.setColor(new Color(245, 222, 179));
        g2d.fillRect(0, getHeight() - 100, getWidth(), 100);
        g2d.dispose();
        // Let Swing paint children (buttons, desktopPane, etc) above
    }

    // public void paintPanel(Graphics g) {
    // Graphics2D g2d = (Graphics2D) g.create();
    // g2d.setColor(new Color(245, 222, 179));
    // g2d.fillRect(0, 0, getWidth(), 100); // Paint upper panel
    // g2d.fillRect(0, getHeight() - 100, getWidth(), 100); // Paint bottom panel
    // g2d.dispose();
    // }
}
