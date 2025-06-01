/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Inventory;
import model.Item;
import model.Player;
import model.TokoItem;

public class TokoItemPanel extends JPanel {

  private TokoItem toko;
  private Player player;
  private JPanel buyItemsPanel;
  private JPanel upgradeItemsPanel;
  private JTabbedPane tabbedPane;
  private JScrollPane buyScrollPane;
  private JScrollPane upgradeScrollPane;
  private JLabel moneyLabel;
  private Runnable backToGameCallback;
  private Inventory inventory;
  private Runnable updateInventoryCallback;
  private Runnable autoSaveCallback;

  public TokoItemPanel(TokoItem toko, Player player) {
    this.toko = toko;
    this.player = player;
    setLayout(new BorderLayout());
    setOpaque(true);
    setBackground(new Color(245, 222, 179));

    JLabel title = new JLabel("Toko Item", JLabel.CENTER);
    title.setFont(new Font("Serif", Font.BOLD, 32));
    title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
    add(title, BorderLayout.NORTH);

    moneyLabel = new JLabel("Uang: " + player.getMoney() + "G", JLabel.CENTER);
    moneyLabel.setFont(new Font("Serif", Font.PLAIN, 22));

    tabbedPane = new JTabbedPane();
    tabbedPane.setFont(new Font("Serif", Font.BOLD, 18));

    buyItemsPanel = new JPanel();
    buyItemsPanel.setLayout(new BoxLayout(buyItemsPanel, BoxLayout.Y_AXIS));
    buyItemsPanel.setOpaque(false);

    buyScrollPane = new JScrollPane(buyItemsPanel);
    buyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    buyScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

    upgradeItemsPanel = new JPanel();
    upgradeItemsPanel.setLayout(new BoxLayout(upgradeItemsPanel, BoxLayout.Y_AXIS));
    upgradeItemsPanel.setOpaque(false);

    upgradeScrollPane = new JScrollPane(upgradeItemsPanel);
    upgradeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    upgradeScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

    tabbedPane.addTab("Beli Item", buyScrollPane);
    tabbedPane.addTab("Upgrade Item", upgradeScrollPane);

    add(tabbedPane, BorderLayout.CENTER);

    populateBuyItems();
    populateUpgradeItems();

    JButton backButton = StyledButton.create("Kembali", 20, 120, 40);
    backButton.addActionListener(_ -> {
      if (backToGameCallback != null) {
        backToGameCallback.run();
      }
    });

    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setOpaque(false);
    bottomPanel.add(moneyLabel, BorderLayout.CENTER);
    bottomPanel.add(backButton, BorderLayout.EAST);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  private void populateBuyItems() {
    buyItemsPanel.removeAll();
    List<Item> daftarItem = toko.getDaftarItem();
    for (Item item : daftarItem) {
      JPanel itemRow = new JPanel(new GridBagLayout());
      itemRow.setOpaque(false);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(5, 5, 5, 5);
      gbc.gridy = 0;
      ImageIcon scaledIcon = GamePanel.getIcon(item.getIconPath(), 40, 40);
      if (scaledIcon == null) {
        scaledIcon = GamePanel.getIcon(item.getNama().toLowerCase().replace(' ', '_'), 40, 40);
      }
      JLabel nameLabel = new JLabel(item.getNama(), scaledIcon, JLabel.LEFT);
      nameLabel.setFont(new Font("Serif", Font.PLAIN, 22));
      gbc.gridx = 0;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.weightx = 1;
      itemRow.add(nameLabel, gbc);

      JLabel effectLabel = new JLabel(getItemEffectString(item));
      effectLabel.setFont(new Font("Serif", Font.ITALIC, 16));
      effectLabel.setForeground(new Color(80, 60, 20));
      gbc.gridy = 1;
      gbc.gridx = 0;
      gbc.gridwidth = 3;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.weightx = 1;
      itemRow.add(effectLabel, gbc);
      gbc.gridy = 0;
      gbc.gridwidth = 1;

      JLabel hargaLabel = new JLabel(item.getHarga() + "G");
      hargaLabel.setFont(new Font("Serif", Font.BOLD, 22));
      hargaLabel.setPreferredSize(new Dimension(120, 30));
      gbc.gridx = 1;
      gbc.anchor = GridBagConstraints.CENTER;
      gbc.weightx = 0;
      itemRow.add(hargaLabel, gbc);

      JButton buyButton = StyledButton.create("Beli", 18, 70, 30);
      gbc.gridx = 2;
      gbc.anchor = GridBagConstraints.EAST;
      itemRow.add(buyButton, gbc);
      buyButton.addActionListener(_ -> {
        if (player.getMoney() < item.getHarga()) {
          JOptionPane.showMessageDialog(this,
              "Uang tidak cukup untuk membeli " + item.getNama() + ".",
              "Gagal", JOptionPane.ERROR_MESSAGE);
          return;
        }

        boolean success = toko.beliItem(player, item.getNama());

        if (success && inventory != null) {
          inventory.tambahItem(item);
        }

        String msg = success ? "Berhasil membeli " + item.getNama() + "!"
            : "Gagal membeli " + item.getNama() + ".";
        JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal",
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        moneyLabel.setText("Uang: " + player.getMoney() + "G");
        if (updateInventoryCallback != null) {
          updateInventoryCallback.run();
        }

        if (success && autoSaveCallback != null) {
          autoSaveCallback.run();
        }

        populateUpgradeItems();
        tabbedPane.setSelectedIndex(1);
      });

      itemRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      buyItemsPanel.add(itemRow);
    }
    buyItemsPanel.revalidate();
    buyItemsPanel.repaint();
  }

  private void populateUpgradeItems() {
    upgradeItemsPanel.removeAll();

    if (inventory == null || inventory.getStokItem().isEmpty()) {
      JPanel messagePanel = new JPanel();
      messagePanel.setOpaque(false);
      JLabel noItemsLabel = new JLabel("Tidak ada item yang dapat di-upgrade");
      noItemsLabel.setFont(new Font("Serif", Font.ITALIC, 22));
      messagePanel.add(noItemsLabel);
      upgradeItemsPanel.add(messagePanel);
    } else {
      for (Item item : inventory.getStokItem()) {
        JPanel itemRow = new JPanel(new GridBagLayout());
        itemRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.gridy = 0;
        ImageIcon scaledIcon = GamePanel.getIcon(item.getIconPath(), 40, 40);
        if (scaledIcon == null) {
          scaledIcon = GamePanel.getIcon(item.getNama().toLowerCase().replace(' ', '_'), 40, 40);
        }
        JLabel nameLabel = new JLabel(item.getNama(), scaledIcon, JLabel.LEFT);
        nameLabel.setFont(new Font("Serif", Font.PLAIN, 22));
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        itemRow.add(nameLabel, gbc);

        JLabel levelLabel = new JLabel("Level: " + item.getLevel());
        levelLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.2;
        itemRow.add(levelLabel, gbc);

        int biayaUpgrade = item.getBiayaUpgrade();
        JLabel costLabel = new JLabel("Biaya: " + biayaUpgrade + "G");
        costLabel.setFont(new Font("Serif", Font.BOLD, 18));
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.2;
        itemRow.add(costLabel, gbc);

        JButton upgradeButton = StyledButton.create("Upgrade", 18, 90, 30);
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        itemRow.add(upgradeButton, gbc);

        upgradeButton.addActionListener(_ -> {
          int biayaUpgradeLocal = item.getBiayaUpgrade();

          if (item.isMaxLevel()) {
            JOptionPane.showMessageDialog(this,
                "Item " + item.getNama() + " sudah mencapai level maksimum (Level " + item.getMaxLevel() + ").",
                "Gagal", JOptionPane.INFORMATION_MESSAGE);
            return;
          }

          if (player.getMoney() < biayaUpgradeLocal) {
            JOptionPane.showMessageDialog(this,
                "Uang tidak cukup untuk upgrade " + item.getNama() + "\n" +
                    "Biaya upgrade: " + biayaUpgradeLocal + "G",
                "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
          }

          player.kurangiMoney(biayaUpgradeLocal);

          boolean success = item.upgradeLevel();
          if (success) {
            String efekDetail = getItemEffectString(item);
            JOptionPane.showMessageDialog(this,
                "Item " + item.getNama() + " berhasil di-upgrade!\n" +
                    "Level sekarang: " + item.getLevel() + "\n" +
                    efekDetail,
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
          } else {
            player.tambahMoney(biayaUpgradeLocal);
            JOptionPane.showMessageDialog(this,
                "Gagal upgrade item.",
                "Gagal", JOptionPane.ERROR_MESSAGE);
          }

          moneyLabel.setText("Uang: " + player.getMoney() + "G");
          populateBuyItems();
          populateUpgradeItems();

          if (updateInventoryCallback != null) {
            updateInventoryCallback.run();
          }

          if (success && autoSaveCallback != null) {
            autoSaveCallback.run();
          }
        });

        itemRow.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        upgradeItemsPanel.add(itemRow);
      }
    }
    upgradeItemsPanel.revalidate();
    upgradeItemsPanel.repaint();
  }

  private String getItemEffectString(Item item) {
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

  public void setBackToGameCallback(Runnable cb) {
    this.backToGameCallback = cb;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
    populateUpgradeItems();
  }

  public void setUpdateInventoryCallback(Runnable cb) {
    this.updateInventoryCallback = cb;
  }

  public void setAutoSaveCallback(Runnable cb) {
    this.autoSaveCallback = cb;
  }

  public void refresh() {
    moneyLabel.setText("Uang: " + player.getMoney() + "G");
    populateBuyItems();
    populateUpgradeItems();
  }

  public void updatePlayerData(Player newPlayer) {
    this.player = newPlayer;
    this.moneyLabel.setText("Uang: " + player.getMoney() + "G");
    if (this.toko != null) {
      this.toko = new TokoItem(newPlayer);
    }
    refresh();
  }
}
