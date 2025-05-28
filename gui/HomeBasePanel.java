package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private JButton btn1, btn2, btn3, btn4, btn5, backButton;
    private Runnable backToGameCallback;
    private Inventory inventory;
    private JDesktopPane desktopPane;
    private JInternalFrame inventoryFrame, gerobakFrame;
    private JTable goodsTable, gerobakNoPriceTable, gerobakWithPriceTable;
    private JLabel lblJumlah, lblGerobakInfo;
    private Image bgImage, tetoImage;
    private final int currentSortBy = 0, currentSortOrder = 0;
    private JTextField jumlahField, hargaField;
    private JTabbedPane tabbedPane;public HomeBasePanel() {
        setLayout(null);
        initializeComponents();
        loadImages();
    }

    private void initializeComponents() {
        // Title
        JLabel titleLabel = new JLabel("Home Base");
        titleLabel.setFont(loadCustomFont().deriveFont(80f));
        titleLabel.setBounds(20, 15, 100000, 200);
        add(titleLabel);

        // Buttons
        btn1 = StyledButton.create("Inventory");
        btn2 = StyledButton.create("Gerobak");
        btn3 = StyledButton.create("Perks");
        btn4 = StyledButton.create("Stats");
        btn5 = StyledButton.create("Sleep");
        add(btn1); add(btn2); add(btn3); add(btn4); add(btn5);
        
        backButton = StyledButton.create("Kembali", 20, 120, 40);
        backButton.addActionListener(e -> { if (backToGameCallback != null) backToGameCallback.run(); });
        add(backButton);

        // Desktop pane
        desktopPane = new JDesktopPane();
        desktopPane.setOpaque(false);
        add(desktopPane);
        setComponentZOrder(desktopPane, 0);

        // Action listeners
        btn1.addActionListener(e -> showInventoryFrame());
        btn2.addActionListener(e -> showGerobakFrame());
        btn3.addActionListener(e -> {});
        btn4.addActionListener(e -> {});
        btn5.addActionListener(e -> {});
    }

    private Font loadCustomFont() {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/medieval.ttf"));
        } catch (FontFormatException | IOException e) {
            return new Font("Serif", Font.BOLD, 24);
        }
    }

    private void loadImages() {
        try { bgImage = ImageIO.read(new File("assets/backgrounds/HomeBase.png")); } catch (IOException e) { bgImage = null; }
        try { tetoImage = ImageIO.read(new File("assets/backgrounds/kasane_teto.png")); } catch (IOException e) { tetoImage = null; }
    }

    private void showInventoryFrame() {
        if (inventoryFrame == null) {
            inventoryFrame = new JInternalFrame("Inventory", true, true, true, true);
            inventoryFrame.setSize(600, 400);
            inventoryFrame.setVisible(true);
            inventoryFrame.setLayout(new BorderLayout());
            inventoryFrame.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)),
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 4)
            ));
            inventoryFrame.setOpaque(true);
            inventoryFrame.getContentPane().setBackground(new Color(255, 248, 220));

            tabbedPane = new JTabbedPane();
            tabbedPane.setBackground(new Color(255, 248, 220));
            tabbedPane.setForeground(new Color(120, 90, 30));
            tabbedPane.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));

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
            goodsPanel.add(bawahPanel, BorderLayout.SOUTH);            btnHapus.addActionListener(e -> {
                int row = goodsTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Pilih barang terlebih dahulu!", "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Get the correct information from the table to find the actual Barang object
                String nama = goodsTable.getValueAt(row, 1).toString();
                String kategori = goodsTable.getValueAt(row, 2).toString();
                int kesegaran = Integer.parseInt(goodsTable.getValueAt(row, 3).toString());
                int hargaBeli = Integer.parseInt(goodsTable.getValueAt(row, 4).toString());
                
                // Find the actual Barang object in inventory
                Barang targetBarang = null;
                for (Barang b : inventory.getStokBarang()) {
                    if (b.getNamaBarang().equals(nama) && b.getKategori().equals(kategori) && 
                        b.getKesegaran() == kesegaran && b.getHargaBeli() == hargaBeli) {
                        targetBarang = b;
                        break;
                    }
                }

                if (targetBarang != null && inventory.hapusBarang(targetBarang)) {
                    JOptionPane.showMessageDialog(this, "Barang berhasil dihapus.");
                } else {
                    JOptionPane.showMessageDialog(this, "Barang tidak ditemukan.");
                }

                updateGoodsTable(currentSortBy, currentSortOrder); 
            });            btnMoveToGerobak.addActionListener(e -> {
                int row = goodsTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Pilih barang terlebih dahulu!", "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Get the correct information from the table to find the actual Barang object
                String nama = goodsTable.getValueAt(row, 1).toString();
                String kategori = goodsTable.getValueAt(row, 2).toString();
                int kesegaran = Integer.parseInt(goodsTable.getValueAt(row, 3).toString());
                int hargaBeli = Integer.parseInt(goodsTable.getValueAt(row, 4).toString());
                int jumlahTersedia = Integer.parseInt(goodsTable.getValueAt(row, 5).toString());
                
                // Show quantity input dialog
                String input = JOptionPane.showInputDialog(
                    this, 
                    "Masukkan jumlah yang ingin dipindahkan ke Gerobak:\n(Tersedia: " + jumlahTersedia + " buah)", 
                    "Input Jumlah", 
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (input == null || input.trim().isEmpty()) {
                    return; // User cancelled or entered empty input
                }
                
                int jumlah;
                try {
                    jumlah = Integer.parseInt(input.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Masukkan angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (jumlah <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (jumlah > jumlahTersedia) {
                    JOptionPane.showMessageDialog(this, "Jumlah melebihi stok yang tersedia!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Find the actual Barang object in inventory
                Barang targetBarang = null;
                for (Barang b : inventory.getStokBarang()) {
                    if (b.getNamaBarang().equals(nama) && b.getKategori().equals(kategori) && 
                        b.getKesegaran() == kesegaran && b.getHargaBeli() == hargaBeli) {
                        targetBarang = b;
                        break;
                    }
                }
                
                if (targetBarang != null) {
                    int kapasitasGerobak = 20; // You might want to get this from a Gerobak object
                    
                    // Check if gerobak has enough capacity
                    int remainingCapacity = inventory.kapasitasBarangTersisa(kapasitasGerobak);
                    if (jumlah > remainingCapacity) {
                        JOptionPane.showMessageDialog(this, 
                            "Kapasitas gerobak tidak cukup! Sisa kapasitas: " + remainingCapacity + " buah", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                      inventory.bawaBarang(targetBarang, jumlah, kapasitasGerobak);
                    
                    // Merge items with same properties after moving to gerobak
                    mergeItemsWithSamePropertiesAndPrice();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Berhasil memindahkan " + jumlah + " buah " + nama + " ke Gerobak.", 
                        "Sukses", 
                        JOptionPane.INFORMATION_MESSAGE);
                    updateGoodsTable(currentSortBy, currentSortOrder);
                } else {
                    JOptionPane.showMessageDialog(this, "Barang tidak ditemukan di inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            tabbedPane.addTab("Goods", goodsPanel);
            JPanel itemsPanel = new JPanel();
            itemsPanel.setBackground(new Color(255, 248, 220));
            tabbedPane.addTab("Items", itemsPanel);
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

    private void setHargaBarangGerobak() {
        int selectedRow = gerobakNoPriceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang di daftar kiri terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String namaBarang = gerobakNoPriceTable.getValueAt(selectedRow, 1).toString();
        String kategori = gerobakNoPriceTable.getValueAt(selectedRow, 2).toString();
        int kesegaran = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 3).toString());
        int jumlahTersedia = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 4).toString());
        int jumlah;
        int hargaJual;
        try {
            jumlah = Integer.parseInt(jumlahField.getText().trim());
            hargaJual = Integer.parseInt(hargaField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah dan harga jual yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (jumlah <= 0 || hargaJual <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah dan harga jual harus lebih dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (jumlah > jumlahTersedia) {
            JOptionPane.showMessageDialog(this, "Jumlah melebihi stok yang tersedia!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }        // Cari barang di inventory.getBarangDibawa()
        Barang barangTarget = null;
        for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
            if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori) && b.getKesegaran() == kesegaran && inventory.getHargaJual(b) <= 0) {
                barangTarget = b;
                break;
            }
        }
        if (barangTarget == null) {
            JOptionPane.showMessageDialog(this, "Barang tidak ditemukan di gerobak!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }        // Set harga jual pada barang (buat objek baru jika perlu)
        // Pertama, kurangi hanya jumlah yang akan diberi harga dari barang target
        inventory.kurangiBarangDibawa(barangTarget, jumlah);
        
        // Buat objek barang baru yang unik untuk item dengan harga
        // Menggunakan System.nanoTime() untuk memastikan objek baru memiliki hash yang berbeda
        Barang barangDenganHarga = new Barang(barangTarget.getNamaBarang(), barangTarget.getKategori(), barangTarget.getHargaBeli(), barangTarget.getIconPath()) {
            // Anonymous class untuk memastikan objek ini unik
            private final long uniqueId = System.nanoTime();            @Override
            public boolean equals(Object obj) {
                // Setiap instance dengan harga adalah unik - hanya equal dengan dirinya sendiri
                if (this == obj) return true;
                // Tidak pernah equal dengan objek lain, bahkan jika objek tersebut memiliki properti yang sama
                return false;
            }
            
            @Override
            public int hashCode() {
                // Hash berdasarkan identitas objek + uniqueId
                return System.identityHashCode(this) + (int)(uniqueId % Integer.MAX_VALUE);
            }
        };
        
        try {
            java.lang.reflect.Field f = Barang.class.getDeclaredField("kesegaran");
            f.setAccessible(true);
            f.setInt(barangDenganHarga, barangTarget.getKesegaran());
        } catch (Exception ex) { /* ignore */ }
          // Tambahkan barang dengan harga sesuai jumlah yang diminta
        inventory.tambahBarangDibawa(barangDenganHarga, jumlah);
        inventory.setHargaJual(barangDenganHarga, hargaJual);
        
        // Merge items with same properties and same price
        mergeItemsWithSamePropertiesAndPrice();
        
        // Jika masih sisa, jangan lakukan apa-apa (barang target sudah berkurang sesuai jumlah)// Reset input
        jumlahField.setText("");
        hargaField.setText("");
        updateGerobakTables();
        JOptionPane.showMessageDialog(this, "Harga jual berhasil diset untuk barang!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        
        // Debug: print current state
        System.out.println("Debug: Barang di gerobak setelah set harga:");
        for (Map.Entry<Barang, Integer> entry : inventory.getBarangDibawaMutable().entrySet()) {
            Barang b = entry.getKey();
            int jml = entry.getValue();
            int harga = inventory.getHargaJual(b);
            System.out.println("  " + b.getNamaBarang() + " (Kesegaran: " + b.getKesegaran() + ") - Jumlah: " + jml + ", Harga Jual: " + harga);
        }
    }

    private void showGerobakFrame() {
        if (gerobakFrame == null) {
            gerobakFrame = new JInternalFrame("Gerobak", true, true, true, true);
            gerobakFrame.setSize(800, 400);
            gerobakFrame.setLayout(new BorderLayout());
            gerobakFrame.setVisible(true);
            gerobakFrame.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)),
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 4)
            ));
            gerobakFrame.setOpaque(true);
            gerobakFrame.getContentPane().setBackground(new Color(255, 248, 220));

            // Panel utama dengan 3 bagian
            JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 0));
            mainPanel.setBackground(new Color(255, 248, 220));            // Bagian kiri: List barang tanpa harga jual + tombol Move to Inventory
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBackground(new Color(255, 248, 220));
            
            gerobakNoPriceTable = new JTable();
            JScrollPane leftScroll = new JScrollPane(gerobakNoPriceTable);
            leftScroll.getViewport().setBackground(new Color(255, 255, 240));
            leftScroll.setBorder(BorderFactory.createTitledBorder("Barang Belum Ada Harga Jual"));
            leftPanel.add(leftScroll, BorderLayout.CENTER);
            
            JButton btnMoveToInventory = StyledButton.create("Move to Inventory", 13, 160, 32);
            btnMoveToInventory.addActionListener(e -> moveFromGerobakToInventory());
            leftPanel.add(btnMoveToInventory, BorderLayout.SOUTH);
            
            mainPanel.add(leftPanel);            // Bagian tengah: Input jumlah, harga jual, tombol set harga
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(new Color(255, 248, 220));
            centerPanel.setBorder(BorderFactory.createTitledBorder("Set Harga Jual Barang"));
            centerPanel.add(Box.createVerticalStrut(30));
            JLabel jumlahLabel = new JLabel("Jumlah:");
            jumlahLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(jumlahLabel);
            jumlahField = new JTextField();
            jumlahField.setMaximumSize(new Dimension(200, 30));
            jumlahField.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(jumlahField);
            centerPanel.add(Box.createVerticalStrut(10));
            JLabel hargaLabel = new JLabel("Harga Jual:");
            hargaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(hargaLabel);
            hargaField = new JTextField();
            hargaField.setMaximumSize(new Dimension(200, 30));
            hargaField.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(hargaField);
            centerPanel.add(Box.createVerticalStrut(20));
            JButton setHargaBtn = StyledButton.create("Set Harga Jual", 13, 140, 32);
            setHargaBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(setHargaBtn);
            centerPanel.add(Box.createVerticalStrut(10));
            
            // Add the total stock label to center panel
            lblGerobakInfo = new JLabel();
            lblGerobakInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblGerobakInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(lblGerobakInfo);
            
            centerPanel.add(Box.createVerticalGlue());
            setHargaBtn.addActionListener(e -> setHargaBarangGerobak());
            mainPanel.add(centerPanel);            // Bagian kanan: List barang sudah ada harga jual + tombol Undo
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBackground(new Color(255, 248, 220));
            
            gerobakWithPriceTable = new JTable();
            JScrollPane rightScroll = new JScrollPane(gerobakWithPriceTable);
            rightScroll.getViewport().setBackground(new Color(255, 255, 240));
            rightScroll.setBorder(BorderFactory.createTitledBorder("Barang Sudah Ada Harga Jual"));
            rightPanel.add(rightScroll, BorderLayout.CENTER);
            
            JButton btnUndo = StyledButton.create("Undo", 13, 80, 32);
            btnUndo.addActionListener(e -> undoPriceFromGerobak());
            rightPanel.add(btnUndo, BorderLayout.SOUTH);
            
            mainPanel.add(rightPanel);            gerobakFrame.add(mainPanel, BorderLayout.CENTER);

            gerobakFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    gerobakFrame = null;
                }
            });
            desktopPane.add(gerobakFrame);
        }
        updateGerobakTables();
        gerobakFrame.setVisible(true);
        gerobakFrame.toFront();
    }

    private void updateGerobakTables() {
        if (gerobakFrame == null || !gerobakFrame.isVisible()) return;
        String[] cols = {"Icon", "Nama", "Kategori", "Kesegaran", "Jumlah"};
        String[] colsWithPrice = {"Icon", "Nama", "Kategori", "Kesegaran", "Harga Jual", "Jumlah"};
        Map<Barang, Integer> dibawa = inventory.getBarangDibawaMutable();
        java.util.List<Object[]> noPriceRows = new java.util.ArrayList<>();
        java.util.List<Object[]> withPriceRows = new java.util.ArrayList<>();
        
        System.out.println("Debug updateGerobakTables: Checking " + dibawa.size() + " items");
          for (Map.Entry<Barang, Integer> entry : dibawa.entrySet()) {
            Barang b = entry.getKey();
            int jml = entry.getValue();
            ImageIcon icon = GamePanel.getIcon(b.getIconPath(), 32, 32);
            if (icon == null) {
                // Fallback: try with item name if iconPath doesn't work
                icon = GamePanel.getIcon(b.getNamaBarang().toLowerCase().replace(' ', '_'), 32, 32);
            }
            int hargaJual = inventory.getHargaJual(b);
            
            System.out.println("  Item: " + b.getNamaBarang() + ", Kesegaran: " + b.getKesegaran() + ", Harga Jual: " + hargaJual);
            
            if (hargaJual <= 0) {
                noPriceRows.add(new Object[]{icon, b.getNamaBarang(), b.getKategori(), b.getKesegaran(), jml});
            } else {
                withPriceRows.add(new Object[]{icon, b.getNamaBarang(), b.getKategori(), b.getKesegaran(), hargaJual, jml});
            }
        }
        
        System.out.println("Debug: noPriceRows size: " + noPriceRows.size() + ", withPriceRows size: " + withPriceRows.size());
        
        DefaultTableModel noPriceModel = new DefaultTableModel(noPriceRows.toArray(Object[][]::new), cols) {
            @Override
            public Class<?> getColumnClass(int c) { return c == 0 ? Icon.class : Object.class; }
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        gerobakNoPriceTable.setModel(noPriceModel);
        gerobakNoPriceTable.setRowHeight(36);
        gerobakNoPriceTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        gerobakNoPriceTable.getColumnModel().getColumn(0).setCellRenderer((_,value,_,_,_,_) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 1; c < gerobakNoPriceTable.getColumnCount(); c++) {
            gerobakNoPriceTable.getColumnModel().getColumn(c).setCellRenderer(centerRenderer);
        }
        DefaultTableModel withPriceModel = new DefaultTableModel(withPriceRows.toArray(new Object[0][]), colsWithPrice) {
            @Override
            public Class<?> getColumnClass(int c) { return c == 0 ? Icon.class : Object.class; }
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        gerobakWithPriceTable.setModel(withPriceModel);
        gerobakWithPriceTable.setRowHeight(36);
        gerobakWithPriceTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        gerobakWithPriceTable.getColumnModel().getColumn(0).setCellRenderer((_,value,_,_,_,_) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });
        DefaultTableCellRenderer centerRenderer2 = new DefaultTableCellRenderer();
        centerRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 1; c < gerobakWithPriceTable.getColumnCount(); c++) {
            gerobakWithPriceTable.getColumnModel().getColumn(c).setCellRenderer(centerRenderer2);
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

        String[] cols = { "Icon", "Nama", "Kategori", "Kesegaran", "Harga Beli", "Jumlah" };        Object[][] data = new Object[rows.size()][cols.length];
        for (int i = 0; i < rows.size(); i++) {
            Barang b = (Barang) rows.get(i)[0];
            int jml = (int) rows.get(i)[1];            
            ImageIcon icon = GamePanel.getIcon(b.getIconPath(), 32, 32);
            if (icon == null) {
                // Fallback: try with item name if iconPath doesn't work
                icon = GamePanel.getIcon(b.getNamaBarang().toLowerCase().replace(' ', '_'), 32, 32);
            }
            data[i][0] = icon;
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
            inventory.addInventoryChangeListener(this::refreshInventoryAndGerobak);
        }
    }    
    public void refreshInventoryAndGerobak() {
        updateGoodsTable(currentSortBy, currentSortOrder);
        updateGerobakTables();
    }    private void moveFromInventoryToGerobak() {
        if (inventoryFrame != null && inventoryFrame.isVisible() && goodsTable != null) {
            int row = goodsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih barang dari Inventory terlebih dahulu!", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get the correct information from the table to find the actual Barang object
            String nama = goodsTable.getValueAt(row, 1).toString();
            String kategori = goodsTable.getValueAt(row, 2).toString();
            int kesegaran = Integer.parseInt(goodsTable.getValueAt(row, 3).toString());
            int hargaBeli = Integer.parseInt(goodsTable.getValueAt(row, 4).toString());
            int jumlahTersedia = Integer.parseInt(goodsTable.getValueAt(row, 5).toString());
            
            // Show quantity input dialog
            String input = JOptionPane.showInputDialog(
                this, 
                "Masukkan jumlah yang ingin dipindahkan ke Gerobak:\n(Tersedia: " + jumlahTersedia + " buah)", 
                "Input Jumlah", 
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (input == null || input.trim().isEmpty()) {
                return; // User cancelled or entered empty input
            }
            
            int jumlah;
            try {
                jumlah = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Masukkan angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (jumlah > jumlahTersedia) {
                JOptionPane.showMessageDialog(this, "Jumlah melebihi stok yang tersedia!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Find the actual Barang object in inventory
            Barang targetBarang = null;
            for (Barang b : inventory.getStokBarang()) {
                if (b.getNamaBarang().equals(nama) && b.getKategori().equals(kategori) && 
                    b.getKesegaran() == kesegaran && b.getHargaBeli() == hargaBeli) {
                    targetBarang = b;
                    break;
                }
            }
            
            if (targetBarang != null) {
                int kapasitasGerobak = 20; // You might want to get this from a Gerobak object
                
                // Check if gerobak has enough capacity
                int remainingCapacity = inventory.kapasitasBarangTersisa(kapasitasGerobak);
                if (jumlah > remainingCapacity) {
                    JOptionPane.showMessageDialog(this, 
                        "Kapasitas gerobak tidak cukup! Sisa kapasitas: " + remainingCapacity + " buah", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                  inventory.bawaBarang(targetBarang, jumlah, kapasitasGerobak);
                
                // Merge items with same properties after moving to gerobak
                mergeItemsWithSamePropertiesAndPrice();
                
                JOptionPane.showMessageDialog(this, 
                    "Berhasil memindahkan " + jumlah + " buah " + nama + " ke Gerobak.", 
                    "Sukses", 
                    JOptionPane.INFORMATION_MESSAGE);
                updateGoodsTable(currentSortBy, currentSortOrder);
                updateGerobakTables();
            } else {
                JOptionPane.showMessageDialog(this, "Barang tidak ditemukan di inventory.", "Error", JOptionPane.ERROR_MESSAGE);
            }        } else {
            JOptionPane.showMessageDialog(this, "Buka Inventory terlebih dahulu untuk memilih barang!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void moveFromGerobakToInventory() {
        int selectedRow = gerobakNoPriceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang dari daftar kiri terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String namaBarang = gerobakNoPriceTable.getValueAt(selectedRow, 1).toString();
        String kategori = gerobakNoPriceTable.getValueAt(selectedRow, 2).toString();
        int kesegaran = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 3).toString());
        int jumlahTersedia = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 4).toString());
        
        // Find the barang in gerobak (without price)
        Barang barangTarget = null;
        for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
            if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori) && 
                b.getKesegaran() == kesegaran && inventory.getHargaJual(b) <= 0) {
                barangTarget = b;
                break;
            }
        }
        
        if (barangTarget != null) {
            // Move the entire quantity back to inventory
            inventory.undoBawaBarang(barangTarget, jumlahTersedia);
            JOptionPane.showMessageDialog(this, "Barang dipindahkan kembali ke Inventory.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            updateGoodsTable(currentSortBy, currentSortOrder);
            updateGerobakTables();
        } else {
            JOptionPane.showMessageDialog(this, "Barang tidak ditemukan di gerobak!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    private void undoPriceFromGerobak() {
        int selectedRow = gerobakWithPriceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang dari daftar kanan terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String namaBarang = gerobakWithPriceTable.getValueAt(selectedRow, 1).toString();
        String kategori = gerobakWithPriceTable.getValueAt(selectedRow, 2).toString();
        int kesegaran = Integer.parseInt(gerobakWithPriceTable.getValueAt(selectedRow, 3).toString());
        
        // Find the barang with price in gerobak
        Barang barangWithPrice = null;
        for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
            if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori) && 
                b.getKesegaran() == kesegaran && inventory.getHargaJual(b) > 0) {
                barangWithPrice = b;
                break;
            }
        }
          if (barangWithPrice != null) {
            // Reset the price to 0 for this barang (this will move it back to the left table)
            inventory.setHargaJual(barangWithPrice, 0);
            
            // Merge items with same properties after undoing price
            mergeItemsWithSamePropertiesAndPrice();
            
            System.out.println("Debug: Undo price for " + barangWithPrice.getNamaBarang() + 
                " (Kesegaran: " + barangWithPrice.getKesegaran() + "), new price: " + inventory.getHargaJual(barangWithPrice));
            
            updateGerobakTables();
            JOptionPane.showMessageDialog(this, "Harga jual berhasil dihapus dari barang!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Barang tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Merge items in gerobak that have the same properties (nama, kategori, kesegaran, hargaBeli)
     * and the same selling price. This consolidates duplicate entries.
     */
    private void mergeItemsWithSamePropertiesAndPrice() {
        Map<Barang, Integer> barangDibawa = inventory.getBarangDibawaMutable();
        Map<String, List<Barang>> groupedItems = new HashMap<>();
        
        // Group items by their properties and selling price
        for (Barang b : barangDibawa.keySet()) {
            String key = b.getNamaBarang() + "|" + b.getKategori() + "|" + 
                        b.getKesegaran() + "|" + b.getHargaBeli() + "|" + 
                        inventory.getHargaJual(b);
            groupedItems.computeIfAbsent(key, k -> new ArrayList<>()).add(b);
        }
        
        // Merge items that have the same properties and selling price
        for (List<Barang> similarItems : groupedItems.values()) {
            if (similarItems.size() > 1) {
                // Find the total quantity across all similar items
                int totalQuantity = 0;
                for (Barang item : similarItems) {
                    totalQuantity += barangDibawa.get(item);
                }
                
                // Keep the first item and merge all quantities into it
                Barang keepItem = similarItems.get(0);
                
                // Remove all other similar items and add their quantities to the first one
                for (int i = 1; i < similarItems.size(); i++) {
                    Barang removeItem = similarItems.get(i);
                    barangDibawa.remove(removeItem);
                    // Also remove from price map if it has a price
                    if (inventory.getHargaJual(removeItem) > 0) {
                        inventory.setHargaJual(removeItem, 0); // This effectively removes it from price tracking
                    }
                }
                
                // Set the total quantity for the kept item
                barangDibawa.put(keepItem, totalQuantity);
                
                System.out.println("Merged " + similarItems.size() + " similar items of " + 
                    keepItem.getNamaBarang() + " with total quantity: " + totalQuantity);
            }
        }
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
        if (backButton != null) {
            backButton.setBounds(getWidth() - 140, getHeight() - 80, 120, 40);
        }
        if (desktopPane != null) {
            desktopPane.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        g2d.setColor(new Color(245, 222, 179));
        g2d.fillRect(0, 0, getWidth(), 150);
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
        g2d.setColor(new Color(245, 222, 179));
        g2d.fillRect(0, getHeight() - 100, getWidth(), 100);
        g2d.dispose();
    }
}
