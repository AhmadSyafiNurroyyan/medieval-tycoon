/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package gui;

import enums.PerkType;
import exceptions.PerkConversionException;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import model.Perk;
import model.PerksManagement;
import model.Player;

public class TokoPerksPanel extends JPanel {
  private PerksManagement perksManagement;
  private Player player;
  private JPanel perksPanel;
  private JScrollPane scrollPane;
  private JLabel moneyLabel;
  private Runnable backToGameCallback;
  private Runnable autoSaveCallback;

  public TokoPerksPanel(PerksManagement perksManagement, Player player) {
    this.perksManagement = perksManagement;
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
    List<Perk> daftarPerk = perksManagement.getDaftarPerkDiToko();
    List<Perk> ownedPerks = perksManagement.getPerkYangDimiliki(player);

    for (Perk perk : daftarPerk) {
      boolean alreadyOwned = player.hasPerk(perk.getPerkType());
      if (alreadyOwned) {
        addPerkRow(perk, true, ownedPerks);
      }
    }
    for (Perk perk : daftarPerk) {
      boolean alreadyOwned = player.hasPerk(perk.getPerkType());
      if (!alreadyOwned) {
        addPerkRow(perk, false, ownedPerks);
      }
    }

    perksPanel.revalidate();
    perksPanel.repaint();
  }

