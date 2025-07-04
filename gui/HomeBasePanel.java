/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package gui;

import interfaces.InventoryChangeListener;
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
import model.ItemEffectManager;
import model.Perk;
import model.PerksManagement;
import model.Player;
import model.Supplier;

public class HomeBasePanel extends JPanel implements InventoryChangeListener {
    private JButton btn1, btn2, btn3, btn4, btn5, backButton;
    private Runnable backToGameCallback;
    private Inventory inventory;
    private Player player;
    private PerksManagement perksManagement;
    private JDesktopPane desktopPane;
    private JInternalFrame inventoryFrame, gerobakFrame, perksFrame, statsFrame;
    private JTable goodsTable, gerobakNoPriceTable, gerobakWithPriceTable;
    private JTable itemGerobakTable;
    private JLabel lblJumlah, lblGerobakInfo;
    private Image bgImage, tetoImage;
    private final int currentSortBy = 0, currentSortOrder = 0;
    private JTextField jumlahField, hargaField;
    private JTabbedPane tabbedPane;
    private Gerobak gerobak;
    private int currentDay = 1;
    private JLabel dayLabel;
    private Runnable onSleepCallback;
    private GamePanel gamePanel;
    private Supplier supplier;
    private ItemEffectManager itemEffectManager;

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        if (gamePanel != null) {
            this.currentDay = gamePanel.getCurrentDay();
        }
    }

    public HomeBasePanel(Player player) {
        this.player = player;
        this.perksManagement = new PerksManagement();
        this.itemEffectManager = new ItemEffectManager(player);
        setLayout(null);
        initializeComponents();
        loadImages();
        this.itemGerobakTable = new JTable();
    }

    public void updateDayLabel() {
        if (dayLabel != null) {
            dayLabel.setText("Day " + currentDay);
        }
    }

    private void initializeComponents() {
        JLabel titleLabel = new JLabel("Home Base");
        titleLabel.setFont(loadCustomFont().deriveFont(80f));
        titleLabel.setBounds(20, 15, 100000, 200);
        add(titleLabel);
        dayLabel = new JLabel("Day " + currentDay, JLabel.CENTER);
        dayLabel.setFont(new Font("Serif", Font.BOLD, 32));
        dayLabel.setForeground(new Color(120, 90, 30));
        dayLabel.setBounds(670, 70, 140, 40);
        add(dayLabel);

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
                System.out.println("HomeBasePanel: Back button clicked - stopping HomeBase BGM and starting Map BGM");
                BGMPlayer.getInstance().stopHomeBaseBGM();
                BGMPlayer.getInstance().playMapBGM();
                backToGameCallback.run();
            }
        });
        add(backButton);

        desktopPane = new JDesktopPane();
        desktopPane.setOpaque(false);
        add(desktopPane);
        setComponentZOrder(desktopPane, 0);
        btn1.addActionListener(_ -> showInventoryFrame());
        btn2.addActionListener(_ -> showGerobakFrame());
        btn3.addActionListener(_ -> showPerksFrame());
        btn4.addActionListener(_ -> showStatsFrame());
        btn5.addActionListener(_ -> sleepAndAdvanceDay());
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
            lblJumlah.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 13));
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

                String nama = goodsTable.getValueAt(row, 1).toString();
                String kategori = goodsTable.getValueAt(row, 2).toString();
                int kesegaran = Integer.parseInt(goodsTable.getValueAt(row, 3).toString());
                int hargaBeli = Integer.parseInt(goodsTable.getValueAt(row, 4).toString());

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

                String nama = goodsTable.getValueAt(row, 1).toString();
                String kategori = goodsTable.getValueAt(row, 2).toString();
                int kesegaran = Integer.parseInt(goodsTable.getValueAt(row, 3).toString());
                int hargaBeli = Integer.parseInt(goodsTable.getValueAt(row, 4).toString());
                int jumlahTersedia = Integer.parseInt(goodsTable.getValueAt(row, 5).toString());

                String input = JOptionPane.showInputDialog(
                        this,
                        "Masukkan jumlah yang ingin dipindahkan ke Gerobak:\n(Tersedia: " + jumlahTersedia + " buah)",
                        "Input Jumlah",
                        JOptionPane.QUESTION_MESSAGE);

                if (input == null || input.trim().isEmpty()) {
                    return;
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

                Barang targetBarang = null;
                System.out.println("Debug: Looking for barang to move:");
                System.out.println("  - Nama: " + nama + ", Kategori: " + kategori +
                        ", Kesegaran: " + kesegaran + ", HargaBeli: " + hargaBeli);

                System.out.println("Debug: Available barang in inventory:");
                for (Barang b : inventory.getStokBarang()) {
                    System.out.println("  - " + b.getNamaBarang() + " (Kategori: " + b.getKategori() +
                            ", Kesegaran: " + b.getKesegaran() + ", HargaBeli: " + b.getHargaBeli() + ")");
                    if (b.getNamaBarang().equals(nama) && b.getKategori().equals(kategori) &&
                            b.getKesegaran() == kesegaran && b.getHargaBeli() == hargaBeli) {
                        targetBarang = b;
                        System.out.println("    -> FOUND MATCH!");
                        break;
                    }
                }

                if (targetBarang == null) {
                    System.out.println("Debug: No matching barang found!");
                }
                if (targetBarang != null) {
                    if (targetBarang.getKesegaran() <= 0) {
                        JOptionPane.showMessageDialog(this,
                                "Tidak bisa memindahkan barang busuk ke gerobak!\nBuang barang busuk terlebih dahulu.",
                                "Barang Busuk",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int kapasitasGerobak = 20;
                    if (inventory.getGerobak() != null) {
                        kapasitasGerobak = inventory.getGerobak().getKapasitasBarang();
                    }

                    int remainingCapacity = inventory.kapasitasBarangTersisa(kapasitasGerobak);
                    if (jumlah > remainingCapacity) {
                        JOptionPane.showMessageDialog(this,
                                "Kapasitas gerobak tidak cukup! Sisa kapasitas: " + remainingCapacity + " buah",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    inventory.bawaBarang(targetBarang, jumlah, kapasitasGerobak);

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
            itemsPanel.setBackground(new Color(255, 248, 220));

            JTable itemsTable = new JTable();
            itemsTable.getTableHeader().setBackground(new Color(212, 175, 55));
            itemsTable.getTableHeader().setForeground(new Color(60, 40, 10));
            itemsTable.setBackground(new Color(255, 255, 240));
            itemsTable.setForeground(new Color(60, 40, 10));
            JScrollPane itemsScroll = new JScrollPane(itemsTable);
            itemsScroll.getViewport().setBackground(new Color(255, 255, 240));
            itemsScroll.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
            itemsPanel.add(itemsScroll, BorderLayout.CENTER);

            JPanel itemButtonsPanel = new JPanel(new BorderLayout());
            itemButtonsPanel.setOpaque(false);

            JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftButtonsPanel.setOpaque(false);
            JButton deleteItemBtn = StyledButton.create("Hapus Item", 14, 150, 38);
            deleteItemBtn.addActionListener(_ -> deleteSelectedItem(itemsTable));
            leftButtonsPanel.add(deleteItemBtn);

            JButton moveItemToGerobakBtn = StyledButton.create("Move to Gerobak", 14, 180, 38);
            moveItemToGerobakBtn.addActionListener(_ -> moveItemToGerobak(itemsTable));
            leftButtonsPanel.add(moveItemToGerobakBtn);

            JLabel itemCountLabel = new JLabel("Jumlah item: 0");
            itemCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            itemCountLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 13));

            itemButtonsPanel.add(leftButtonsPanel, BorderLayout.WEST);
            itemButtonsPanel.add(itemCountLabel, BorderLayout.EAST);
            itemsPanel.add(itemButtonsPanel, BorderLayout.SOUTH);
            tabbedPane.addTab("Items", itemsPanel);

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

        if (player != null && player.getInventory() != null) {
            player.getInventory().setGerobak(gerobak);
            System.out.println("  - Set gerobak in player's inventory");
        }
    }

    @Override
    public void onInventoryChanged() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Debug: onInventoryChanged triggered - refreshing UI");
            refreshInventoryAndGerobak();

            if (inventoryFrame != null && inventoryFrame.isVisible()) {
                updateItemsTableInCurrentTab();
            }

            if (gerobakFrame != null && gerobakFrame.isVisible()) {
                updateItemGerobakTable();
            }
        });
    }

    private void updateItemsTableInCurrentTab() {
        if (inventoryFrame == null || !inventoryFrame.isVisible()) {
            return;
        }

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

        Component itemsTab = tabPane.getComponentAt(1);
        if (!(itemsTab instanceof JPanel)) {
            return;
        }
        JTable itemsTable = null;
        JLabel countLabel = null;

        for (Component c : ((JPanel) itemsTab).getComponents()) {
            if (c instanceof JScrollPane) {
                Component view = ((JScrollPane) c).getViewport().getView();
                if (view instanceof JTable) {
                    itemsTable = (JTable) view;
                }
            } else if (c instanceof JPanel) {
                searchForItemsComponents((JPanel) c, new ComponentHolder(countLabel, null));
                if (componentHolder.countLabel != null)
                    countLabel = componentHolder.countLabel;
            }
        }

        if (itemsTable != null && countLabel != null) {
            updateItemsTable(itemsTable, countLabel, 0);
            System.out.println("Debug: Items table updated automatically via InventoryChangeListener");
        }
    }

    private static class ComponentHolder {
        JLabel countLabel;

        ComponentHolder(JLabel countLabel, JComboBox<?> unused) {
            this.countLabel = countLabel;
        }
    }

    private ComponentHolder componentHolder = new ComponentHolder(null, null);

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

        System.out.println("Debug updateGoodsTable: Updating table with current inventory data");
        List<Barang> stokBarang = inventory.getStokBarang();
        System.out.println("Debug updateGoodsTable: Total barang in inventory: " + stokBarang.size());

        Map<String, Map<String, Object>> groupedItems = new LinkedHashMap<>();
        for (Barang barang : stokBarang) {
            System.out.println("Debug updateGoodsTable: Processing " + barang.getNamaBarang() +
                    " (Kesegaran: " + barang.getKesegaran() + ")");
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

        String[] columnNames = { "Icon", "Nama", "Kategori", "Kesegaran", "Harga Beli", "Jumlah" };
        Object[][] data = new Object[groupedItems.size()][columnNames.length];

        int row = 0;
        for (Map<String, Object> itemData : groupedItems.values()) {
            Barang barang = (Barang) itemData.get("barang");
            ImageIcon icon = GamePanel.getIcon(barang.getIconPath(), 32, 32);
            if (icon == null) {
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

        goodsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        goodsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        goodsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        goodsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        goodsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        goodsTable.getColumnModel().getColumn(5).setPreferredWidth(60);

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

        if (lblJumlah != null) {
            lblJumlah.setText("Jumlah barang: " + inventory.getJumlahBarang());
        }
    }

    private void updateGerobakTables() {
        if (inventory == null)
            return;

        updateGerobakNoPriceTable();
        updateGerobakWithPriceTable();

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

            ImageIcon icon = GamePanel.getIcon(barang.getIconPath(), 32, 32);
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

        gerobakNoPriceTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        gerobakNoPriceTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        gerobakNoPriceTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        gerobakNoPriceTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        gerobakNoPriceTable.getColumnModel().getColumn(4).setPreferredWidth(60);

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

            ImageIcon icon = GamePanel.getIcon(barang.getIconPath(), 32, 32);
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

        if (hargaJual > (3 * barangTarget.getHargaBeli())) {
            JOptionPane.showMessageDialog(this, "Harga jual tidak boleh lebih dari 3 kali harga beli!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (hargaJual < barangTarget.getHargaBeli()) {
            JOptionPane.showMessageDialog(this, "Harga jual tidak boleh kurang dari harga beli!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        inventory.kurangiBarangDibawa(barangTarget, jumlah);

        Barang barangDenganHarga = new Barang(barangTarget.getNamaBarang(), barangTarget.getKategori(),
                barangTarget.getHargaBeli(), barangTarget.getIconPath()) {
            private final long uniqueId = System.nanoTime();

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                return false;
            }

            @Override
            public int hashCode() {
                return System.identityHashCode(this) + (int) (uniqueId % Integer.MAX_VALUE);
            }
        };

        try {
            java.lang.reflect.Field f = Barang.class.getDeclaredField("kesegaran");
            f.setAccessible(true);
            f.setInt(barangDenganHarga, barangTarget.getKesegaran());
        } catch (Exception ex) {
        }

        inventory.tambahBarangDibawa(barangDenganHarga, jumlah);
        inventory.setHargaJual(barangDenganHarga, hargaJual);

        mergeItemsWithSamePropertiesAndPrice();

        jumlahField.setText("");
        hargaField.setText("");
        updateGerobakTables();
        JOptionPane.showMessageDialog(this, "Harga jual berhasil diset untuk barang!", "Sukses",
                JOptionPane.INFORMATION_MESSAGE);

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
            JPanel headerPanel = createGerobakHeaderPanel();

            gerobakFrame = new JInternalFrame("Gerobak", true, true, true, true);
            gerobakFrame.setSize(1000, 550);
            gerobakFrame.setLayout(new BorderLayout());
            gerobakFrame.setVisible(true);
            gerobakFrame.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)),
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 4)));
            gerobakFrame.setOpaque(true);
            gerobakFrame.getContentPane().setBackground(new Color(255, 248, 220));

            gerobakFrame.add(headerPanel, BorderLayout.NORTH);

            JTabbedPane gerobakTabs = new JTabbedPane();
            gerobakTabs.setBackground(new Color(255, 248, 220));
            gerobakTabs.setForeground(new Color(120, 90, 30));
            gerobakTabs.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));

            JPanel barangPanel = createBarangGerobakTab();
            gerobakTabs.addTab("Barang", barangPanel);

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

    private JPanel createBarangGerobakTab() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        mainPanel.setBackground(new Color(255, 248, 220));

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

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(255, 248, 220));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Set Harga Jual Barang"));
        centerPanel.add(Box.createVerticalStrut(30));

        JLabel jumlahLabel = new JLabel("Jumlah:");
        jumlahLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(jumlahLabel);
        JPanel jumlahPanel = new JPanel();
        jumlahPanel.setLayout(new BoxLayout(jumlahPanel, BoxLayout.X_AXIS));
        jumlahPanel.setOpaque(false);
        JButton minJumlahBtn = StyledButton.create("Min", 12, 50, 28);
        JButton minusJumlahBtn = StyledButton.create("-", 12, 40, 28);
        JButton plusJumlahBtn = StyledButton.create("+", 12, 40, 28);
        JButton maxJumlahBtn = StyledButton.create("Max", 12, 50, 28);
        jumlahField = new JTextField();
        jumlahField.setMaximumSize(new Dimension(80, 30));
        jumlahField.setHorizontalAlignment(JTextField.CENTER);
        jumlahPanel.add(minJumlahBtn);
        jumlahPanel.add(Box.createHorizontalStrut(4));
        jumlahPanel.add(minusJumlahBtn);
        jumlahPanel.add(Box.createHorizontalStrut(4));
        jumlahPanel.add(jumlahField);
        jumlahPanel.add(Box.createHorizontalStrut(4));
        jumlahPanel.add(plusJumlahBtn);
        jumlahPanel.add(Box.createHorizontalStrut(4));
        jumlahPanel.add(maxJumlahBtn);
        centerPanel.add(jumlahPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        JLabel hargaLabel = new JLabel("Harga Jual:");
        hargaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(hargaLabel);
        JPanel hargaPanel = new JPanel();
        hargaPanel.setLayout(new BoxLayout(hargaPanel, BoxLayout.X_AXIS));
        hargaPanel.setOpaque(false);
        JButton minHargaBtn = StyledButton.create("Min", 12, 50, 28);
        JButton minusHargaBtn = StyledButton.create("-", 12, 40, 28);
        JButton plusHargaBtn = StyledButton.create("+", 12, 40, 28);
        JButton maxHargaBtn = StyledButton.create("Max", 12, 50, 28);
        hargaField = new JTextField();
        hargaField.setMaximumSize(new Dimension(80, 30));
        hargaField.setHorizontalAlignment(JTextField.CENTER);
        hargaPanel.add(minHargaBtn);
        hargaPanel.add(Box.createHorizontalStrut(4));
        hargaPanel.add(minusHargaBtn);
        hargaPanel.add(Box.createHorizontalStrut(4));
        hargaPanel.add(hargaField);
        hargaPanel.add(Box.createHorizontalStrut(4));
        hargaPanel.add(plusHargaBtn);
        hargaPanel.add(Box.createHorizontalStrut(4));
        hargaPanel.add(maxHargaBtn);
        centerPanel.add(hargaPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        JButton setHargaBtn = StyledButton.create("Set Harga Jual", 13, 140, 32);
        setHargaBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(setHargaBtn);
        centerPanel.add(Box.createVerticalStrut(10));

        lblGerobakInfo = new JLabel();
        lblGerobakInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblGerobakInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblGerobakInfo);

        centerPanel.add(Box.createVerticalGlue());
        setHargaBtn.addActionListener(_ -> setHargaBarangGerobak());
        mainPanel.add(centerPanel);

        minJumlahBtn.addActionListener(_ -> {
            int min = 1;
            jumlahField.setText(String.valueOf(min));
        });
        maxJumlahBtn.addActionListener(_ -> {
            int max = 1;
            int selectedRow = gerobakNoPriceTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    max = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 4).toString());
                } catch (Exception ignored) {
                }
            }
            jumlahField.setText(String.valueOf(max));
        });
        minusJumlahBtn.addActionListener(_ -> {
            try {
                int val = Integer.parseInt(jumlahField.getText().trim());
                int min = 1;
                if (val > min)
                    jumlahField.setText(String.valueOf(val - 1));
            } catch (Exception ignored) {
            }
        });
        plusJumlahBtn.addActionListener(_ -> {
            int max = 1;
            int selectedRow = gerobakNoPriceTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    max = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 4).toString());
                } catch (Exception ignored) {
                }
            }
            try {
                int val = Integer.parseInt(jumlahField.getText().trim());
                if (val < max)
                    jumlahField.setText(String.valueOf(val + 1));
            } catch (Exception ignored) {
            }
        });
        minHargaBtn.addActionListener(_ -> {
            int min = 1;
            int selectedRow = gerobakNoPriceTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    String namaBarang = gerobakNoPriceTable.getValueAt(selectedRow, 1).toString();
                    String kategori = gerobakNoPriceTable.getValueAt(selectedRow, 2).toString();
                    int kesegaran = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 3).toString());
                    for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
                        if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori)
                                && b.getKesegaran() == kesegaran) {
                            min = b.getHargaBeli();
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            hargaField.setText(String.valueOf(min));
        });
        maxHargaBtn.addActionListener(_ -> {
            int max = 999999;
            int selectedRow = gerobakNoPriceTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    String namaBarang = gerobakNoPriceTable.getValueAt(selectedRow, 1).toString();
                    String kategori = gerobakNoPriceTable.getValueAt(selectedRow, 2).toString();
                    int kesegaran = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 3).toString());
                    for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
                        if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori)
                                && b.getKesegaran() == kesegaran) {
                            max = b.getHargaBeli() * 3;
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            hargaField.setText(String.valueOf(max));
        });
        minusHargaBtn.addActionListener(_ -> {
            int min = 1;
            int selectedRow = gerobakNoPriceTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    String namaBarang = gerobakNoPriceTable.getValueAt(selectedRow, 1).toString();
                    String kategori = gerobakNoPriceTable.getValueAt(selectedRow, 2).toString();
                    int kesegaran = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 3).toString());
                    for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
                        if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori)
                                && b.getKesegaran() == kesegaran) {
                            min = b.getHargaBeli();
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            try {
                int val = Integer.parseInt(hargaField.getText().trim());
                if (val > min)
                    hargaField.setText(String.valueOf(val - 1));
            } catch (Exception ignored) {
            }
        });
        plusHargaBtn.addActionListener(_ -> {
            int max = 999999;
            int selectedRow = gerobakNoPriceTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    String namaBarang = gerobakNoPriceTable.getValueAt(selectedRow, 1).toString();
                    String kategori = gerobakNoPriceTable.getValueAt(selectedRow, 2).toString();
                    int kesegaran = Integer.parseInt(gerobakNoPriceTable.getValueAt(selectedRow, 3).toString());
                    for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
                        if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori)
                                && b.getKesegaran() == kesegaran) {
                            max = b.getHargaBeli() * 3;
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            try {
                int val = Integer.parseInt(hargaField.getText().trim());
                if (val < max)
                    hargaField.setText(String.valueOf(val + 1));
            } catch (Exception ignored) {
            }
        });
        minJumlahBtn.setFocusable(false);
        maxJumlahBtn.setFocusable(false);
        minusJumlahBtn.setFocusable(false);
        plusJumlahBtn.setFocusable(false);
        minHargaBtn.setFocusable(false);
        maxHargaBtn.setFocusable(false);
        minusHargaBtn.setFocusable(false);
        plusHargaBtn.setFocusable(false);

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

    private JPanel createItemGerobakTab() {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(new Color(255, 248, 220));

        itemGerobakTable = new JTable();
        itemGerobakTable.getTableHeader().setBackground(new Color(212, 175, 55));
        itemGerobakTable.getTableHeader().setForeground(new Color(60, 40, 10));
        itemGerobakTable.setBackground(new Color(255, 255, 240));
        itemGerobakTable.setForeground(new Color(60, 40, 10));

        JScrollPane itemScroll = new JScrollPane(itemGerobakTable);
        itemScroll.getViewport().setBackground(new Color(255, 255, 240));
        itemScroll.setBorder(BorderFactory.createTitledBorder("Item di Gerobak"));
        itemPanel.add(itemScroll, BorderLayout.CENTER);

        JPanel itemButtonPanel = new JPanel(new BorderLayout());
        itemButtonPanel.setBackground(new Color(255, 248, 220));

        JButton btnMoveItemBack = StyledButton.create("Move to Inventory", 13, 160, 32);
        btnMoveItemBack.addActionListener(_ -> moveItemFromGerobakToInventory());

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

    private String getItemEffectDescription(Item item) {
        if (item.isHipnotis()) {
            return "Meningkatkan peluang pembeli langsung membeli tanpa menawar (" +
                    String.format("%.0f%% chance langsung beli", item.getHipnotisChance() * 100) + ")";
        } else if (item.isJampi()) {
            return "Melipatgandakan penghasilan dari transaksi hari ini (" +
                    String.format("%.1fx multiplier penghasilan", item.getJampiMultiplier()) + ")";
        } else if (item.isSemproten()) {
            return "Menambah kesan barang lebih fresh, harga bisa ditawar lebih mahal (" +
                    String.format("+%.0f%% harga jual", item.getSemprotenPriceBoost() * 100) + ")";
        } else if (item.isTip()) {
            return "Pembeli kadang memberi uang ekstra (" +
                    String.format("%.0f%% chance bonus tip", item.getTipBonusRate() * 100) + ")";
        } else if (item.isPeluit()) {
            return "Memanggil pembeli tambahan secara instan (" +
                    String.format("+%d pembeli tambahan", item.getPeluitExtraBuyers()) + ")";
        }
        return "Efek tidak diketahui";
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
            ImageIcon icon = GamePanel.getIcon(item.getIconPath(), 32, 32);
            if (icon == null) {
                icon = GamePanel.getIcon(item.getNama().toLowerCase().replace(' ', '_'), 32, 32);
            }

            data[i][0] = icon;
            data[i][1] = item.getNama();
            data[i][2] = "Level " + item.getLevel();
            data[i][3] = getItemEffectPercentage(item);
            data[i][4] = item.isActive() ? "Aktif" : "Non-aktif";
            data[i][5] = getItemEffectDescription(item);
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

        itemGerobakTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 5; i++) {
            itemGerobakTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        itemGerobakTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);

        if (gerobakFrame != null && gerobakFrame.isVisible()) {
            updateItemInfoLabel(itemsDiGerobak.size());
        }
    }

    private void updateItemInfoLabel(int itemCount) {
        JTabbedPane gerobakTabs = null;
        for (Component c : gerobakFrame.getContentPane().getComponents()) {
            if (c instanceof JTabbedPane) {
                gerobakTabs = (JTabbedPane) c;
                break;
            }
        }

        if (gerobakTabs != null && gerobakTabs.getTabCount() > 1) {
            Component itemTab = gerobakTabs.getComponentAt(1);
            if (itemTab instanceof JPanel) {
                findAndUpdateLabel((JPanel) itemTab, "Total item di Gerobak: " + itemCount);
            }
        }
    }

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

    private void moveItemFromGerobakToInventory() {
        int selectedRow = itemGerobakTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaItem = itemGerobakTable.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah anda yakin ingin memindahkan item \"" + namaItem + "\" kembali ke Inventory?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (inventory.undoBawaItem(namaItem)) {
                JOptionPane.showMessageDialog(this,
                        "Item berhasil dipindahkan kembali ke Inventory.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

                refreshInventoryAndGerobak();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal memindahkan item.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshInventoryAndGerobak() {
        System.out.println("Debug: refreshInventoryAndGerobak() called");

        updateGoodsTable(currentSortBy, currentSortOrder);
        updateGerobakTables();
        updateItemGerobakTable();

        if (goodsTable != null) {
            DefaultTableModel model = (DefaultTableModel) goodsTable.getModel();
            model.fireTableDataChanged();
            goodsTable.revalidate();
            goodsTable.repaint();
            System.out.println("Debug: goodsTable refreshed and model fired");
        }

        if (gerobakNoPriceTable != null) {
            DefaultTableModel model = (DefaultTableModel) gerobakNoPriceTable.getModel();
            model.fireTableDataChanged();
            gerobakNoPriceTable.revalidate();
            gerobakNoPriceTable.repaint();
            System.out.println("Debug: gerobakNoPriceTable refreshed and model fired");
        }

        if (gerobakWithPriceTable != null) {
            DefaultTableModel model = (DefaultTableModel) gerobakWithPriceTable.getModel();
            model.fireTableDataChanged();
            gerobakWithPriceTable.revalidate();
            gerobakWithPriceTable.repaint();
            System.out.println("Debug: gerobakWithPriceTable refreshed and model fired");
        }

        if (inventoryFrame != null && inventoryFrame.isVisible()) {
            System.out.println("Debug: Inventory frame is open, updating items table");
            JTabbedPane tabPane = null;
            for (Component c : inventoryFrame.getContentPane().getComponents()) {
                if (c instanceof JTabbedPane) {
                    tabPane = (JTabbedPane) c;
                    break;
                }
            }

            if (tabPane != null && tabPane.getTabCount() > 1) {
                Component itemsTab = tabPane.getComponentAt(1);
                if (itemsTab instanceof JPanel) {
                    JTable itemsTable = null;
                    JLabel countLabel = null;
                    JComboBox<?> filterCombo = null;

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
                        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
                        model.fireTableDataChanged();
                        itemsTable.revalidate();
                        itemsTable.repaint();
                        System.out.println("Debug: items table in inventory frame refreshed and model fired");
                    }
                }
            }
        }

        System.out.println("Debug: refreshInventoryAndGerobak() completed");
    }

    private void moveItemToGerobak(JTable itemsTable) {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaItem = itemsTable.getValueAt(selectedRow, 1).toString();
        int kapasitasItem = 0;

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

        Gerobak inventoryGerobak = inventory.getGerobak();
        if (inventoryGerobak != null) {
            kapasitasItem = inventoryGerobak.getKapasitasItem();
        }

        int totalItemDiGerobak = inventory.getJumlahItemDiGerobak();

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
        }
        System.out.println("Debug: Moving item " + namaItem);
        System.out.println("Debug: Current items in gerobak: " + totalItemDiGerobak);
        System.out.println("Debug: Gerobak capacity: " + kapasitasItem);
        System.out.println("Debug: Using inventory.gerobak: " + (inventoryGerobak != null ? "exists" : "null"));

        try {
            boolean success = inventory.bawaItem(namaItem, kapasitasItem);
            if (success) {
                JOptionPane.showMessageDialog(this, "Item berhasil dipindahkan ke Gerobak.", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

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

        Barang barangTarget = null;
        for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
            if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori) &&
                    b.getKesegaran() == kesegaran && inventory.getHargaJual(b) <= 0) {
                barangTarget = b;
                break;
            }
        }
        if (barangTarget != null) {
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

        Barang barangWithPrice = null;
        for (Barang b : inventory.getBarangDibawaMutable().keySet()) {
            if (b.getNamaBarang().equals(namaBarang) && b.getKategori().equals(kategori) &&
                    b.getKesegaran() == kesegaran && inventory.getHargaJual(b) > 0) {
                barangWithPrice = b;
                break;
            }
        }

        if (barangWithPrice != null) {
            inventory.setHargaJual(barangWithPrice, 0);

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

    private void mergeItemsWithSamePropertiesAndPrice() {
        Map<Barang, Integer> barangDibawa = inventory.getBarangDibawaMutable();
        Map<String, List<Barang>> groupedItems = new HashMap<>();

        for (Barang b : barangDibawa.keySet()) {
            String key = b.getNamaBarang() + "|" + b.getKategori() + "|" +
                    b.getKesegaran() + "|" + b.getHargaBeli() + "|" +
                    inventory.getHargaJual(b);
            groupedItems.computeIfAbsent(key, _ -> new ArrayList<>()).add(b);
        }

        for (List<Barang> similarItems : groupedItems.values()) {
            if (similarItems.size() > 1) {
                int totalQuantity = 0;
                for (Barang item : similarItems) {
                    totalQuantity += barangDibawa.get(item);
                }

                Barang keepItem = similarItems.get(0);

                for (int i = 1; i < similarItems.size(); i++) {
                    Barang removeItem = similarItems.get(i);
                    barangDibawa.remove(removeItem);
                    if (inventory.getHargaJual(removeItem) > 0) {
                        inventory.setHargaJual(removeItem, 0);
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
    }

    private void updateItemsTable(JTable itemsTable, JLabel itemCountLabel, int filterMode) {
        if (inventory == null)
            return;

        List<Item> allItems = inventory.getStokItem();
        List<Item> filteredItems = allItems;

        String[] cols = { "Icon", "Nama", "Level", "Chance", "Status", "Deskripsi" };
        Object[][] data = new Object[filteredItems.size()][cols.length];
        for (int i = 0; i < filteredItems.size(); i++) {
            Item item = filteredItems.get(i);
            ImageIcon icon = GamePanel.getIcon(item.getIconPath(), 32, 32);
            if (icon == null) {
                icon = GamePanel.getIcon(item.getNama().toLowerCase().replace(' ', '_'), 32, 32);
            }
            data[i][0] = icon;
            data[i][1] = item.getNama();
            data[i][2] = "Level " + item.getLevel();
            data[i][3] = getItemEffectPercentage(item);
            data[i][4] = item.isActive() ? "Aktif" : "Non-aktif";
            data[i][5] = getItemEffectDescription(item);
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

        itemsTable.getColumnModel().getColumn(0).setCellRenderer((_, value, _, _, _, _) -> {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setIcon(value instanceof Icon ? (Icon) value : null);
            return label;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 5; i++) {
            itemsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);

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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah anda yakin ingin menghapus item \"" + namaItem + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (inventory.hapusItem(namaItem)) {
                JOptionPane.showMessageDialog(this,
                        "Item \"" + namaItem + "\" berhasil dihapus.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

                JComboBox<?> filterCombo = null;
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

                int filterIndex = filterCombo != null ? filterCombo.getSelectedIndex() : 0;

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
        if (this.inventory != null) {
            this.inventory.removeInventoryChangeListener(this);
        }

        this.inventory = inventory;

        if (inventory != null) {
            inventory.addInventoryChangeListener(this);
            System.out.println("Debug: InventoryChangeListener added to inventory");
        }
    }

    public void setBackToGameCallback(Runnable callback) {
        this.backToGameCallback = callback;
    }

    public void setOnSleepCallback(Runnable callback) {
        this.onSleepCallback = callback;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void onPanelShown() {
        System.out.println("HomeBasePanel: onPanelShown() called - starting HomeBase BGM");
        BGMPlayer.getInstance().stopMapBGM();
        BGMPlayer.getInstance().playHomeBaseBGM();
    }

    public void onPanelHidden() {
        System.out.println("HomeBasePanel: onPanelHidden() called - stopping HomeBase BGM and starting Map BGM");

        if (this.inventory != null && this.player != null) {
            this.player.setInventory(this.inventory);

            System.out.println("Debug: Items in gerobak when leaving HomeBase: " +
                    this.inventory.getItemDibawa().size());
            for (Item item : this.inventory.getItemDibawa()) {
                System.out.println("Debug: Keeping item in gerobak: " + item.getNama());
            }
        }

        BGMPlayer.getInstance().stopHomeBaseBGM();
        BGMPlayer.getInstance().playMapBGM();
    }

    public void updatePlayerData(Player newPlayer) {
        this.player = newPlayer;
        System.out.println("HomeBasePanel: Updated player data - Username: " + newPlayer.getUsername() +
                ", Money: " + newPlayer.getMoney());

        updatePerksFrameContent();

        SwingUtilities.invokeLater(() -> {
            System.out.println("HomeBasePanel: Forcing UI refresh after player data update");
            refreshInventoryAndGerobak();

            if (inventoryFrame != null && inventoryFrame.isVisible()) {
                updateGoodsTable(currentSortBy, currentSortOrder);
                updateItemsTableInCurrentTab();
            }

            if (gerobakFrame != null && gerobakFrame.isVisible()) {
                updateGerobakTables();
                updateItemGerobakTable();
            }

            if (perksFrame != null && perksFrame.isVisible()) {
                updatePerksFrameContent();
            }
        });
    }

    private JPanel createGerobakHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 248, 220));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

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

    private void upgradeGerobak(Gerobak gerobak, int biayaUpgrade) {
        if (player == null)
            return;

        if (gerobak.isMaxLevel()) {
            JOptionPane.showMessageDialog(this,
                    "Gerobak sudah mencapai level maksimal!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int actualUpgradeCost = gerobak.getBiayaUpgrade();

        if (player.getMoney() < actualUpgradeCost) {
            JOptionPane.showMessageDialog(this,
                    "Uang tidak cukup untuk upgrade!\nBiaya: " + actualUpgradeCost + "G\nUang Anda: "
                            + player.getMoney() + "G",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean upgradeSuccess = gerobak.upgradeLevel();
        if (upgradeSuccess) {
            player.kurangiMoney(actualUpgradeCost);

            if (inventory != null) {
                inventory.setGerobak(gerobak);
            }

            boolean wasGerobakFrameVisible = false;
            if (gerobakFrame != null && gerobakFrame.isVisible()) {
                wasGerobakFrameVisible = true;
                gerobakFrame.dispose();
                gerobakFrame = null;
            }

            refreshInventoryAndGerobak();

            if (wasGerobakFrameVisible) {
                showGerobakFrame();
            }

            JOptionPane.showMessageDialog(this,
                    "Gerobak berhasil di-upgrade ke Level " + gerobak.getLevel() + "!\n" +
                            "Kapasitas Barang: " + gerobak.getKapasitasBarang() + "\n" +
                            "Kapasitas Item: " + gerobak.getKapasitasItem(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

            if (backToGameCallback != null) {
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

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(255, 248, 220));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            JLabel titleLabel = new JLabel("Perks yang Dimiliki", JLabel.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
            titleLabel.setForeground(new Color(120, 90, 30));
            headerPanel.add(titleLabel, BorderLayout.CENTER);

            JLabel moneyLabel = new JLabel("Uang: " + player.getMoney() + "G", JLabel.RIGHT);
            moneyLabel.setFont(new Font("Serif", Font.PLAIN, 18));
            moneyLabel.setForeground(new Color(120, 90, 30));
            headerPanel.add(moneyLabel, BorderLayout.EAST);

            perksFrame.add(headerPanel, BorderLayout.NORTH);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(255, 248, 220));

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

            perksFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    perksFrame = null;
                }
            });

            desktopPane.add(perksFrame);
        }

        if (perksFrame.getContentPane().getComponentCount() > 0) {
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

        perksTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        perksTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        perksTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        perksTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        perksTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        perksTable.getColumnModel().getColumn(5).setPreferredWidth(300);

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

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        perksTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);
    }

    private void updatePerksFrameContent() {
        if (perksFrame == null || !perksFrame.isVisible())
            return;

        updateMoneyInPerksFrame(perksFrame.getContentPane());

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

    private void showStatsFrame() {
        if (statsFrame != null && statsFrame.isVisible()) {
            statsFrame.toFront();
            return;
        }
        statsFrame = new JInternalFrame("Stats", true, true, true, true);
        statsFrame.setSize(520, 420);
        statsFrame.setLayout(new BorderLayout());
        statsFrame.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(6, 6, 16, 16, new Color(120, 90, 30, 180)),
                BorderFactory.createLineBorder(new Color(212, 175, 55), 4)));
        statsFrame.setOpaque(true);
        statsFrame.getContentPane().setBackground(new Color(255, 248, 220));

        JLabel title = new JLabel("Statistik Pemain", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        statsFrame.add(title, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        String username = player != null ? player.getUsername() : "-";
        int uang = player != null ? player.getMoney() : 0;
        int totalBarang = inventory != null ? inventory.getJumlahBarang() : 0;
        int totalItem = inventory != null ? inventory.getStokItem().size() : 0;
        int totalPerk = player != null ? player.getSemuaPerkDimiliki().size() : 0;
        int gerobakLevel = (inventory != null && inventory.getGerobak() != null) ? inventory.getGerobak().getLevel()
                : 0;

        statsPanel.add(makeStatsLabel("Nama Pemain:", username));
        statsPanel.add(makeStatsLabel("Uang:", uang + "G"));
        statsPanel.add(makeStatsLabel("Total Barang di Inventory:", String.valueOf(totalBarang)));
        statsPanel.add(makeStatsLabel("Total Item Dimiliki:", String.valueOf(totalItem)));
        statsPanel.add(makeStatsLabel("Total Perk Dimiliki:", String.valueOf(totalPerk)));
        statsPanel.add(makeStatsLabel("Level Gerobak:", String.valueOf(gerobakLevel)));

        statsPanel.add(Box.createVerticalStrut(20));

        JLabel quote = new JLabel("\"Jadilah pedagang terhebat di sekai!\"", JLabel.CENTER);
        quote.setFont(new Font("Serif", Font.ITALIC, 18));
        quote.setForeground(new Color(120, 90, 30));
        statsPanel.add(quote);

        statsFrame.add(statsPanel, BorderLayout.CENTER);

        JButton closeBtn = StyledButton.create("Tutup", 16, 100, 36);
        closeBtn.addActionListener(_ -> statsFrame.dispose());
        JPanel closePanel = new JPanel();
        closePanel.setOpaque(false);
        closePanel.add(closeBtn);
        statsFrame.add(closePanel, BorderLayout.SOUTH);

        statsFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
                statsFrame = null;
            }
        });
        desktopPane.add(statsFrame);
        statsFrame.setVisible(true);
        statsFrame.toFront();
    }

    private JPanel makeStatsLabel(String label, String value) {
        JLabel l = new JLabel(label + "  ", JLabel.LEFT);
        l.setFont(new Font("Serif", Font.BOLD, 20));
        JLabel v = new JLabel(value, JLabel.RIGHT);
        v.setFont(new Font("Serif", Font.PLAIN, 20));
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        p.setMaximumSize(new Dimension(400, 32));
        return p;
    }      private void sleepAndAdvanceDay() {
        if (gamePanel != null) {
            gamePanel.advanceDay();
            this.currentDay = gamePanel.getCurrentDay();
        }
        if (onSleepCallback != null)
            onSleepCallback.run();
        JDialog sleepDialog = createMedievalSleepDialog();
        sleepDialog.setVisible(true);
    }

    private JDialog createMedievalSleepDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sleep", true);
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        Color parchment = new Color(241, 233, 210);
        Color border = new Color(120, 90, 30);
        dialog.getContentPane().setBackground(parchment);
        JLabel dialogDayLabel = new JLabel("Day " + currentDay, JLabel.CENTER);
        dialogDayLabel.setFont(new Font("Serif", Font.BOLD, 36));
        dialogDayLabel.setForeground(new Color(120, 90, 30));
        dialogDayLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        dialog.add(dialogDayLabel, BorderLayout.NORTH);

        JLabel infoLabel = new JLabel("<html><center>Apakah Anda ingin tidur dan melanjutkan ke hari berikutnya?<br>" +
                "Kota akan diperbarui dan kesegaran barang akan berkurang.</center></html>", JLabel.CENTER);
        infoLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        infoLabel.setForeground(border);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        dialog.add(infoLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(parchment);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 32, 20));

        JButton sleepButton = new JButton("💤 Tidur");
        sleepButton.setFont(new Font("Serif", Font.BOLD, 20));
        sleepButton.setBackground(new Color(120, 180, 120));
        sleepButton.setForeground(Color.WHITE);
        sleepButton.setFocusPainted(false);
        sleepButton.setPreferredSize(new Dimension(140, 48));          
        sleepButton.addActionListener(_ -> {
            updateDayLabel();
            player.setHasSlept(true);
            player.setDailyPerkChanceUsed(false); // Reset daily perk chance when sleeping
            System.out.println("Player sleeps. Day advanced to: " + currentDay);
            if (inventory != null) {
                inventory.kurangiKesegaranSemua();
                System.out.println("Debug: Freshness reduced for all items");
            }

            if (itemEffectManager != null) {
                itemEffectManager.resetDailyEffects();
                System.out.println("Debug: Daily item effects reset for new day");
            }

            if (supplier != null) {
                supplier.generateStokHariIni();
                System.out.println("Debug: Supplier stock regenerated for day " + currentDay);
            }

            if (onSleepCallback != null) {
                onSleepCallback.run();
            }

            SwingUtilities.invokeLater(() -> {
                System.out.println("Debug: Starting UI refresh after sleep");

                refreshInventoryAndGerobak();

                if (goodsTable != null) {
                    ((DefaultTableModel) goodsTable.getModel()).fireTableDataChanged();
                    goodsTable.revalidate();
                    goodsTable.repaint();
                    System.out.println("Debug: goodsTable force updated");
                }

                if (gerobakNoPriceTable != null) {
                    ((DefaultTableModel) gerobakNoPriceTable.getModel()).fireTableDataChanged();
                    gerobakNoPriceTable.revalidate();
                    gerobakNoPriceTable.repaint();
                    System.out.println("Debug: gerobakNoPriceTable force updated");
                }

                if (gerobakWithPriceTable != null) {
                    ((DefaultTableModel) gerobakWithPriceTable.getModel()).fireTableDataChanged();
                    gerobakWithPriceTable.revalidate();
                    gerobakWithPriceTable.repaint();
                    System.out.println("Debug: gerobakWithPriceTable force updated");
                }

                if (inventoryFrame != null && inventoryFrame.isVisible()) {
                    updateGoodsTable(currentSortBy, currentSortOrder);
                    updateItemsTableInCurrentTab();
                    System.out.println("Debug: inventory frame tables updated");
                }

                System.out.println("Debug: UI refresh completed, showing success message");

                JOptionPane.showMessageDialog(this,
                        "Hari baru telah dimulai!\n" +
                                "• Kota telah diperbarui dengan pembeli baru\n" +
                                "• Kesegaran semua barang telah berkurang\n" +
                                "• Hari ke-" + currentDay + " dimulai",
                        "Tidur Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            dialog.dispose();
        });
        buttonPanel.add(sleepButton);

        JButton stayAwakeButton = new JButton("✖ Tetap Terjaga");
        stayAwakeButton.setFont(new Font("Serif", Font.BOLD, 20));
        stayAwakeButton.setBackground(new Color(180, 120, 60));
        stayAwakeButton.setForeground(Color.WHITE);
        stayAwakeButton.setFocusPainted(false);
        stayAwakeButton.setPreferredSize(new Dimension(180, 48));
        stayAwakeButton.addActionListener(_ -> {
            System.out.println("Player stays awake. Day: " + currentDay);
            dialog.dispose();
        });
        buttonPanel.add(stayAwakeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        return dialog;
    }
}
