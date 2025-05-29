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

  public TokoItemPanel() {
    this(new TokoItem(new Player()), new Player());
  }

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

    // Create tabbed pane for Buy and Upgrade menus
    tabbedPane = new JTabbedPane();
    tabbedPane.setFont(new Font("Serif", Font.BOLD, 18));

    // Buy panel
    buyItemsPanel = new JPanel();
    buyItemsPanel.setLayout(new BoxLayout(buyItemsPanel, BoxLayout.Y_AXIS));
    buyItemsPanel.setOpaque(false);

    buyScrollPane = new JScrollPane(buyItemsPanel);
    buyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    buyScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

    // Upgrade panel
    upgradeItemsPanel = new JPanel();
    upgradeItemsPanel.setLayout(new BoxLayout(upgradeItemsPanel, BoxLayout.Y_AXIS));
    upgradeItemsPanel.setOpaque(false);

    upgradeScrollPane = new JScrollPane(upgradeItemsPanel);
    upgradeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    upgradeScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

    // Add tabs
    tabbedPane.addTab("Beli Item", buyScrollPane);
    tabbedPane.addTab("Upgrade Item", upgradeScrollPane);

    add(tabbedPane, BorderLayout.CENTER);

    // Populate the panels
    populateBuyItems();
    populateUpgradeItems();

    JButton backButton = StyledButton.create("Kembali", 20, 120, 40);
    backButton.addActionListener(e -> {
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

      Image icon = new ImageIcon("assets/icons/" + item.getIconPath()).getImage().getScaledInstance(40, 40,
          Image.SCALE_SMOOTH);
      ImageIcon scaledIcon = new ImageIcon(icon);
      JLabel nameLabel = new JLabel(item.getNama(), scaledIcon, JLabel.LEFT);
      nameLabel.setFont(new Font("Serif", Font.PLAIN, 22));
      gbc.gridx = 0;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.weightx = 1;
      itemRow.add(nameLabel, gbc);

      JLabel hargaLabel = new JLabel(item.getHarga() + "G");
      hargaLabel.setFont(new Font("Serif", Font.PLAIN, 22));
      hargaLabel.setPreferredSize(new Dimension(90, 30));
      gbc.gridx = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.weightx = 0;
      itemRow.add(hargaLabel, gbc);

      gbc.anchor = GridBagConstraints.EAST;
      JButton minusBtn = StyledButton.create("-", 18, 40, 30);
      gbc.gridx = 2;
      itemRow.add(minusBtn, gbc);

      JTextField qtyField = new JTextField("1", 2);
      qtyField.setHorizontalAlignment(JTextField.CENTER);
      qtyField.setFont(new Font("Serif", Font.PLAIN, 20));
      qtyField.setPreferredSize(new Dimension(40, 30));
      gbc.gridx = 3;
      itemRow.add(qtyField, gbc);

      JButton plusBtn = StyledButton.create("+", 18, 40, 30);
      gbc.gridx = 4;
      itemRow.add(plusBtn, gbc);

      JLabel totalLabel = new JLabel("= " + item.getHarga() + "G");
      totalLabel.setFont(new Font("Serif", Font.BOLD, 22));
      totalLabel.setPreferredSize(new Dimension(110, 30));
      gbc.gridx = 5;
      itemRow.add(totalLabel, gbc);

      JButton buyButton = StyledButton.create("Beli", 18, 70, 30);
      gbc.gridx = 6;
      itemRow.add(buyButton, gbc);

      minusBtn.addActionListener(e -> {
        int qty = Integer.parseInt(qtyField.getText());
        if (qty > 1) {
          qty--;
        }
        qtyField.setText(String.valueOf(qty));
        totalLabel.setText("= " + (item.getHarga() * qty) + "G");
      });

      plusBtn.addActionListener(e -> {
        int qty = Integer.parseInt(qtyField.getText()) + 1;
        qtyField.setText(String.valueOf(qty));
        totalLabel.setText("= " + (item.getHarga() * qty) + "G");
      });

      qtyField.addActionListener(e -> {
        int qty;
        try {
          qty = Math.max(1, Integer.parseInt(qtyField.getText()));
        } catch (NumberFormatException ex) {
          qty = 1;
        }
        qtyField.setText(String.valueOf(qty));
        totalLabel.setText("= " + (item.getHarga() * qty) + "G");
      });

      buyButton.addActionListener(e -> {
        int qty;
        try {
          qty = Math.max(1, Integer.parseInt(qtyField.getText()));
        } catch (NumberFormatException ex) {
          qty = 1;
        }
        int totalHarga = item.getHarga() * qty;
        if (player.getMoney() < totalHarga) {
          JOptionPane.showMessageDialog(this, "Uang tidak cukup untuk membeli " + item.getNama() + " x" + qty + ".",
              "Gagal", JOptionPane.ERROR_MESSAGE);
          return;
        }
        boolean success = true;
        for (int i = 0; i < qty; i++) {
          if (!toko.beliItem(player, item.getNama())) {
            success = false;
            break;
          } else {
            if (inventory != null) {
              inventory.tambahItem(item); // Tambahkan item ke inventory
            }
          }
        }
        String msg = success ? "Berhasil membeli " + item.getNama() + " x" + qty + "!"
            : "Gagal membeli " + item.getNama() + ".";
        JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal",
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        moneyLabel.setText("Uang: " + player.getMoney() + "G");
        if (updateInventoryCallback != null) {
          updateInventoryCallback.run();
        }
      });

      itemRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      buyItemsPanel.add(itemRow);
    }
    buyItemsPanel.revalidate();
    buyItemsPanel.repaint();
  }

  private void populateUpgradeItems() {
    upgradeItemsPanel.removeAll();

    // Change getAllItems() to getStokItem() to match the Inventory class
    if (inventory == null || inventory.getStokItem().isEmpty()) {
      JPanel messagePanel = new JPanel();
      messagePanel.setOpaque(false);
      JLabel noItemsLabel = new JLabel("Tidak ada item yang dapat di-upgrade");
      noItemsLabel.setFont(new Font("Serif", Font.ITALIC, 22));
      messagePanel.add(noItemsLabel);
      upgradeItemsPanel.add(messagePanel);
    } else {
      // Change getAllItems() to getStokItem() here as well
      for (Item item : inventory.getStokItem()) {
        // This is just a placeholder implementation - real implementation would depend
        // on your upgrade system
        JPanel itemRow = new JPanel(new GridBagLayout());
        itemRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;

        // Item icon and name
        Image icon = new ImageIcon("assets/icons/" + item.getIconPath()).getImage().getScaledInstance(40, 40,
            Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(icon);
        JLabel nameLabel = new JLabel(item.getNama(), scaledIcon, JLabel.LEFT);
        nameLabel.setFont(new Font("Serif", Font.PLAIN, 22));
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        itemRow.add(nameLabel, gbc);

        // Current level
        JLabel levelLabel = new JLabel("Level: 1"); // Placeholder - get real level
        levelLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        itemRow.add(levelLabel, gbc);

        // Upgrade cost
        int upgradeCost = item.getHarga() * 2; // Placeholder - calculate real cost
        JLabel costLabel = new JLabel("Biaya: " + upgradeCost + "G");
        costLabel.setFont(new Font("Serif", Font.BOLD, 18));
        costLabel.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 2;
        itemRow.add(costLabel, gbc);

        // Upgrade button
        JButton upgradeButton = StyledButton.create("Upgrade", 18, 100, 30);
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.EAST;
        itemRow.add(upgradeButton, gbc);

        upgradeButton.addActionListener(e -> {
          if (player.getMoney() < upgradeCost) {
            JOptionPane.showMessageDialog(this, "Uang tidak cukup untuk upgrade " + item.getNama(),
                "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
          }

          // Fix: Change addMoney(-upgradeCost) to kurangiMoney(upgradeCost)
          player.kurangiMoney(upgradeCost);
          JOptionPane.showMessageDialog(this, "Item " + item.getNama() + " berhasil di-upgrade!",
              "Sukses", JOptionPane.INFORMATION_MESSAGE);
          moneyLabel.setText("Uang: " + player.getMoney() + "G");

          // Refresh both panels
          populateBuyItems();
          populateUpgradeItems();

          if (updateInventoryCallback != null) {
            updateInventoryCallback.run();
          }
        });

        itemRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        upgradeItemsPanel.add(itemRow);
      }
    }
    upgradeItemsPanel.revalidate();
    upgradeItemsPanel.repaint();
  }

  public void setBackToGameCallback(Runnable cb) {
    this.backToGameCallback = cb;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
    // Refresh the upgrade panel since it depends on inventory
    populateUpgradeItems();
  }

  public void setUpdateInventoryCallback(Runnable cb) {
    this.updateInventoryCallback = cb;
  }

  public void refresh() {
    moneyLabel.setText("Uang: " + player.getMoney() + "G");
    populateBuyItems();
    populateUpgradeItems();
  }
}