  private void addPerkRow(Perk perk, boolean alreadyOwned, List<Perk> ownedPerks) {
    JPanel perkRow = new JPanel(new GridBagLayout());
    perkRow.setOpaque(false);
    perkRow.setPreferredSize(new Dimension(800, 70));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(0, 10, 0, 10);
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;

    ImageIcon perkIcon = loadIcon(perk.getIconPath(), 40, 40);
    JLabel iconLabel = new JLabel(perkIcon);
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    perkRow.add(iconLabel, gbc);

    Perk ownedPerk = null;
    int level = 0;
    if (alreadyOwned) {
        ownedPerk = perksManagement.getPlayerPerkByType(player, perk.getPerkType());
        if (ownedPerk != null) {
            level = ownedPerk.getLevel();
        }
    }
    String labelText = perk.getName() + " (" + perk.getPerkType() + ")";
    if (alreadyOwned && ownedPerk != null) {
        labelText += "  Lv. " + level;
    }
    JLabel nameLabel = new JLabel(labelText);
    nameLabel.setFont(new Font("Serif", Font.BOLD, 22));
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    perkRow.add(nameLabel, gbc);

    String descText = perk.getDeskripsi();
    int showLevel = (alreadyOwned && ownedPerk != null) ? ownedPerk.getLevel() : 0;
    if (perk.getPerkType() == PerkType.ACTIVE) {
        descText += " (+" + (showLevel * 0.5) + "x pembeli)";
    } else if (perk.getPerkType() == PerkType.CHARMING) {
        descText += " (+" + (showLevel * 15) + "% nego)";
    } else if (perk.getPerkType() == PerkType.ELEGAN) {
        descText += " (+" + (showLevel * 10) + "% tajir)";
    }
    JLabel descLabel = new JLabel(descText);
    descLabel.setFont(new Font("Serif", Font.ITALIC, 16));
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    perkRow.add(descLabel, gbc);
    gbc.gridy = 0;

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

    if (alreadyOwned && ownedPerk != null) {
        JLabel biayaLabel = new JLabel(ownedPerk.isMaxLevel() ? "MAX" : ownedPerk.getBiayaUpgrade() + "G");
        biayaLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        biayaLabel.setForeground(new Color(139, 69, 19));
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.EAST;
        perkRow.add(biayaLabel, gbc);

        JButton upgradeButton = StyledButton.create("Upgrade", 18, 100, 35);
        upgradeButton.setEnabled(perksManagement.canPlayerAffordUpgrade(player, ownedPerk) && !ownedPerk.isMaxLevel());
        gbc.gridx = 4;
        perkRow.add(upgradeButton, gbc);
        final Perk finalOwnedPerk = ownedPerk;
        final JLabel finalNameLabel = nameLabel;
        final JLabel finalDescLabel = descLabel;
        final JLabel finalBiayaLabel = biayaLabel;
        final JButton finalUpgradeButton = upgradeButton;
        upgradeButton.addActionListener(e -> {
            try {
                boolean success = perksManagement.upgradePerk(player, finalOwnedPerk);
                String msg = success
                        ? "Upgrade berhasil ke level " + finalOwnedPerk.getLevel() + "!"
                        : (finalOwnedPerk.isMaxLevel() ? "Level sudah maksimum." : "Uang tidak cukup untuk upgrade.");
                JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal",
                        success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                updateMoneyDisplay();
                if (success) {
                    finalNameLabel.setText(perk.getName() + " (" + perk.getPerkType() + ")  Lv. " + finalOwnedPerk.getLevel());
                    String updatedDesc = perk.getDeskripsi();
                    int newLevel = finalOwnedPerk.getLevel();
                    if (perk.getPerkType() == PerkType.ACTIVE) {
                        updatedDesc += " (+" + (newLevel * 0.5) + "x pembeli)";
                    } else if (perk.getPerkType() == PerkType.CHARMING) {
                        updatedDesc += " (+" + (newLevel * 15) + "% nego)";
                    } else if (perk.getPerkType() == PerkType.ELEGAN) {
                        updatedDesc += " (+" + (newLevel * 10) + "% tajir)";
                    }
                    finalDescLabel.setText(updatedDesc);
                    finalBiayaLabel.setText(finalOwnedPerk.isMaxLevel() ? "MAX" : finalOwnedPerk.getBiayaUpgrade() + "G");
                    finalUpgradeButton.setEnabled(perksManagement.canPlayerAffordUpgrade(player, finalOwnedPerk) && !finalOwnedPerk.isMaxLevel());
                    if (autoSaveCallback != null) autoSaveCallback.run();
                }
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });
    } else {
        boolean hasAllPerkSlots = ownedPerks.size() >= 2;
        boolean canBuy = !hasAllPerkSlots && perksManagement.canPlayerAffordPerk(player, perk.getPerkType());
        boolean canConvert = hasAllPerkSlots && perksManagement.hasConvertiblePerk(player, perk.getPerkType());
        if (canBuy || canConvert) {
            JLabel hargaLabel = new JLabel(perk.getHarga() + "G");
            hargaLabel.setFont(new Font("Serif", Font.PLAIN, 20));
            hargaLabel.setForeground(new Color(139, 69, 19));
            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.EAST;
            perkRow.add(hargaLabel, gbc);
            String buttonText = canBuy ? "Beli" : "Convert";
            JButton actionButton = StyledButton.create(buttonText, 18, 100, 35);
            gbc.gridx = 4;
            perkRow.add(actionButton, gbc);
            actionButton.addActionListener(e -> {
                try {
                    boolean success = false;
                    if (canBuy) {
                        success = perksManagement.buyPerk(player, perk.getPerkType());
                    } else if (canConvert) {
                        List<Perk> convertiblePerksList = perksManagement.getConvertiblePerks(player, perk.getPerkType());
                        if (convertiblePerksList.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Tidak ada perk yang dapat dikonversi ke " + perk.getName(), "Konversi Tidak Memungkinkan", JOptionPane.WARNING_MESSAGE);
                            return;
                        } else if (convertiblePerksList.size() == 1) {
                            success = perksManagement.convertPerk(player, convertiblePerksList.get(0), perk.getPerkType());
                        } else {
                            String[] optionsArr = new String[convertiblePerksList.size()];
                            for (int i = 0; i < convertiblePerksList.size(); i++) {
                                optionsArr[i] = convertiblePerksList.get(i).getName() + " (Lv." + convertiblePerksList.get(i).getLevel() + ")";
                            }
                            int dialogResult = JOptionPane.showOptionDialog(this,
                                    "Pilih perk yang ingin diganti:\n(Level akan direset ke 0)",
                                    "Ganti Perk",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null, optionsArr, optionsArr[0]);
                            if (dialogResult >= 0 && dialogResult < convertiblePerksList.size()) {
                                Perk perkToReplace = convertiblePerksList.get(dialogResult);
                                success = perksManagement.convertPerk(player, perkToReplace, perk.getPerkType());
                            } else {
                                return;
                            }
                        }
                    }
                    String msg = success
                            ? "Berhasil " + (canBuy ? "membeli" : "mengganti dengan") + " " + perk.getName() + "!"
                            : "Gagal " + (canBuy ? "membeli" : "mengganti dengan") + " " + perk.getName() + ".";
                    JOptionPane.showMessageDialog(this, msg, success ? "Sukses" : "Gagal",
                            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                    if (success && autoSaveCallback != null) autoSaveCallback.run();
                    updateMoneyDisplay();
                    refresh();
                } catch (PerkConversionException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Konversi Tidak Diizinkan", JOptionPane.ERROR_MESSAGE);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            });
        } else if (hasAllPerkSlots) {
            JLabel infoLabel = new JLabel("Perlu konversi", JLabel.CENTER);
            infoLabel.setFont(new Font("Serif", Font.ITALIC, 16));
            infoLabel.setForeground(new Color(139, 69, 19));
            gbc.gridx = 3;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.EAST;
            perkRow.add(infoLabel, gbc);
        }
    }
    perksPanel.add(perkRow);
    perksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
  }

  public void setBackToGameCallback(Runnable cb) {
    this.backToGameCallback = cb;
  }

  public void setAutoSaveCallback(Runnable cb) {
    this.autoSaveCallback = cb;
  }

  private void updateMoneyDisplay() {
    moneyLabel.setText("Uang: " + player.getMoney() + "G");
  }

  public void refresh() {
    updateMoneyDisplay();
    populatePerks();
  }

  public void updatePlayerData(Player newPlayer) {
    this.player = newPlayer;
    updateMoneyDisplay();
    populatePerks();
  }

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
