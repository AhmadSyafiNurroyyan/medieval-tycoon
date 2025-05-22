package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.*;

public class HomeBasePanel extends JPanel {
    private JButton btn1, btn2, btn3, btn4, btn5;
    private Runnable backToGameCallback;
    private JButton backButton;

    public HomeBasePanel(Player player) {
        setLayout(null);

        // new Color(245, 222, 179)

        // JPanel upperPanel = new JPanel();
        // upperPanel.setBackground(new Color(245, 222, 179));
        // upperPanel.setBounds(0, 0, getWidth(), 50); // Adjust height as needed
        // upperPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // add(upperPanel);

        // // Create bottom panel
        // JPanel bottomPanel = new JPanel();
        // bottomPanel.setBackground(new Color(245, 222, 179));
        // bottomPanel.setBounds(0, getHeight() - 50, getWidth(), 50); // Adjust height as needed
        // bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // add(bottomPanel);

        Font customFont;
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/medieval.ttf"));

        } catch (FontFormatException | IOException e) {
            customFont = new Font("Serif", Font.BOLD, 24);
        }

        JLabel titleLabel = new JLabel("Home Base");
        titleLabel.setFont(customFont.deriveFont(80f));
        titleLabel.setBounds(20, 15, 100000, 200); 
        
        add(titleLabel);

        btn1 = StyledButton.create("Inventory");
        btn2 = StyledButton.create("Supplier");
        btn3 = StyledButton.create("Perks");
        btn4 = StyledButton.create("Settings");
        btn5 = StyledButton.create("Help");
        add(btn1);
        add(btn2);
        add(btn3);
        add(btn4);
        add(btn5);

        // Back button setup mirip SupplierPanel
        backButton = StyledButton.create("Kembali", 20, 120, 40);
        backButton.addActionListener(e -> {
            if (backToGameCallback != null) backToGameCallback.run();
        });
        // Add backButton to be painted directly in paintComponent
        add(backButton);
    }

    public void setBackToGameCallback(Runnable cb) {
        this.backToGameCallback = cb;
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int panelTop = 150;
        int panelBottom = 100;
        int marginLeft = (int)(getWidth() * 0.02);
        int buttonWidth = (int)(getWidth() * 0.3);
        int areaHeight = getHeight() - panelTop - panelBottom;
        int buttonHeight = (int)(areaHeight * 0.15);
        int numButtons = 5;
        int spacing = (areaHeight - (numButtons * buttonHeight)) / (numButtons + 1);
        int startY = panelTop + spacing;
        JButton[] buttons = {btn1, btn2, btn3, btn4, btn5};
        for (int i = 0; i < numButtons; i++) {
            int y = startY + i * (buttonHeight + spacing);
            buttons[i].setBounds(marginLeft, y, buttonWidth, buttonHeight);
        }
        // Layout backButton di kanan bawah
        if (backButton != null) {
            backButton.setBounds(getWidth() - 140, getHeight() - 80, 120, 40);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(245, 222, 179));
        try {
            Image bg = ImageIO.read(new File("assets/backgrounds/HomeBase.png"));
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        } catch (IOException e) {
            setBackground(Color.WHITE);
        }
        
        g2d.fillRect(0, 0, getWidth(), 150); // Paint upper panel
        
        try {
            Image teto = ImageIO.read(new File("assets/backgrounds/kasane_teto.png"));
            int maxW = (int)(getWidth() * 0.35); // for resize 
            int maxH = (int)(getHeight() - 120); // ts idrk
            int iw = teto.getWidth(this), ih = teto.getHeight(this);
            double scale = Math.max((double)maxW / iw, (double)maxH / ih); // logic
            int w = (int)(iw * scale), h = (int)(ih * scale);
            int x = getWidth() - w + 0; // for align right
            int y = 25; // align from top
            g.drawImage(teto, x, y, w, h, this);
        } catch (IOException ignored) {}

        g2d.fillRect(0, getHeight() - 100, getWidth(), 100); // Paint bottom panel
        // paintPanel(g);
    }

    // public void paintPanel(Graphics g) {
    //     Graphics2D g2d = (Graphics2D) g.create();
    //     g2d.setColor(new Color(245, 222, 179));
    //     g2d.fillRect(0, 0, getWidth(), 100); // Paint upper panel
    //     g2d.fillRect(0, getHeight() - 100, getWidth(), 100); // Paint bottom panel
    //     g2d.dispose();
    // }
}
