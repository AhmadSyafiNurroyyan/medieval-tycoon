package gui;

import model.Supplier;
import model.Player;
import enums.JenisBarang;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Set;

public class SupplierPanel extends JPanel {
    private Supplier supplier;
    private Player player;
    private JPanel itemsPanel;
    private JScrollPane scrollPane;
    private JLabel moneyLabel;
    private Runnable backToGameCallback;

    public SupplierPanel(Supplier supplier, Player player) {
        this.supplier = supplier;
        this.player = player;
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(245, 222, 179));

        JLabel title = new JLabel("Supplier - Stok Hari Ini", JLabel.CENTER);
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
            if (backToGameCallback != null) backToGameCallback.run();
        });
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(moneyLabel, BorderLayout.CENTER);
        bottomPanel.add(backButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void populateItems() {
        itemsPanel.removeAll();
        Set<JenisBarang> stok = supplier.getStokHariIni();
        for (JenisBarang jenis : stok) {
            JPanel itemRow = new JPanel(new GridBagLayout());
            itemRow.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = 0;

            // Format name: replace _ with space, capitalize each word
            String formattedName = Arrays.stream(jenis.name().toLowerCase().split("_"))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .reduce((a, b) -> a + " " + b).orElse(jenis.name());

            //ImageIcon icon = new ImageIcon("assets/icons/" + jenis.getIconPath());
            ImageIcon icon = new ImageIcon("assets/sprites/dir1_0.png");
            JLabel nameLabel = new JLabel(formattedName, icon, JLabel.LEFT);
            nameLabel.setFont(new Font("Serif", Font.PLAIN, 22));
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx = 1;
            itemRow.add(nameLabel, gbc);

            JLabel hargaLabel = new JLabel(jenis.getHarga() + "G");
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

            JLabel totalLabel = new JLabel("= " + jenis.getHarga() + "G");
            totalLabel.setFont(new Font("Serif", Font.BOLD, 22));
            totalLabel.setPreferredSize(new Dimension(110, 30));
            gbc.gridx = 5;
            itemRow.add(totalLabel, gbc);

            JButton buyButton = StyledButton.create("Beli", 18, 70, 30);
            gbc.gridx = 6;
            itemRow.add(buyButton, gbc);

            minusBtn.addActionListener(e -> {
                int qty = Integer.parseInt(qtyField.getText());
                if (qty > 1) qty--;
                qtyField.setText(String.valueOf(qty));
                totalLabel.setText("= " + (jenis.getHarga() * qty) + "G");
            });
            plusBtn.addActionListener(e -> {
                int qty = Integer.parseInt(qtyField.getText()) + 1;
                qtyField.setText(String.valueOf(qty));
                totalLabel.setText("= " + (jenis.getHarga() * qty) + "G");
            });
            qtyField.addActionListener(e -> {
                int qty;
                try { qty = Math.max(1, Integer.parseInt(qtyField.getText())); }
                catch (NumberFormatException ex) { qty = 1; }
                qtyField.setText(String.valueOf(qty));
                totalLabel.setText("= " + (jenis.getHarga() * qty) + "G");
            });

            buyButton.addActionListener(e -> {
                int qty;
                try { qty = Math.max(1, Integer.parseInt(qtyField.getText())); }
                catch (NumberFormatException ex) { qty = 1; }
                int totalHarga = jenis.getHarga() * qty;
                if (player.getMoney() < totalHarga) {
                    JOptionPane.showMessageDialog(this, "Uang tidak cukup untuk membeli " + formattedName + " x" + qty + ".", "Gagal", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean success = true;
                for (int i = 0; i < qty; i++) {
                    if (!supplier.beli(player, jenis)) { success = false; break; }
                }
                String msg = success ? "Berhasil membeli " + formattedName + " x" + qty + "!" : "Gagal membeli " + formattedName + ".";
                JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal", success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                moneyLabel.setText("Uang: " + player.getMoney()+"G");
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

    public void refresh() {
        moneyLabel.setText("Uang: " + player.getMoney()+"G");
        populateItems();
    }
}
