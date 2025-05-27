package gui;

import model.Player;
import model.Perk;
import model.TokoPerks;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TokoPerksPanel extends JPanel {

  private TokoPerks toko;
  private Player player;
  private JPanel perksPanel;
  private JScrollPane scrollPane;
  private JLabel moneyLabel;
  private Runnable backToGameCallback;

  public TokoPerksPanel(TokoPerks toko, Player player) {
    this.toko = toko;
    this.player = player;
    setLayout(new BorderLayout());
    setOpaque(true);
    setBackground(new Color(245, 222, 179));

    JLabel title = new JLabel("Toko Perks", JLabel.CENTER);
    title.setFont(new Font("Serif", Font.BOLD, 32));
    title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
    add(title, BorderLayout.NORTH);

    moneyLabel = new JLabel("Uang: " + player.getMoney() + "G", JLabel.CENTER);
    moneyLabel.setFont(new Font("Serif", Font.PLAIN, 22));

    perksPanel = new JPanel();
    perksPanel.setLayout(new BoxLayout(perksPanel, BoxLayout.Y_AXIS));
    perksPanel.setOpaque(false);
    populatePerks();

    scrollPane = new JScrollPane(perksPanel);
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

  private void populatePerks() {
    perksPanel.removeAll();
    List<Perk> daftarPerk = toko.getDaftarPerk();

    for (Perk perk : daftarPerk) {
      JPanel perkRow = new JPanel(new GridBagLayout());
      perkRow.setOpaque(false);
      perkRow.setPreferredSize(new Dimension(800, 70)); // Tinggi lebih nyaman

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(0, 10, 0, 10); // Spasi antar elemen
      gbc.gridy = 0;
      gbc.anchor = GridBagConstraints.WEST;

      // Nama Perk (tidak bold, font besar)
      JLabel nameLabel = new JLabel(perk.getName() + " (" + perk.getPerkType() + ")");
      nameLabel.setFont(new Font("Serif", Font.PLAIN, 24));
      gbc.gridx = 0;
      gbc.weightx = 1.0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      perkRow.add(nameLabel, gbc);

      // Harga
      JLabel hargaLabel = new JLabel(perk.getHarga() + "G");
      hargaLabel.setFont(new Font("Serif", Font.BOLD, 22));
      gbc.gridx = 1;
      gbc.weightx = 0;
      gbc.anchor = GridBagConstraints.CENTER;
      perkRow.add(hargaLabel, gbc);

      // Tombol Beli
      JButton beliButton = StyledButton.create("Beli", 18, 80, 35);
      gbc.gridx = 2;
      gbc.anchor = GridBagConstraints.EAST;
      perkRow.add(beliButton, gbc);

      beliButton.addActionListener(e -> {
        if (player.hasPerk(perk.getPerkType())) {
          JOptionPane.showMessageDialog(this, "Kamu sudah memiliki perk ini.",
              "Gagal", JOptionPane.ERROR_MESSAGE);
          return;
        }

        boolean success = toko.beli(player, perk);
        String msg = success ? "Berhasil membeli " + perk.getName() + "!"
            : "Gagal membeli " + perk.getName() + ".";

        JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal",
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        moneyLabel.setText("Uang: " + player.getMoney() + "G");
        refresh();
      });

      perksPanel.add(perkRow);
    }

    perksPanel.revalidate();
    perksPanel.repaint();
  }

  public void setBackToGameCallback(Runnable cb) {
    this.backToGameCallback = cb;
  }

  public void refresh() {
    moneyLabel.setText("Uang: " + player.getMoney() + "G");
    populatePerks();
  }
}
