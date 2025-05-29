package gui;

import model.Player;
import model.Perk;
import model.TokoPerks;

import javax.swing.*;

import exceptions.PerkConversionException;

import java.awt.*;
import java.io.File;
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

  private Perk currentPerk = null;

  private void populatePerks() {
    perksPanel.removeAll();
    List<Perk> daftarPerk = toko.getDaftarPerk();
    List<Perk> ownedPerks = player.getSemuaPerkDimiliki();

    for (Perk perk : daftarPerk) {
      JPanel perkRow = new JPanel(new GridBagLayout());
      perkRow.setOpaque(false);
      perkRow.setPreferredSize(new Dimension(800, 70));

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(0, 10, 0, 10);
      gbc.gridy = 0;
      gbc.anchor = GridBagConstraints.WEST;

      boolean alreadyOwned = player.hasPerk(perk.getPerkType());

      // Icon perk
      ImageIcon perkIcon = loadIcon(perk.getIconPath(), 40, 40);
      JLabel iconLabel = new JLabel(perkIcon);
      gbc.gridx = 0;
      gbc.anchor = GridBagConstraints.WEST;
      perkRow.add(iconLabel, gbc);

      // Nama + Level
      String labelText = perk.getName() + " (" + perk.getPerkType() + ")";
      if (alreadyOwned) {
        // Cari instance perk yang dimiliki player (agar level & upgrade sesuai)
        for (Perk p : ownedPerks) {
          if (p.getPerkType() == perk.getPerkType()) {
            labelText += "  Lv. " + p.getLevel();
            break;
          }
        }
      }
      JLabel nameLabel = new JLabel(labelText);
      nameLabel.setFont(new Font("Serif", Font.PLAIN, 24));
      gbc.gridx = 1;
      gbc.weightx = 1.0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      perkRow.add(nameLabel, gbc);

      // Status label: "Dimiliki" jika sudah punya
      JLabel statusLabel;
      if (alreadyOwned) {
        statusLabel = new JLabel("Dimiliki", JLabel.CENTER);
        statusLabel.setFont(new Font("Serif", Font.BOLD, 22));
        statusLabel.setForeground(new Color(34, 139, 34));
      } else {
        statusLabel = new JLabel("", JLabel.CENTER);
      }
      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.anchor = GridBagConstraints.CENTER;
      perkRow.add(statusLabel, gbc);

      // Tombol Upgrade untuk perk yang dimiliki
      if (alreadyOwned) {
        // Cari instance perk yang dimiliki player
        for (Perk p : ownedPerks) {
          if (p.getPerkType() == perk.getPerkType()) {
            final Perk ownedPerk = p;
            // Biaya upgrade
            JLabel biayaLabel = new JLabel(ownedPerk.isMaxLevel() ? "MAX" : ownedPerk.getBiayaUpgrade() + "G");
            biayaLabel.setFont(new Font("Serif", Font.PLAIN, 20));
            biayaLabel.setForeground(new Color(139, 69, 19));
            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.EAST;
            perkRow.add(biayaLabel, gbc);

            JButton upgradeButton = StyledButton.create("Upgrade", 18, 100, 35);
            upgradeButton.setEnabled(!ownedPerk.isMaxLevel());
            gbc.gridx = 4;
            perkRow.add(upgradeButton, gbc);

            upgradeButton.addActionListener(e -> {
              try {
                boolean success = toko.upgrade(player, ownedPerk);
                String msg = success
                    ? "Upgrade berhasil ke level " + ownedPerk.getLevel() + "!"
                    : (ownedPerk.isMaxLevel() ? "Level sudah maksimum." : "Uang tidak cukup untuk upgrade.");
                JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal",
                    success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                moneyLabel.setText("Uang: " + player.getMoney() + "G");

                // Update label level jika berhasil, TANPA refresh seluruh panel
                if (success) {
                  nameLabel.setText(perk.getName() + " (" + perk.getPerkType() + ")  Lv. " + ownedPerk.getLevel());
                  biayaLabel.setText(ownedPerk.isMaxLevel() ? "MAX" : ownedPerk.getBiayaUpgrade() + "G");
                  upgradeButton.setEnabled(!ownedPerk.isMaxLevel());
                }
              } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
              }
            });
            break;
          }
        }
      }

      // Tombol Convert hanya jika belum dimiliki dan bisa dikonversi
      if (!alreadyOwned) {
        boolean canConvert = false;
        for (Perk owned : ownedPerks) {
          if (owned.canConvertTo(perk.getPerkType())) {
            canConvert = true;
            break;
          }
        }
        if (canConvert) {
          JLabel hargaLabel = new JLabel(perk.getHarga() + "G");
          hargaLabel.setFont(new Font("Serif", Font.PLAIN, 20));
          hargaLabel.setForeground(new Color(139, 69, 19));
          gbc.gridx = 3;
          gbc.anchor = GridBagConstraints.EAST;
          perkRow.add(hargaLabel, gbc);

          JButton convertButton = StyledButton.create("Convert", 18, 100, 35);
          gbc.gridx = 4;
          perkRow.add(convertButton, gbc);

          convertButton.addActionListener(e -> {
            try {
              boolean success = false;
              if (ownedPerks.size() < 2) {
                success = toko.convert(player, null, perk.getPerkType());
              } else {
                String[] options = new String[2];
                options[0] = ownedPerks.get(0).getName();
                options[1] = ownedPerks.get(1).getName();
                int result = JOptionPane.showOptionDialog(this, "Pilih perk yang ingin diganti:",
                    "Ganti Perk", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
                if (result == 0 || result == 1) {
                  Perk perkToReplace = ownedPerks.get(result);
                  success = toko.convert(player, perkToReplace, perk.getPerkType());
                } else {
                  return;
                }
              }
              String msg = success
                  ? "Berhasil menambah/mengganti dengan " + perk.getName() + "!"
                  : "Gagal menambah/mengganti dengan " + perk.getName() + ".";
              JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal",
                  success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
              moneyLabel.setText("Uang: " + player.getMoney() + "G");
              refresh();
            } catch (PerkConversionException ex) {
              JOptionPane.showMessageDialog(this, ex.getMessage(), "Konversi Tidak Diizinkan",
                  JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException ex) {
              JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
            }
          });
        }
      }

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

  // Tambahkan method ini di kelas TokoPerksPanel
  private ImageIcon loadIcon(String iconName, int width, int height) {
    try {
      File iconFile = new File("assets/icons/" + iconName);
      if (!iconFile.exists()) {
        System.err.println("Icon file not found: " + iconFile.getAbsolutePath());
        return null;
      }

      ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
      Image img = icon.getImage();
      Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      return new ImageIcon(resizedImg);
    } catch (Exception e) {
      System.err.println("Error loading icon: " + iconName + " - " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
}
