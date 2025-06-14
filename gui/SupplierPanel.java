/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package gui;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import model.Barang;
import model.Player;
import model.Supplier;

public class SupplierPanel extends JPanel {

    private Supplier supplier;
    private Player player;
    private JPanel itemsPanel;
    private JScrollPane scrollPane;
    private JLabel moneyLabel;
    private Runnable backToGameCallback;
    private Runnable autoSaveCallback;

    public SupplierPanel(Supplier supplier, Player player) {
        this.supplier = supplier;
        this.player = player;
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(245, 222, 179));

        JLabel title = new JLabel("Supplier ", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        moneyLabel = new JLabel("Uang: " + player.getMoney(), JLabel.CENTER);
        moneyLabel.setFont(new Font("Serif", Font.PLAIN, 22));

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        populateItems();

        scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        add(scrollPane, BorderLayout.CENTER);

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

    private void populateItems() {
        itemsPanel.removeAll();
        List<Barang> stok = supplier.getStokHariIni();
        for (Barang barang : stok) {
            JPanel itemRow = new JPanel(new GridBagLayout());
            itemRow.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = 0;
            String formattedName = Arrays.stream(barang.getNamaBarang().toLowerCase().split("_"))
                    .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                    .reduce((a, b) -> a + " " + b).orElse(barang.getNamaBarang());

            ImageIcon scaledIcon = GamePanel.getIcon(barang.getIconPath(), 40, 40);
            if (scaledIcon == null) {
                scaledIcon = new ImageIcon();
            }

            JLabel nameLabel = new JLabel(formattedName, scaledIcon, JLabel.LEFT);
            nameLabel.setFont(new Font("Serif", Font.PLAIN, 22));
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx = 1;
            itemRow.add(nameLabel, gbc);

            JLabel hargaLabel = new JLabel(barang.getHargaBeli() + "G");
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

            JLabel totalLabel = new JLabel("= " + barang.getHargaBeli() + "G");
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
                totalLabel.setText("= " + (barang.getHargaBeli() * qty) + "G");
            });
            plusBtn.addActionListener(e -> {
                int qty = Integer.parseInt(qtyField.getText()) + 1;
                qtyField.setText(String.valueOf(qty));
                totalLabel.setText("= " + (barang.getHargaBeli() * qty) + "G");
            });
            qtyField.addActionListener(e -> {
                int qty;
                try {
                    qty = Math.max(1, Integer.parseInt(qtyField.getText()));
                } catch (NumberFormatException ex) {
                    qty = 1;
                }
                qtyField.setText(String.valueOf(qty));
                totalLabel.setText("= " + (barang.getHargaBeli() * qty) + "G");
            });
            buyButton.addActionListener(e -> {
                int qty;
                try {
                    qty = Math.max(1, Integer.parseInt(qtyField.getText()));
                } catch (NumberFormatException ex) {
                    qty = 1;
                }
                int totalHarga = barang.getHargaBeli() * qty;

                System.out.println("=== DEBUG SUPPLIER PURCHASE START ===");
                System.out.println("Player: " + player.getUsername());
                System.out.println("Buying: " + barang.getNamaBarang() + " x" + qty);
                System.out.println("Price per item: " + barang.getHargaBeli());
                System.out.println("Total price: " + totalHarga);
                System.out.println("Player money before: " + player.getMoney());
                System.out.println("Inventory barang before: " + player.getInventory().getStokBarang().size());

                if (player.getMoney() < totalHarga) {
                    JOptionPane.showMessageDialog(this,
                            "Uang tidak cukup untuk membeli " + formattedName + " x" + qty + ".", "Gagal",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean success = true;
                for (int i = 0; i < qty; i++) {
                    if (!supplier.beli(player, barang.getNamaBarang())) {
                        success = false;
                        break;
                    }
                }

                System.out.println("Purchase success: " + success);
                System.out.println("Player money after: " + player.getMoney());
                System.out.println("Inventory barang after: " + player.getInventory().getStokBarang().size());

                String msg = success ? "Berhasil membeli " + formattedName + " x" + qty + "!"
                        : "Gagal membeli " + formattedName + ".";
                JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal",
                        success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                moneyLabel.setText("Uang: " + player.getMoney() + "G");

                if (success && autoSaveCallback != null) {
                    System.out.println("Calling auto-save callback...");
                    autoSaveCallback.run();
                    System.out.println("Auto-save callback completed");
                }
                System.out.println("=== DEBUG SUPPLIER PURCHASE END ===");
            });

            itemRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            itemsPanel.add(itemRow);
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    public void setBackToGameCallback(Runnable cb) {
        this.backToGameCallback = cb;
    }

    public void setAutoSaveCallback(Runnable cb) {
        this.autoSaveCallback = cb;
    }

    public void refresh() {
        moneyLabel.setText("Uang: " + player.getMoney() + "G");
        populateItems();
    }

    public void updatePlayerData(Player newPlayer) {
        System.out.println("DEBUG: SupplierPanel.updatePlayerData() called with player: " + newPlayer.getUsername());
        System.out.println("  - Old player: " + (this.player != null ? this.player.getUsername() : "null"));
        System.out.println("  - New player: " + newPlayer.getUsername());
        this.player = newPlayer;
        refresh();
    }
}
