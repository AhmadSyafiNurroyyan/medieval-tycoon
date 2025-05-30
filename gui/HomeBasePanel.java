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
import model.Gerobak;
import model.Inventory;
import model.Item;
import model.Perk;
import model.Player;
import interfaces.InventoryChangeListener;
import model.PerksManagement;

public class HomeBasePanel extends JPanel implements InventoryChangeListener {
    private JButton btn1, btn2, btn3, btn4, btn5, backButton;
    private Runnable backToGameCallback;
    private Inventory inventory;
    private Player player;
    private PerksManagement perksManagement;
    private JDesktopPane desktopPane;
    private JInternalFrame inventoryFrame, gerobakFrame, perksFrame;
    private JTable goodsTable, gerobakNoPriceTable, gerobakWithPriceTable;
    // Tambah field untuk item gerobak table
    private JTable itemGerobakTable;
    private JLabel lblJumlah, lblGerobakInfo;
    private Image bgImage, tetoImage;
    private final int currentSortBy = 0, currentSortOrder = 0;
    private JTextField jumlahField, hargaField;
    private JTabbedPane tabbedPane;
    private Gerobak gerobak;

    public HomeBasePanel(Player player) {
        this.player = player;
        this.perksManagement = new PerksManagement();
        setLayout(null);
        initializeComponents();
        loadImages();
        this.itemGerobakTable = new JTable();
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
        add(btn1);
        add(btn2);
        add(btn3);
        add(btn4);
        add(btn5);
        backButton = StyledButton.create("Kembali", 20, 120, 40);
        backButton.addActionListener(_ -> {
            if (backToGameCallback != null) {
                // Stop HomeBase BGM and start Map BGM when returning to game
                System.out.println("HomeBasePanel: Back button clicked - stopping HomeBase BGM and starting Map BGM");
                BGMPlayer.getInstance().stopHomeBaseBGM();
                BGMPlayer.getInstance().playMapBGM();
                backToGameCallback.run();
            }
        });
        add(backButton);

        // Desktop pane
        desktopPane = new JDesktopPane();
        desktopPane.setOpaque(false);
        add(desktopPane);
        setComponentZOrder(desktopPane, 0); // Action listeners
        btn1.addActionListener(_ -> showInventoryFrame());
        btn2.addActionListener(_ -> showGerobakFrame());
        btn3.addActionListener(_ -> showPerksFrame());
        btn4.addActionListener(_ -> {
        });
        btn5.addActionListener(_ -> {
        });
    }

