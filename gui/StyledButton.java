package gui;

import java.awt.*;
import javax.swing.*;

public class StyledButton {
    public static JButton create(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 24));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 60));
        button.setPreferredSize(new Dimension(300, 60));
        button.setBackground(new Color(139, 69, 19));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(160, 82, 45));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(139, 69, 19));
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return button;
    }
    public static JButton create(String text, int fontSize, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, fontSize));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(width, height));
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(new Color(139, 69, 19));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(160, 82, 45));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(139, 69, 19));
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return button;
    }
    public static JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(new Font("Serif", Font.PLAIN, 24));
        checkBox.setOpaque(true);
        checkBox.setBackground(new Color(139, 69, 19));
        checkBox.setForeground(Color.WHITE);
        checkBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        return checkBox;
    }
    public static JLabel createLabel(String text, int fontSize, Color color, int style, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Serif", style, fontSize));
        label.setForeground(color);
        return label;
    }

    public static JLabel createLabel(String text, int fontSize, Color color) {
        return createLabel(text, fontSize, color, Font.BOLD, JLabel.LEFT);
    }

    public static JLabel createLabel(String text) {
        return createLabel(text, 24, Color.WHITE, Font.BOLD, JLabel.LEFT);
    }
}
