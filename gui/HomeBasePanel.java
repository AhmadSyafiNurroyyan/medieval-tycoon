package gui;

import interfaces.InventoryChangeListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
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
    private JInternalFrame gerobakFrame;
    private JTabbedPane tabbedPane;
    private JTable goodsTable;
    private JTable gerobakTable;
    private JLabel lblJumlah;
    private JLabel lblGerobakInfo;
    private Image bgImage;
    private Image tetoImage;
    private int currentSortBy = 0;
    private int currentSortOrder = 0;

    public HomeBasePanel() {
        setLayout(null);

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
        btn2 = StyledButton.create("Gerobak");
        btn3 = StyledButton.create("Perks");
        btn4 = StyledButton.create("Settings");
        btn5 = StyledButton.create("aaa");
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

        desktopPane = new JDesktopPane();
        desktopPane.setOpaque(false); 
        add(desktopPane);
        setComponentZOrder(desktopPane, 0);

        btn1.addActionListener(e -> showInventoryFrame());
        btn2.addActionListener(e -> showGerobakFrame());
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
            
            JButton btnHapus = StyledButton.create("Hapus Barang", 13, 123, 32);
            JButton btnMoveToGerobak = StyledButton.create("Move to Gerobak", 13, 160, 32);
            
            lblJumlah = new JLabel("Jumlah barang: " + inventory.getJumlahBarang());
            lblJumlah.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblJumlah.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 13));

            JPanel bawahPanel = new JPanel(new BorderLayout());
            bawahPanel.add(btnHapus, BorderLayout.WEST);
            bawahPanel.add(btnMoveToGerobak, BorderLayout.CENTER);
            bawahPanel.add(lblJumlah, BorderLayout.EAST);
            goodsPanel.add(bawahPanel, BorderLayout.SOUTH);

            btnHapus.addActionListener(e -> {
                int row = goodsTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Pilih barang terlebih dahulu!", "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nama = goodsTable.getValueAt(row, 1).toString();
                Barang b = new Barang(nama);

                if (inventory.hapusBarang(b)) {
                    JOptionPane.showMessageDialog(this, "Barang berhasil dihapus.");
                } else {
                    JOptionPane.showMessageDialog(this, "Barang tidak ditemukan.");
                }

                updateGoodsTable(currentSortBy, currentSortOrder); 
            });

            btnMoveToGerobak.addActionListener(e -> {
                int row = goodsTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Pilih barang terlebih dahulu!", "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nama = goodsTable.getValueAt(row, 1).toString();
                Barang b = new Barang(nama);
                int jumlah = 1;
                int kapasitasGerobak = 20;
                inventory.bawaBarang(b, jumlah, kapasitasGerobak);
                JOptionPane.showMessageDialog(this, "Barang dipindahkan ke Gerobak.");
                updateGoodsTable(currentSortBy, currentSortOrder);
            });
            
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
            inventoryFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    inventoryFrame = null;
                }
            });
            desktopPane.add(inventoryFrame);
        }
        updateGoodsTable(0, 0);
        inventoryFrame.setVisible(true);
        inventoryFrame.toFront();
    }

    private void showGerobakFrame() {
        if (gerobakFrame == null) {
            gerobakFrame = new JInternalFrame("Gerobak", true, true, true, true);
            gerobakFrame.setSize(500, 350);
            gerobakFrame.setLayout(new BorderLayout());
            gerobakFrame.setVisible(true);
            gerobakFrame.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)),
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 4)
            ));
            gerobakFrame.setOpaque(true);
            gerobakFrame.getContentPane().setBackground(new Color(255, 248, 220));

            // Table
            gerobakTable = new JTable();
            gerobakTable.setRowHeight(36);
            gerobakTable.getTableHeader().setBackground(new Color(212, 175, 55));
            gerobakTable.getTableHeader().setForeground(new Color(60, 40, 10));
            gerobakTable.setBackground(new Color(255, 255, 240));
            gerobakTable.setForeground(new Color(60, 40, 10));
            JScrollPane scroll = new JScrollPane(gerobakTable);
            scroll.getViewport().setBackground(new Color(255, 255, 240));
            scroll.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
            gerobakFrame.add(scroll, BorderLayout.CENTER);

            // Info label
            lblGerobakInfo = new JLabel();
            lblGerobakInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblGerobakInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 13));
            gerobakFrame.add(lblGerobakInfo, BorderLayout.SOUTH);

            gerobakFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    gerobakFrame = null;
                }
            });
            desktopPane.add(gerobakFrame);
        }
        updateGerobakTable();
        gerobakFrame.setVisible(true);
        gerobakFrame.toFront();
    }

    private void updateGerobakTable() {
        if (gerobakFrame == null || !gerobakFrame.isVisible() || gerobakTable == null) return;
        String[] cols = {"Icon", "Nama", "Kategori", "Kesegaran", "Harga Beli", "Jumlah"};
        Map<Barang, Integer> dibawa = inventory.getBarangDibawa();
        Object[][] data = new Object[dibawa.size()][cols.length];
        int i = 0;
        for (Map.Entry<Barang, Integer> entry : dibawa.entrySet()) {
            Barang b = entry.getKey();
            int jml = entry.getValue();
            ImageIcon icon = null;
            try {
                Image img = ImageIO.read(new File(
                        "assets/icons/" + b.getNamaBarang().toLowerCase().replace(' ', '_') + ".png"));
                icon = new ImageIcon(img.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
            } catch (IOException ignored) {}
            data[i][0] = icon;
            data[i][1] = b.getNamaBarang();
            data[i][2] = b.getKategori();
            data[i][3] = b.getKesegaran();
            data[i][4] = b.getHargaBeli();
            data[i][5] = jml;
            i++;
        }
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? Icon.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        gerobakTable.setModel(model);
        gerobakTable.setRowHeight(36);
        gerobakTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        gerobakTable.getColumnModel().getColumn(0).setCellRenderer((_,value,_,_,_,_) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 1; c < gerobakTable.getColumnCount(); c++) {
            gerobakTable.getColumnModel().getColumn(c).setCellRenderer(centerRenderer);
        }
        if (lblGerobakInfo != null) {
            lblGerobakInfo.setText("Total barang di Gerobak: " + dibawa.values().stream().mapToInt(Integer::intValue).sum());
        }
    }

    private void updateGoodsTable(int sortBy, int order) {
        if (goodsTable == null) return;
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
            } catch (java.io.IOException ignored) {
            }
            data[i][0] = icon; // hanya ImageIcon, bukan JLabel
            data[i][1] = b.getNamaBarang();
            data[i][2] = b.getKategori();
            data[i][3] = b.getKesegaran();
            data[i][4] = b.getHargaBeli();
            data[i][5] = jml;
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        goodsTable.setModel(model);
        goodsTable.setRowHeight(36);
        goodsTable.getColumnModel().getColumn(0).setPreferredWidth(40);

        // Set renderer kolom icon agar menampilkan ImageIcon
        goodsTable.getColumnModel().getColumn(0).setCellRenderer((_,value,_,_,_,_) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < goodsTable.getColumnCount(); i++) {
            goodsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        if (lblJumlah != null) {
            lblJumlah.setText("Jumlah barang: " + inventory.getJumlahBarang());
        }
    }

    public void setBackToGameCallback(Runnable cb) {
        this.backToGameCallback = cb;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        if (inventory != null) {
            inventory.addInventoryChangeListener(new InventoryChangeListener() {
                @Override
                public void onInventoryChanged() {
                    refreshInventoryAndGerobak();
                }
            });
        }
    }

    public void refreshInventoryAndGerobak() {
        updateGoodsTable(currentSortBy, currentSortOrder);
        updateGerobakTable();
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