    private Font loadCustomFont() {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/medieval.ttf"));
        } catch (FontFormatException | IOException e) {
            return new Font("Serif", Font.BOLD, 24);
        }
    }

    private void loadImages() {
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
            inventoryFrame.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)),
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 4)));
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
            JButton btnHapus = StyledButton.create("Hapus Barang", 14, 150, 38);
            JButton btnMoveToGerobak = StyledButton.create("Move to Gerobak", 14, 180, 38);
            JButton btnBersihkanBusuk = StyledButton.create("Bersihkan Barang Busuk", 14, 180, 38);

            lblJumlah = new JLabel("Jumlah barang: " + (inventory != null ? inventory.getJumlahBarang() : 0));
            lblJumlah.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblJumlah.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 13)); // Create a panel for the buttons with
                                                                                // better layout
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            buttonPanel.setOpaque(false);
            buttonPanel.add(btnHapus);
            buttonPanel.add(btnMoveToGerobak);
            buttonPanel.add(btnBersihkanBusuk);

            JPanel bawahPanel = new JPanel(new BorderLayout());
            bawahPanel.add(buttonPanel, BorderLayout.WEST);
            bawahPanel.add(lblJumlah, BorderLayout.EAST);
            goodsPanel.add(bawahPanel, BorderLayout.SOUTH);
            btnHapus.addActionListener(_ -> {
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
                    refreshInventoryAndGerobak();
                } else {
                    JOptionPane.showMessageDialog(this, "Barang tidak ditemukan.");
                }
            });

            btnMoveToGerobak.addActionListener(_ -> {
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
                        JOptionPane.QUESTION_MESSAGE);

                if (input == null || input.trim().isEmpty()) {
                    return; // User cancelled or entered empty input
                }

                int jumlah;
                try {
                    jumlah = Integer.parseInt(input.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Masukkan angka yang valid!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (jumlah <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (jumlah > jumlahTersedia) {
                    JOptionPane.showMessageDialog(this, "Jumlah melebihi stok yang tersedia!", "Error",
                            JOptionPane.ERROR_MESSAGE);
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
                    // Get the actual gerobak capacity from inventory
                    int kapasitasGerobak = 20; // Default fallback
                    if (inventory.getGerobak() != null) {
                        kapasitasGerobak = inventory.getGerobak().getKapasitasBarang();
                    }

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
                    refreshInventoryAndGerobak();
                } else {
                    JOptionPane.showMessageDialog(this, "Barang tidak ditemukan di inventory.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            btnBersihkanBusuk.addActionListener(_ -> {
                if (inventory == null) {
                    JOptionPane.showMessageDialog(this, "Inventory tidak tersedia!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Apakah anda yakin ingin menghapus semua barang busuk dari inventory?",
                        "Konfirmasi Bersihkan Barang Busuk",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    inventory.bersihkanBarangBusuk();
                    JOptionPane.showMessageDialog(this,
                            "Semua barang busuk telah dihapus dari inventory.",
                            "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshInventoryAndGerobak();
                }
            });

            tabbedPane.addTab("Goods", goodsPanel);

            JPanel itemsPanel = new JPanel(new BorderLayout());
            itemsPanel.setBackground(new Color(255, 248, 220)); // Removed filter panel for cleaner interface

            // Tabel untuk item
            JTable itemsTable = new JTable();
            itemsTable.getTableHeader().setBackground(new Color(212, 175, 55));
            itemsTable.getTableHeader().setForeground(new Color(60, 40, 10));
            itemsTable.setBackground(new Color(255, 255, 240));
            itemsTable.setForeground(new Color(60, 40, 10));
            JScrollPane itemsScroll = new JScrollPane(itemsTable);
            itemsScroll.getViewport().setBackground(new Color(255, 255, 240));
            itemsScroll.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
            itemsPanel.add(itemsScroll, BorderLayout.CENTER);

            // Panel untuk tombol aksi
            JPanel itemButtonsPanel = new JPanel(new BorderLayout());
            itemButtonsPanel.setOpaque(false);

            // Buat panel untuk tombol-tombol di sebelah kiri
            JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftButtonsPanel.setOpaque(false); // Tombol Hapus Item
            JButton deleteItemBtn = StyledButton.create("Hapus Item", 14, 150, 38);
            deleteItemBtn.addActionListener(_ -> deleteSelectedItem(itemsTable));
            leftButtonsPanel.add(deleteItemBtn);

            // Tombol Move to Gerobak
            JButton moveItemToGerobakBtn = StyledButton.create("Move to Gerobak", 14, 180, 38);
            moveItemToGerobakBtn.addActionListener(_ -> moveItemToGerobak(itemsTable));
            leftButtonsPanel.add(moveItemToGerobakBtn);

            // Label jumlah item
            JLabel itemCountLabel = new JLabel("Jumlah item: 0");
            itemCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            itemCountLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 13));

            itemButtonsPanel.add(leftButtonsPanel, BorderLayout.WEST);
            itemButtonsPanel.add(itemCountLabel, BorderLayout.EAST);
            itemsPanel.add(itemButtonsPanel, BorderLayout.SOUTH);
            tabbedPane.addTab("Items", itemsPanel);

            // Initial update - always show all items (filter mode 0)
            updateItemsTable(itemsTable, itemCountLabel, 0);
            inventoryFrame.add(tabbedPane, BorderLayout.CENTER);

            sortCombo.addActionListener(
                    _ -> updateGoodsTable(sortCombo.getSelectedIndex(), orderCombo.getSelectedIndex()));
            orderCombo.addActionListener(
                    _ -> updateGoodsTable(sortCombo.getSelectedIndex(), orderCombo.getSelectedIndex()));
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

    public void initializeWithGerobak(Gerobak gerobak) {
        System.out.println("Debug initializeWithGerobak:");
        System.out.println(
                "  - Received gerobak: " + (gerobak != null ? "exists (level=" + gerobak.getLevel() + ")" : "null"));
        System.out.println("  - Current inventory: " + (inventory != null ? "exists" : "null"));

        this.gerobak = gerobak;
        if (inventory != null) {
            System.out.println("  - Setting gerobak in inventory");
            inventory.setGerobak(gerobak);
            System.out.println(
                    "  - Inventory gerobak after set: " + (inventory.getGerobak() != null ? "exists" : "null"));
        }

        // Ensure player's inventory uses the same gerobak if it exists
        if (player != null && player.getInventory() != null) {
            player.getInventory().setGerobak(gerobak);
            System.out.println("  - Set gerobak in player's inventory");
        }
    }

    @Override
    public void onInventoryChanged() {
        // Refresh semua tabel ketika inventory berubah
        SwingUtilities.invokeLater(() -> {
            System.out.println("Debug: onInventoryChanged triggered - refreshing UI");
            refreshInventoryAndGerobak();

            // Update items table juga jika sedang terbuka
            if (inventoryFrame != null && inventoryFrame.isVisible()) {
                updateItemsTableInCurrentTab();
            }

            // Update item gerobak table jika gerobak frame terbuka
            if (gerobakFrame != null && gerobakFrame.isVisible()) {
                updateItemGerobakTable();
            }
        });
    }

    /**
     * Update items table in current tab when inventory frame is open
     */
    private void updateItemsTableInCurrentTab() {
        if (inventoryFrame == null || !inventoryFrame.isVisible()) {
            return;
        }

        // Find the tabbed pane
        JTabbedPane tabPane = null;
        for (Component c : inventoryFrame.getContentPane().getComponents()) {
            if (c instanceof JTabbedPane) {
                tabPane = (JTabbedPane) c;
                break;
            }
        }

        if (tabPane == null || tabPane.getTabCount() <= 1) {
            return;
        }

        // Get the Items tab (index 1)
        Component itemsTab = tabPane.getComponentAt(1);
        if (!(itemsTab instanceof JPanel)) {
            return;
        } // Find the items table and count label
        JTable itemsTable = null;
        JLabel countLabel = null;

        // Search for components in the items tab
        for (Component c : ((JPanel) itemsTab).getComponents()) {
            if (c instanceof JScrollPane) {
                Component view = ((JScrollPane) c).getViewport().getView();
                if (view instanceof JTable) {
                    itemsTable = (JTable) view;
                }
            } else if (c instanceof JPanel) {
                // Search in nested panels
                searchForItemsComponents((JPanel) c, new ComponentHolder(countLabel, null));
                if (componentHolder.countLabel != null)
                    countLabel = componentHolder.countLabel;
            }
        }

        // Update the table if we found all necessary components
        if (itemsTable != null && countLabel != null) {
            // Always use filter mode 0 (show all items)
            updateItemsTable(itemsTable, countLabel, 0);
            System.out.println("Debug: Items table updated automatically via InventoryChangeListener");
        }
    } // Helper class to hold component references

    private static class ComponentHolder {
        JLabel countLabel;

        ComponentHolder(JLabel countLabel, JComboBox<?> unused) {
            this.countLabel = countLabel;
        }
    }

    // We need to add this field at class level
    private ComponentHolder componentHolder = new ComponentHolder(null, null);

    /**
     * Recursively search for items table components
     */
    private void searchForItemsComponents(JPanel panel, ComponentHolder holder) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().startsWith("Jumlah item")) {
                holder.countLabel = (JLabel) comp;
            } else if (comp instanceof JPanel) {
                searchForItemsComponents((JPanel) comp, holder);
            }
        }
    }

    private void updateGoodsTable(int sortBy, int sortOrder) {
        if (goodsTable == null || inventory == null)
            return;

        List<Barang> stokBarang = inventory.getStokBarang();

        // Group similar items and count them
        Map<String, Map<String, Object>> groupedItems = new LinkedHashMap<>();
        for (Barang barang : stokBarang) {
            String key = barang.getNamaBarang() + "|" + barang.getKategori() + "|" +
                    barang.getKesegaran() + "|" + barang.getHargaBeli();

            if (groupedItems.containsKey(key)) {
                Map<String, Object> itemData = groupedItems.get(key);
                itemData.put("jumlah", (Integer) itemData.get("jumlah") + 1);
            } else {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("barang", barang);
                itemData.put("jumlah", 1);
                groupedItems.put(key, itemData);
            }
        }

        // Convert to array for table
        String[] columnNames = { "Icon", "Nama", "Kategori", "Kesegaran", "Harga Beli", "Jumlah" };
        Object[][] data = new Object[groupedItems.size()][columnNames.length];

        int row = 0;
        for (Map<String, Object> itemData : groupedItems.values()) {
            Barang barang = (Barang) itemData.get("barang"); // Get icon directly from file path
            ImageIcon icon = new ImageIcon("assets/icons/" + barang.getIconPath());
            // Scale the image to appropriate size
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            } else {
                // Fallback to GamePanel.getIcon if direct loading fails
                icon = GamePanel.getIcon(barang.getNamaBarang().toLowerCase().replace(' ', '_'), 32, 32);
            }

            data[row][0] = icon;
            data[row][1] = barang.getNamaBarang();
            data[row][2] = barang.getKategori();
            data[row][3] = barang.getKesegaran();
            data[row][4] = barang.getHargaBeli();
            data[row][5] = itemData.get("jumlah");
            row++;
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
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

        // Set column widths
        goodsTable.getColumnModel().getColumn(0).setPreferredWidth(40); // Icon
        goodsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Nama
        goodsTable.getColumnModel().getColumn(2).setPreferredWidth(80); // Kategori
        goodsTable.getColumnModel().getColumn(3).setPreferredWidth(60); // Kesegaran
        goodsTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Harga Beli
        goodsTable.getColumnModel().getColumn(5).setPreferredWidth(60); // Jumlah

        // Set renderers
        goodsTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < columnNames.length; i++) {
            goodsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Update the count label
        if (lblJumlah != null) {
            lblJumlah.setText("Jumlah barang: " + inventory.getJumlahBarang());
        }
    } // Method untuk update gerobak tables

    private void updateGerobakTables() {
        if (inventory == null)
            return;

        updateGerobakNoPriceTable();
        updateGerobakWithPriceTable();

        // Update info label
        if (lblGerobakInfo != null) {
            int totalBarang = 0;
            Map<Barang, Integer> barangDibawa = inventory.getBarangDibawaMutable();
            for (int jumlah : barangDibawa.values()) {
                totalBarang += jumlah;
            }
            lblGerobakInfo.setText("Total barang di gerobak: " + totalBarang);
        }
    }

    private void updateGerobakNoPriceTable() {
        if (gerobakNoPriceTable == null || inventory == null)
            return;

        Map<Barang, Integer> barangDibawa = inventory.getBarangDibawaMutable();

        // Filter items without price (harga jual <= 0)
        List<Map<String, Object>> itemsWithoutPrice = new ArrayList<>();
        for (Map.Entry<Barang, Integer> entry : barangDibawa.entrySet()) {
            Barang barang = entry.getKey();
            int jumlah = entry.getValue();
            int hargaJual = inventory.getHargaJual(barang);

            if (hargaJual <= 0) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("barang", barang);
                itemData.put("jumlah", jumlah);
                itemsWithoutPrice.add(itemData);
            }
        }

        String[] columnNames = { "Icon", "Nama", "Kategori", "Kesegaran", "Jumlah" };
        Object[][] data = new Object[itemsWithoutPrice.size()][columnNames.length];

        for (int i = 0; i < itemsWithoutPrice.size(); i++) {
            Map<String, Object> itemData = itemsWithoutPrice.get(i);
            Barang barang = (Barang) itemData.get("barang");

            // Get icon
            ImageIcon icon = GamePanel.getIcon("assets/icons/" + barang.getIconPath(), 32, 32);
            if (icon == null) {
                icon = GamePanel.getIcon(barang.getNamaBarang().toLowerCase().replace(' ', '_'), 32, 32);
            }

            data[i][0] = icon;
            data[i][1] = barang.getNamaBarang();
            data[i][2] = barang.getKategori();
            data[i][3] = barang.getKesegaran();
            data[i][4] = itemData.get("jumlah");
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        gerobakNoPriceTable.setModel(model);
        gerobakNoPriceTable.setRowHeight(36);

        // Set column widths
        gerobakNoPriceTable.getColumnModel().getColumn(0).setPreferredWidth(40); // Icon
        gerobakNoPriceTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Nama
        gerobakNoPriceTable.getColumnModel().getColumn(2).setPreferredWidth(80); // Kategori
        gerobakNoPriceTable.getColumnModel().getColumn(3).setPreferredWidth(60); // Kesegaran
        gerobakNoPriceTable.getColumnModel().getColumn(4).setPreferredWidth(60); // Jumlah

        // Set renderers
        gerobakNoPriceTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < columnNames.length; i++) {
            gerobakNoPriceTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void updateGerobakWithPriceTable() {
        if (gerobakWithPriceTable == null || inventory == null)
            return;

        Map<Barang, Integer> barangDibawa = inventory.getBarangDibawaMutable();

        // Filter items with price (harga jual > 0)
        List<Map<String, Object>> itemsWithPrice = new ArrayList<>();
        for (Map.Entry<Barang, Integer> entry : barangDibawa.entrySet()) {
            Barang barang = entry.getKey();
            int jumlah = entry.getValue();
            int hargaJual = inventory.getHargaJual(barang);

            if (hargaJual > 0) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("barang", barang);
                itemData.put("jumlah", jumlah);
                itemData.put("hargaJual", hargaJual);
                itemsWithPrice.add(itemData);
            }
        }

        String[] columnNames = { "Icon", "Nama", "Kategori", "Kesegaran", "Jumlah", "Harga Jual" };
        Object[][] data = new Object[itemsWithPrice.size()][columnNames.length];

        for (int i = 0; i < itemsWithPrice.size(); i++) {
            Map<String, Object> itemData = itemsWithPrice.get(i);
            Barang barang = (Barang) itemData.get("barang");

            // Get icon
            ImageIcon icon = GamePanel.getIcon("assets/icons/" + barang.getIconPath(), 32, 32);
            if (icon == null) {
                icon = GamePanel.getIcon(barang.getNamaBarang().toLowerCase().replace(' ', '_'), 32, 32);
            }

            data[i][0] = icon;
            data[i][1] = barang.getNamaBarang();
            data[i][2] = barang.getKategori();
            data[i][3] = barang.getKesegaran();
            data[i][4] = itemData.get("jumlah");
            data[i][5] = itemData.get("hargaJual") + "G";
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        gerobakWithPriceTable.setModel(model);
        gerobakWithPriceTable.setRowHeight(36);

        // Set column widths
        gerobakWithPriceTable.getColumnModel().getColumn(0).setPreferredWidth(40); // Icon
        gerobakWithPriceTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Nama
        gerobakWithPriceTable.getColumnModel().getColumn(2).setPreferredWidth(80); // Kategori
        gerobakWithPriceTable.getColumnModel().getColumn(3).setPreferredWidth(60); // Kesegaran
        gerobakWithPriceTable.getColumnModel().getColumn(4).setPreferredWidth(60); // Jumlah
        gerobakWithPriceTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Harga Jual

        // Set renderers
        gerobakWithPriceTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < columnNames.length; i++) {
            gerobakWithPriceTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void setHargaBarangGerobak() {
        int selectedRow = gerobakNoPriceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang di daftar kiri terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Masukkan jumlah dan harga jual yang valid!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (jumlah <= 0 || hargaJual <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah dan harga jual harus lebih dari 0!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (jumlah > jumlahTersedia) {
            JOptionPane.showMessageDialog(this, "Jumlah melebihi stok yang tersedia!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cari barang di inventory.getBarangDibawa()
        Barang barangTarget = null;
        for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
            if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori)
                    && b.getKesegaran() == kesegaran && inventory.getHargaJual(b) <= 0) {
                barangTarget = b;
                break;
            }
        }
        if (barangTarget == null) {
            JOptionPane.showMessageDialog(this, "Barang tidak ditemukan di gerobak!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (hargaJual > (2 * barangTarget.getHargaBeli())) {
            JOptionPane.showMessageDialog(this, "Harga jual tidak boleh lebih dari 2 kali harga beli!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set harga jual pada barang (buat objek baru jika perlu)
        // Pertama, kurangi hanya jumlah yang akan diberi harga dari barang target
        inventory.kurangiBarangDibawa(barangTarget, jumlah);

        // Buat objek barang baru yang unik untuk item dengan harga
        // Menggunakan System.nanoTime() untuk memastikan objek baru memiliki hash yang
        // berbeda
        Barang barangDenganHarga = new Barang(barangTarget.getNamaBarang(), barangTarget.getKategori(),
                barangTarget.getHargaBeli(), barangTarget.getIconPath()) {
            // Anonymous class untuk memastikan objek ini unik
            private final long uniqueId = System.nanoTime();

            @Override
            public boolean equals(Object obj) {
                // Setiap instance dengan harga adalah unik - hanya equal dengan dirinya sendiri
                if (this == obj)
                    return true;
                // Tidak pernah equal dengan objek lain, bahkan jika objek tersebut memiliki
                // properti yang sama
                return false;
            }

            @Override
            public int hashCode() {
                // Hash berdasarkan identitas objek + uniqueId
                return System.identityHashCode(this) + (int) (uniqueId % Integer.MAX_VALUE);
            }
        };

        try {
            java.lang.reflect.Field f = Barang.class.getDeclaredField("kesegaran");
            f.setAccessible(true);
            f.setInt(barangDenganHarga, barangTarget.getKesegaran());
        } catch (Exception ex) {
            /* ignore */
        }

        // Tambahkan barang dengan harga sesuai jumlah yang diminta
        inventory.tambahBarangDibawa(barangDenganHarga, jumlah);
        inventory.setHargaJual(barangDenganHarga, hargaJual);

        // Merge items with same properties and same price
        mergeItemsWithSamePropertiesAndPrice();

        // Reset input
        jumlahField.setText("");
        hargaField.setText("");
        updateGerobakTables();
        JOptionPane.showMessageDialog(this, "Harga jual berhasil diset untuk barang!", "Sukses",
                JOptionPane.INFORMATION_MESSAGE);

        // Debug: print current state
        System.out.println("Debug: Barang di gerobak setelah set harga:");
        for (Map.Entry<Barang, Integer> entry : inventory.getBarangDibawaMutable().entrySet()) {
            Barang b = entry.getKey();
            int jml = entry.getValue();
            int harga = inventory.getHargaJual(b);
            System.out.println("  " + b.getNamaBarang() + " (Kesegaran: " + b.getKesegaran() + ") - Jumlah: " + jml
                    + ", Harga Jual: " + harga);
        }
    }

    private void showGerobakFrame() {
        if (gerobakFrame == null) {
            // Create header panel with level info and upgrade button
            JPanel headerPanel = createGerobakHeaderPanel();

            gerobakFrame = new JInternalFrame("Gerobak", true, true, true, true);
            gerobakFrame.setSize(1000, 550); // Increased height for header
            gerobakFrame.setLayout(new BorderLayout());
            gerobakFrame.setVisible(true);
            gerobakFrame.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)),
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 4)));
            gerobakFrame.setOpaque(true);
            gerobakFrame.getContentPane().setBackground(new Color(255, 248, 220));

            // Add header panel at the top
            gerobakFrame.add(headerPanel, BorderLayout.NORTH);

            // Buat tabbed pane untuk gerobak
            JTabbedPane gerobakTabs = new JTabbedPane();
            gerobakTabs.setBackground(new Color(255, 248, 220));
            gerobakTabs.setForeground(new Color(120, 90, 30));
            gerobakTabs.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));

            // Tab pertama untuk barang (existing code)
            JPanel barangPanel = createBarangGerobakTab();
            gerobakTabs.addTab("Barang", barangPanel);

            // Tab kedua untuk item
            JPanel itemPanel = createItemGerobakTab();
            gerobakTabs.addTab("Item", itemPanel);

            gerobakFrame.add(gerobakTabs, BorderLayout.CENTER);

            gerobakFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    gerobakFrame = null;
                }
            });
            desktopPane.add(gerobakFrame);
        }
        updateGerobakTables();
        updateItemGerobakTable();
        gerobakFrame.setVisible(true);
        gerobakFrame.toFront();
    }

    // Method untuk create tab barang (existing logic dipindah ke sini)
    private JPanel createBarangGerobakTab() {
        // Panel utama dengan 3 bagian
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        mainPanel.setBackground(new Color(255, 248, 220));

        // Bagian kiri: List barang tanpa harga jual + tombol Move to Inventory
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(255, 248, 220));

        gerobakNoPriceTable = new JTable();
        JScrollPane leftScroll = new JScrollPane(gerobakNoPriceTable);
        leftScroll.getViewport().setBackground(new Color(255, 255, 240));
        leftScroll.setBorder(BorderFactory.createTitledBorder("Barang Belum Ada Harga Jual"));
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        JButton btnMoveToInventory = StyledButton.create("Move to Inventory", 13, 160, 32);
        btnMoveToInventory.addActionListener(_ -> moveFromGerobakToInventory());
        leftPanel.add(btnMoveToInventory, BorderLayout.SOUTH);

        mainPanel.add(leftPanel);

        // Bagian tengah: Input jumlah, harga jual, tombol set harga
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
        setHargaBtn.addActionListener(_ -> setHargaBarangGerobak());
        mainPanel.add(centerPanel);

        // Bagian kanan: List barang sudah ada harga jual + tombol Undo
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(255, 248, 220));

        gerobakWithPriceTable = new JTable();
        JScrollPane rightScroll = new JScrollPane(gerobakWithPriceTable);
        rightScroll.getViewport().setBackground(new Color(255, 255, 240));
        rightScroll.setBorder(BorderFactory.createTitledBorder("Barang Sudah Ada Harga Jual"));
        rightPanel.add(rightScroll, BorderLayout.CENTER);

        JButton btnUndo = StyledButton.create("Undo", 13, 80, 32);
        btnUndo.addActionListener(_ -> undoPriceFromGerobak());
        rightPanel.add(btnUndo, BorderLayout.SOUTH);

        mainPanel.add(rightPanel);

        return mainPanel;
    }

    // Method baru untuk create tab item
    private JPanel createItemGerobakTab() {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(new Color(255, 248, 220));

        // Tabel untuk item di gerobak
        itemGerobakTable = new JTable();
        itemGerobakTable.getTableHeader().setBackground(new Color(212, 175, 55));
        itemGerobakTable.getTableHeader().setForeground(new Color(60, 40, 10));
        itemGerobakTable.setBackground(new Color(255, 255, 240));
        itemGerobakTable.setForeground(new Color(60, 40, 10));

        JScrollPane itemScroll = new JScrollPane(itemGerobakTable);
        itemScroll.getViewport().setBackground(new Color(255, 255, 240));
        itemScroll.setBorder(BorderFactory.createTitledBorder("Item di Gerobak"));
        itemPanel.add(itemScroll, BorderLayout.CENTER);

        // Panel tombol untuk item
        JPanel itemButtonPanel = new JPanel(new BorderLayout());
        itemButtonPanel.setBackground(new Color(255, 248, 220));

        // Tombol Move back to Inventory
        JButton btnMoveItemBack = StyledButton.create("Move to Inventory", 13, 160, 32);
        btnMoveItemBack.addActionListener(_ -> moveItemFromGerobakToInventory());

        // Label info item
        JLabel lblItemInfo = new JLabel("Total item di Gerobak: 0");
        lblItemInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblItemInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 13));

        itemButtonPanel.add(btnMoveItemBack, BorderLayout.WEST);
        itemButtonPanel.add(lblItemInfo, BorderLayout.EAST);
        itemPanel.add(itemButtonPanel, BorderLayout.SOUTH);

        return itemPanel;
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

    private void updateItemGerobakTable() {
        System.out.println("Debug: updateItemGerobakTable called");

        if (itemGerobakTable == null || inventory == null) {
            System.out.println("Debug: itemGerobakTable is null: " + (itemGerobakTable == null));
            System.out.println("Debug: inventory is null: " + (inventory == null));
            return;
        }

        List<Item> itemsDiGerobak = inventory.getItemDibawa();
        System.out.println("Debug: Items in gerobak: " + itemsDiGerobak.size());

        for (Item item : itemsDiGerobak) {
            System.out.println("Debug: Item in gerobak: " + item.getNama());
        }

        String[] cols = { "Icon", "Nama", "Level", "Chance", "Status", "Deskripsi" };
        Object[][] data = new Object[itemsDiGerobak.size()][cols.length];
        for (int i = 0; i < itemsDiGerobak.size(); i++) {
            Item item = itemsDiGerobak.get(i);
            // Try to load the icon directly from the file path
            ImageIcon icon = new ImageIcon("assets/icons/" + item.getIconPath());
            // Scale the image to appropriate size
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            } else {
                // Fallback to GamePanel.getIcon if direct loading fails
                icon = GamePanel.getIcon(item.getNama().toLowerCase().replace(' ', '_'), 32, 32);
            }

            data[i][0] = icon;
            data[i][1] = item.getNama();
            data[i][2] = "Level " + item.getLevel();
            data[i][3] = getItemEffectPercentage(item);
            data[i][4] = item.isActive() ? "Aktif" : "Non-aktif";
            data[i][5] = item.getDeskripsi();
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

        itemGerobakTable.setModel(model);
        itemGerobakTable.setRowHeight(36);
        itemGerobakTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        itemGerobakTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        itemGerobakTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        itemGerobakTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        itemGerobakTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        itemGerobakTable.getColumnModel().getColumn(5).setPreferredWidth(240);

        // Set renderer untuk icon column
        itemGerobakTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        // Set renderer untuk kolom lain (center alignment)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 5; i++) {
            itemGerobakTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Left align description column
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        itemGerobakTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);

        // Update info label - cari label di panel
        if (gerobakFrame != null && gerobakFrame.isVisible()) {
            updateItemInfoLabel(itemsDiGerobak.size());
        }
    }

    // Helper method untuk update label info item
    private void updateItemInfoLabel(int itemCount) {
        // Cari label info di tab item
        JTabbedPane gerobakTabs = null;
        for (Component c : gerobakFrame.getContentPane().getComponents()) {
            if (c instanceof JTabbedPane) {
                gerobakTabs = (JTabbedPane) c;
                break;
            }
        }

        if (gerobakTabs != null && gerobakTabs.getTabCount() > 1) {
            // Get the Items tab
            Component itemTab = gerobakTabs.getComponentAt(1);
            if (itemTab instanceof JPanel) {
                // Cari label info
                findAndUpdateLabel((JPanel) itemTab, "Total item di Gerobak: " + itemCount);
            }
        }
    }

    // Helper method untuk cari dan update label
    private void findAndUpdateLabel(JPanel panel, String newText) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JPanel) {
                for (Component inner : ((JPanel) c).getComponents()) {
                    if (inner instanceof JLabel && ((JLabel) inner).getText().startsWith("Total item")) {
                        ((JLabel) inner).setText(newText);
                        return;
                    }
                }
            }
        }
    }

    // Method untuk move item dari gerobak kembali ke inventory
    private void moveItemFromGerobakToInventory() {
        int selectedRow = itemGerobakTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaItem = itemGerobakTable.getValueAt(selectedRow, 1).toString();

        // Confirm action
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah anda yakin ingin memindahkan item \"" + namaItem + "\" kembali ke Inventory?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (inventory.undoBawaItem(namaItem)) {
                JOptionPane.showMessageDialog(this,
                        "Item berhasil dipindahkan kembali ke Inventory.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

                // Refresh displays
                refreshInventoryAndGerobak();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal memindahkan item.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Update method refreshInventoryAndGerobak()
    public void refreshInventoryAndGerobak() {
        updateGoodsTable(currentSortBy, currentSortOrder);
        updateGerobakTables();
        updateItemGerobakTable();

        // Also update items table if inventory frame is open
        if (inventoryFrame != null && inventoryFrame.isVisible()) {
            JTabbedPane tabPane = null;
            for (Component c : inventoryFrame.getContentPane().getComponents()) {
                if (c instanceof JTabbedPane) {
                    tabPane = (JTabbedPane) c;
                    break;
                }
            }

            if (tabPane != null && tabPane.getTabCount() > 1) {
                // Get the Items tab
                Component itemsTab = tabPane.getComponentAt(1);
                if (itemsTab instanceof JPanel) {
                    // Find the table and count label
                    JTable itemsTable = null;
                    JLabel countLabel = null;
                    JComboBox<?> filterCombo = null;

                    // Search for the table and count label
                    for (Component c : ((JPanel) itemsTab).getComponents()) {
                        if (c instanceof JScrollPane) {
                            Component view = ((JScrollPane) c).getViewport().getView();
                            if (view instanceof JTable) {
                                itemsTable = (JTable) view;
                            }
                        } else if (c instanceof JPanel) {
                            for (Component inner : ((JPanel) c).getComponents()) {
                                if (inner instanceof JLabel && ((JLabel) inner).getText().startsWith("Jumlah item")) {
                                    countLabel = (JLabel) inner;
                                } else if (inner instanceof JComboBox) {
                                    filterCombo = (JComboBox<?>) inner;
                                }
                            }
                        }
                    }

                    if (itemsTable != null && countLabel != null) {
                        int filterIndex = filterCombo != null ? filterCombo.getSelectedIndex() : 0;
                        updateItemsTable(itemsTable, countLabel, filterIndex);
                    }
                }
            }
        }
    }

    private void moveItemToGerobak(JTable itemsTable) {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the item name from the items table
        String namaItem = itemsTable.getValueAt(selectedRow, 1).toString();
        int kapasitasItem = 0;

        // Find the actual Item object in inventory
        Item targetItem = null;
        for (Item item : inventory.getStokItem()) {
            if (item.getNama().equals(namaItem)) {
                targetItem = item;
                break;
            }
        }
        if (targetItem == null) {
            JOptionPane.showMessageDialog(this, "Item tidak ditemukan di inventory.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use inventory.getGerobak() instead of this.gerobak to ensure we get the
        // correct gerobak object
        Gerobak inventoryGerobak = inventory.getGerobak();
        if (inventoryGerobak != null) {
            kapasitasItem = inventoryGerobak.getKapasitasItem();
        }

        int totalItemDiGerobak = inventory.getJumlahItemDiGerobak();

        // Tambahkan debug ini:
        System.out.println("Debug moveItemToGerobak:");
        System.out.println(
                "  - this.gerobak: " + (gerobak != null ? "exists (level=" + gerobak.getLevel() + ")" : "null"));
        System.out.println("  - inventory.gerobak: "
                + (inventoryGerobak != null ? "exists (level=" + inventoryGerobak.getLevel() + ")" : "null"));
        System.out.println("  - kapasitasItem: " + kapasitasItem);
        System.out.println("  - totalItemDiGerobak: " + totalItemDiGerobak);

        if (totalItemDiGerobak >= kapasitasItem) {
            JOptionPane.showMessageDialog(this,
                    "Kapasitas gerobak untuk item penuh! Sisa kapasitas: " + (kapasitasItem - totalItemDiGerobak),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } // Add this before the bawaItem call for debugging
        System.out.println("Debug: Moving item " + namaItem);
        System.out.println("Debug: Current items in gerobak: " + totalItemDiGerobak);
        System.out.println("Debug: Gerobak capacity: " + kapasitasItem);
        System.out.println("Debug: Using inventory.gerobak: " + (inventoryGerobak != null ? "exists" : "null"));

        try {
            boolean success = inventory.bawaItem(namaItem, kapasitasItem);
            if (success) {
                JOptionPane.showMessageDialog(this, "Item berhasil dipindahkan ke Gerobak.", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

                // Refresh the display
                refreshInventoryAndGerobak();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memindahkan item ke gerobak.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void moveFromGerobakToInventory() {
        int selectedRow = gerobakNoPriceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang dari daftar kiri terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Barang dipindahkan kembali ke Inventory.", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshInventoryAndGerobak();
        } else {
            JOptionPane.showMessageDialog(this, "Barang tidak ditemukan di gerobak!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void undoPriceFromGerobak() {
        int selectedRow = gerobakWithPriceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang dari daftar kanan terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
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
            // Reset the price to 0 for this barang (this will move it back to the left
            // table)
            inventory.setHargaJual(barangWithPrice, 0);

            // Merge items with same properties after undoing price
            mergeItemsWithSamePropertiesAndPrice();

            System.out.println("Debug: Undo price for " + barangWithPrice.getNamaBarang() +
                    " (Kesegaran: " + barangWithPrice.getKesegaran() + "), new price: "
                    + inventory.getHargaJual(barangWithPrice));

            updateGerobakTables();
            JOptionPane.showMessageDialog(this, "Harga jual berhasil dihapus dari barang!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Barang tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Merge items in gerobak that have the same properties (nama, kategori,
     * kesegaran, hargaBeli)
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
            groupedItems.computeIfAbsent(key, _ -> new ArrayList<>()).add(b);
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
    } // Tambahkan method ini di kelas HomeBasePanel

    private void updateItemsTable(JTable itemsTable, JLabel itemCountLabel, int filterMode) {
        if (inventory == null)
            return;

        // Always show all items - filter mode is ignored
        List<Item> allItems = inventory.getStokItem();
        List<Item> filteredItems = allItems;

        String[] cols = { "Icon", "Nama", "Level", "Chance", "Status", "Deskripsi" };
        Object[][] data = new Object[filteredItems.size()][cols.length];
        for (int i = 0; i < filteredItems.size(); i++) {
            Item item = filteredItems.get(i);
            // Try to load the icon directly from the file path
            ImageIcon icon = new ImageIcon("assets/icons/" + item.getIconPath());
            // Scale the image to appropriate size
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            } else {
                // Fallback to GamePanel.getIcon if direct loading fails
                icon = GamePanel.getIcon(item.getNama().toLowerCase().replace(' ', '_'), 32, 32);
            }

            data[i][0] = icon;
            data[i][1] = item.getNama();
            data[i][2] = "Level " + item.getLevel();
            data[i][3] = getItemEffectPercentage(item);
            data[i][4] = item.isActive() ? "Aktif" : "Non-aktif";
            data[i][5] = item.getDeskripsi();
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

        itemsTable.setModel(model);
        itemsTable.setRowHeight(36);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(240);

        // Set renderer for icon column
        itemsTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        // Set renderer for other columns (center alignment)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 5; i++) { // Columns 1-4 centered
            itemsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Left align the description column
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);

        // Update count label
        itemCountLabel.setText("Jumlah item: " + filteredItems.size());
    }

    private void deleteSelectedItem(JTable itemsTable) {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaItem = itemsTable.getValueAt(selectedRow, 1).toString();

        // Konfirmasi penghapusan
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah anda yakin ingin menghapus item \"" + namaItem + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (inventory.hapusItem(namaItem)) {
                JOptionPane.showMessageDialog(this,
                        "Item \"" + namaItem + "\" berhasil dihapus.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

                // Refresh table
                JComboBox<?> filterCombo = null;
                // Find the filter combo box
                for (Component comp : ((JPanel) tabbedPane.getComponentAt(1)).getComponents()) {
                    if (comp instanceof JPanel) {
                        for (Component innerComp : ((JPanel) comp).getComponents()) {
                            if (innerComp instanceof JComboBox) {
                                filterCombo = (JComboBox<?>) innerComp;
                                break;
                            }
                        }
                    }
                    if (filterCombo != null)
                        break;
                }

                // Update table with current filter
                int filterIndex = filterCombo != null ? filterCombo.getSelectedIndex() : 0;

                // Find the count label
                JLabel countLabel = null;
                for (Component comp : ((JPanel) tabbedPane.getComponentAt(1)).getComponents()) {
                    if (comp instanceof JPanel && ((JPanel) comp).getLayout() instanceof BorderLayout) {
                        for (Component innerComp : ((JPanel) comp).getComponents()) {
                            if (innerComp instanceof JLabel
                                    && ((JLabel) innerComp).getText().startsWith("Jumlah item")) {
                                countLabel = (JLabel) innerComp;
                                break;
                            }
                        }
                    }
                    if (countLabel != null)
                        break;
                }

                updateItemsTable(itemsTable, countLabel, filterIndex);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus item.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void setInventory(Inventory inventory) {
        // Remove listener dari inventory lama jika ada
        if (this.inventory != null) {
            this.inventory.removeInventoryChangeListener(this);
        }

        this.inventory = inventory;

        // Tambahkan listener ke inventory baru
        if (inventory != null) {
            inventory.addInventoryChangeListener(this);
            System.out.println("Debug: InventoryChangeListener added to inventory");
        }
    }

    public void setBackToGameCallback(Runnable callback) {
        this.backToGameCallback = callback;
    }

    /**
     * Called when HomeBase panel becomes visible (starts BGM)
     */
    public void onPanelShown() {
        System.out.println("HomeBasePanel: onPanelShown() called - starting HomeBase BGM");
        // Stop Map BGM if playing and start HomeBase BGM
        BGMPlayer.getInstance().stopMapBGM();
        BGMPlayer.getInstance().playHomeBaseBGM();
    }

    /**
     * Called when HomeBase panel is hidden (stops BGM)
     */
    public void onPanelHidden() {
        System.out.println("HomeBasePanel: onPanelHidden() called - stopping HomeBase BGM and starting Map BGM");

        // Make sure inventory changes are properly saved before leaving HomeBase
        if (this.inventory != null && this.player != null) {
            // Ensure that the player has the current inventory
            this.player.setInventory(this.inventory);

            // Debug inventory state
            System.out.println("Debug: Items in gerobak when leaving HomeBase: " +
                    this.inventory.getItemDibawa().size());
            for (Item item : this.inventory.getItemDibawa()) {
                System.out.println("Debug: Keeping item in gerobak: " + item.getNama());
            }
        }

        BGMPlayer.getInstance().stopHomeBaseBGM();
        // Start Map BGM when leaving HomeBase
        BGMPlayer.getInstance().playMapBGM();
    }

    /**
     * Update player reference for loading saved games
     */
    public void updatePlayerData(Player newPlayer) {
        this.player = newPlayer;
        System.out.println("HomeBasePanel: Updated player data - Username: " + newPlayer.getUsername() +
                ", Money: " + newPlayer.getMoney());

        // Update any open frames that display player data
        updatePerksFrameContent();
    }

    /**
     * Creates header panel with gerobak level info and upgrade functionality
     */
    private JPanel createGerobakHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 248, 220));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Left side - Level info
        JPanel levelInfoPanel = new JPanel();
        levelInfoPanel.setLayout(new BoxLayout(levelInfoPanel, BoxLayout.Y_AXIS));
        levelInfoPanel.setBackground(new Color(255, 248, 220));
        final Gerobak currentGerobak = inventory != null ? inventory.getGerobak()
                : (gerobak != null ? gerobak : new Gerobak());

        JLabel levelLabel = new JLabel("Level: " + currentGerobak.getLevel());
        levelLabel.setFont(new Font("Serif", Font.BOLD, 18));
        levelLabel.setForeground(new Color(120, 90, 30));

        JLabel capacityBarangLabel = new JLabel("Kapasitas Barang: " + currentGerobak.getKapasitasBarang());
        capacityBarangLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        capacityBarangLabel.setForeground(new Color(120, 90, 30));

        JLabel capacityItemLabel = new JLabel("Kapasitas Item: " + currentGerobak.getKapasitasItem());
        capacityItemLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        capacityItemLabel.setForeground(new Color(120, 90, 30));

        levelInfoPanel.add(levelLabel);
        levelInfoPanel.add(capacityBarangLabel);
        levelInfoPanel.add(capacityItemLabel);

        // Right side - Upgrade section
        JPanel upgradePanel = new JPanel();
        upgradePanel.setLayout(new BoxLayout(upgradePanel, BoxLayout.Y_AXIS));
        upgradePanel.setBackground(new Color(255, 248, 220));
        upgradePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        if (currentGerobak.isMaxLevel()) {
            JLabel maxLevelLabel = new JLabel("LEVEL MAKSIMAL");
            maxLevelLabel.setFont(new Font("Serif", Font.BOLD, 16));
            maxLevelLabel.setForeground(new Color(180, 120, 30));
            maxLevelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            upgradePanel.add(maxLevelLabel);
        } else {
            int upgradeCost = currentGerobak.getBiayaUpgrade();

            JLabel upgradeCostLabel = new JLabel("Biaya Upgrade: " + upgradeCost + "G");
            upgradeCostLabel.setFont(new Font("Serif", Font.PLAIN, 14));
            upgradeCostLabel.setForeground(new Color(120, 90, 30));
            upgradeCostLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nextLevelLabel = new JLabel("Next Level: " + (currentGerobak.getLevel() + 1));
            nextLevelLabel.setFont(new Font("Serif", Font.PLAIN, 12));
            nextLevelLabel.setForeground(new Color(100, 70, 20));
            nextLevelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton upgradeButton = StyledButton.create("Upgrade Gerobak", 14, 150, 35);
            upgradeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add action listener for upgrade
            upgradeButton.addActionListener(_ -> upgradeGerobak(currentGerobak, currentGerobak.getBiayaUpgrade()));

            upgradePanel.add(nextLevelLabel);
            upgradePanel.add(Box.createVerticalStrut(5));
            upgradePanel.add(upgradeCostLabel);
            upgradePanel.add(Box.createVerticalStrut(5));
            upgradePanel.add(upgradeButton);
        }

        headerPanel.add(levelInfoPanel, BorderLayout.WEST);
        headerPanel.add(upgradePanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Upgrade the gerobak to the next level if possible
     */
    private void upgradeGerobak(Gerobak gerobak, int biayaUpgrade) {
        if (player == null)
            return;

        // Check if already at max level
        if (gerobak.isMaxLevel()) {
            JOptionPane.showMessageDialog(this,
                    "Gerobak sudah mencapai level maksimal!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Recalculate the actual current upgrade cost
        int actualUpgradeCost = gerobak.getBiayaUpgrade();

        // Check if the player has enough money
        if (player.getMoney() < actualUpgradeCost) {
            JOptionPane.showMessageDialog(this,
                    "Uang tidak cukup untuk upgrade!\nBiaya: " + actualUpgradeCost + "G\nUang Anda: "
                            + player.getMoney() + "G",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } // Perform the upgrade
        boolean upgradeSuccess = gerobak.upgradeLevel();
        if (upgradeSuccess) {
            player.kurangiMoney(actualUpgradeCost);

            // Make sure the inventory's gerobak is also updated
            if (inventory != null) {
                inventory.setGerobak(gerobak);
            }

            // Completely close and reopen the gerobak frame if it's open
            boolean wasGerobakFrameVisible = false;
            if (gerobakFrame != null && gerobakFrame.isVisible()) {
                wasGerobakFrameVisible = true;
                gerobakFrame.dispose();
                gerobakFrame = null;
            }

            // Refresh the displays
            refreshInventoryAndGerobak();

            // Reopen the gerobak frame if it was open before
            if (wasGerobakFrameVisible) {
                showGerobakFrame();
            }

            JOptionPane.showMessageDialog(this,
                    "Gerobak berhasil di-upgrade ke Level " + gerobak.getLevel() + "!\n" +
                            "Kapasitas Barang: " + gerobak.getKapasitasBarang() + "\n" +
                            "Kapasitas Item: " + gerobak.getKapasitasItem(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Also update UI elements from other panels that might show player money
            if (backToGameCallback != null) {
                // This will signal the main game to update money displays
                // Note: If there's a more direct way to update money displays, use that instead
                System.out.println("Debug: Signaling game to refresh money display");
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal melakukan upgrade. Gerobak mungkin sudah mencapai level maksimal.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPerksFrame() {
        if (perksFrame == null) {
            perksFrame = new JInternalFrame("Perks", true, true, true, true);
            perksFrame.setSize(800, 500);
            perksFrame.setVisible(true);
            perksFrame.setLayout(new BorderLayout());
            perksFrame.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)),
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 4)));
            perksFrame.setOpaque(true);
            perksFrame.getContentPane().setBackground(new Color(255, 248, 220));

            // Header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(255, 248, 220));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            JLabel titleLabel = new JLabel("Perks yang Dimiliki", JLabel.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
            titleLabel.setForeground(new Color(120, 90, 30));
            headerPanel.add(titleLabel, BorderLayout.CENTER);

            // Money display
            JLabel moneyLabel = new JLabel("Uang: " + player.getMoney() + "G", JLabel.RIGHT);
            moneyLabel.setFont(new Font("Serif", Font.PLAIN, 18));
            moneyLabel.setForeground(new Color(120, 90, 30));
            headerPanel.add(moneyLabel, BorderLayout.EAST);

            perksFrame.add(headerPanel, BorderLayout.NORTH);

            // Main content panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(255, 248, 220));

            // Perks table
            JTable perksTable = new JTable();
            perksTable.getTableHeader().setBackground(new Color(212, 175, 55));
            perksTable.getTableHeader().setForeground(new Color(60, 40, 10));
            perksTable.setBackground(new Color(255, 255, 240));
            perksTable.setForeground(new Color(60, 40, 10));
            perksTable.setRowHeight(50);

            updatePerksTable(perksTable);

            JScrollPane perksScroll = new JScrollPane(perksTable);
            perksScroll.getViewport().setBackground(new Color(255, 255, 240));
            perksScroll.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
            mainPanel.add(perksScroll, BorderLayout.CENTER);

            // Action buttons panel
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            actionPanel.setBackground(new Color(255, 248, 220));

            JButton activateButton = StyledButton.create("Activate", 14, 150, 35);
            JButton deactivateButton = StyledButton.create("Deactivate", 14, 120, 35);
            JButton refreshButton = StyledButton.create("Refresh", 14, 100, 35);

            activateButton.addActionListener(_ -> {
                int selectedRow = perksTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Pilih perk terlebih dahulu!", "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String perkName = perksTable.getValueAt(selectedRow, 1).toString().split(" \\(")[0];

                // Find the perk by name
                List<Perk> ownedPerks = perksManagement.getPerkYangDimiliki(player);
                Perk selectedPerk = null;
                for (Perk perk : ownedPerks) {
                    if (perk.getName().equals(perkName)) {
                        selectedPerk = perk;
                        break;
                    }
                }

                if (selectedPerk != null) {
                    if (perksManagement.pilihPerkUntukJualan(player, selectedPerk)) {
                        JOptionPane.showMessageDialog(this,
                                "Perk " + selectedPerk.getName() + " berhasil diaktifkan!",
                                "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        updatePerksTable(perksTable);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Gagal mengaktifkan perk. Mungkin slot sudah penuh atau perk sudah aktif.",
                                "Gagal", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            deactivateButton.addActionListener(_ -> {
                int selectedRow = perksTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Pilih perk terlebih dahulu!", "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String perkName = perksTable.getValueAt(selectedRow, 1).toString().split(" \\(")[0];

                // Find the perk by name
                List<Perk> tradingPerks = player.getPerkDipilihUntukJualan();
                Perk selectedPerk = null;
                for (Perk perk : tradingPerks) {
                    if (perk.getName().equals(perkName)) {
                        selectedPerk = perk;
                        break;
                    }
                }

                if (selectedPerk != null) {
                    selectedPerk.deactivate();
                    player.getPerkDipilihUntukJualan().remove(selectedPerk);
                    JOptionPane.showMessageDialog(this,
                            "Perk " + selectedPerk.getName() + " berhasil dinonaktifkan!",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    updatePerksTable(perksTable);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Perk tidak sedang aktif.",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            refreshButton.addActionListener(_ -> {
                updatePerksTable(perksTable);
                moneyLabel.setText("Uang: " + player.getMoney() + "G");
            });

            actionPanel.add(activateButton);
            actionPanel.add(deactivateButton);
            actionPanel.add(refreshButton);

            mainPanel.add(actionPanel, BorderLayout.SOUTH);
            perksFrame.add(mainPanel, BorderLayout.CENTER);

            // Add frame listener to clean up when closed
            perksFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    perksFrame = null;
                }
            });

            desktopPane.add(perksFrame);
        }

        // Update the table and money display when showing the frame
        if (perksFrame.getContentPane().getComponentCount() > 0) {
            // Find and update table
            updatePerksFrameContent();
        }

        perksFrame.setVisible(true);
        perksFrame.toFront();
    }

    private void updatePerksTable(JTable perksTable) {
        List<Perk> ownedPerks = perksManagement.getPerkYangDimiliki(player);
        List<Perk> tradingPerks = player.getPerkDipilihUntukJualan();

        String[] columns = { "Icon", "Name & Level", "Type", "Power", "Perks Status", "Description" };
        Object[][] data = new Object[ownedPerks.size()][columns.length];

        for (int i = 0; i < ownedPerks.size(); i++) {
            Perk perk = ownedPerks.get(i);

            // Load icon
            ImageIcon icon = GamePanel.getIcon(perk.getIconPath(), 32, 32);
            if (icon == null) {
                icon = GamePanel.getIcon("default_perk", 32, 32);
            }

            data[i][0] = icon;
            data[i][1] = perk.getName() + " (Lv." + perk.getLevel() + ")";
            data[i][2] = perk.getPerkType().toString();
            data[i][3] = String.format("%.1f", perk.getKesaktianSekarang());
            data[i][4] = tradingPerks.contains(perk) ? "Active" : "Inactive";
            data[i][5] = perk.getDeskripsi();
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        perksTable.setModel(model);

        // Set column widths
        perksTable.getColumnModel().getColumn(0).setPreferredWidth(50); // Icon
        perksTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name & Level
        perksTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Type
        perksTable.getColumnModel().getColumn(3).setPreferredWidth(80); // Power
        perksTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Trading Status
        perksTable.getColumnModel().getColumn(5).setPreferredWidth(300); // Description

        // Set cell renderers
        perksTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i <= 4; i++) {
            perksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Left align description
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        perksTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);
    }

    private void updatePerksFrameContent() {
        if (perksFrame == null || !perksFrame.isVisible())
            return;

        // Find the money label and update it
        updateMoneyInPerksFrame(perksFrame.getContentPane());

        // Find the table and update it
        updateTableInPerksFrame(perksFrame.getContentPane());
    }

    private void updateMoneyInPerksFrame(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                updateMoneyInPerksFrame((Container) comp);
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().startsWith("Uang:")) {
                    label.setText("Uang: " + player.getMoney() + "G");
                }
            }
        }
    }

    private void updateTableInPerksFrame(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JScrollPane) {
                Component view = ((JScrollPane) comp).getViewport().getView();
                if (view instanceof JTable) {
                    updatePerksTable((JTable) view);
                    return;
                }
            } else if (comp instanceof JPanel) {
                updateTableInPerksFrame((Container) comp);
            }
        }
    }
}
