package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class DialogSystem extends JPanel {
    private static final int DIALOG_HEIGHT = 200;
    private static final int TETO_IMAGE_WIDTH = 120;
    private static final int DIALOG_MARGIN = 20;
    private static final int TEXT_PADDING = 15;
    
    private Image tetoImage;
    private Image neuvilletteImage;
    private String currentMessage;
    private boolean isVisible;
    private JPanel parentPanel;
    private Font dialogFont;
    
    private static final Color DIALOG_BG = new Color(255, 248, 220);
    private static final Color BORDER_COLOR = new Color(212, 175, 55);
    private static final Color TEXT_COLOR = new Color(60, 40, 10);
    private static final Color SHADOW_COLOR = new Color(120, 90, 30, 80);    public DialogSystem(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.isVisible = false;
        this.currentMessage = "";
        
        loadTetoImage();
        dialogFont = new Font("Serif", Font.PLAIN, 20);
        
        setOpaque(false);
        setLayout(null);
    }
    
    private void loadTetoImage() {
        try {
            tetoImage = ImageIO.read(new File("assets/backgrounds/kasane_teto.png"));
        } catch (IOException e) {
            System.err.println("Could not load Kasane Teto image: " + e.getMessage());
            tetoImage = null;
        }
        
        try {
            neuvilletteImage = ImageIO.read(new File("assets/backgrounds/Neuvillette.png"));
        } catch (IOException e) {
            System.err.println("Could not load Neuvillette image: " + e.getMessage());
            neuvilletteImage = null;
        }    }
    
    public void showDialog(String message) {
        this.currentMessage = message;
        this.isVisible = true;
        
        if (parentPanel != null) {
            if (getParent() != parentPanel) {
                parentPanel.add(this);
                parentPanel.setComponentZOrder(this, 0);
            }
            
            setBounds(0, 0, parentPanel.getWidth(), parentPanel.getHeight());
            setVisible(true);
            parentPanel.repaint();
        }
    }
    
    public void hideDialog() {
        this.isVisible = false;
        setVisible(false);
        
        if (parentPanel != null) {
            parentPanel.repaint();
        }
    }
    
    public boolean isDialogVisible() {
        return isVisible;
    }
      @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!isVisible || currentMessage.isEmpty()) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int dialogWidth = panelWidth - (2 * DIALOG_MARGIN);
        int dialogX = DIALOG_MARGIN;
        int dialogY = panelHeight - DIALOG_HEIGHT - DIALOG_MARGIN;
        
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, panelWidth, panelHeight);        
        if (neuvilletteImage != null) {
            int iw = neuvilletteImage.getWidth(this);
            int ih = neuvilletteImage.getHeight(this);
            
            if (iw > 0 && ih > 0) {
                double headFromTop = panelHeight * 0.12;
                double headPortionInImage = 0.2;
                double targetImageHeight = headFromTop / headPortionInImage;
                double scale = targetImageHeight / ih;
                
                double minScale = Math.max(0.3, panelHeight / (ih * 5.0));
                double maxScale = Math.min(2.0, panelHeight / (ih * 0.5));
                scale = Math.max(scale, minScale);
                scale = Math.min(scale, maxScale);
                
                int w = (int) (iw * scale);
                int h = (int) (ih * scale);
                int x = panelWidth - w + (int)(panelWidth * 0.02);
                int y = panelHeight - (h / 2);
                
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.drawImage(neuvilletteImage, x, y, w, h, this);
            }
        }        if (tetoImage != null) {
            int iw = tetoImage.getWidth(this);
            int ih = tetoImage.getHeight(this);
            
            if (iw > 0 && ih > 0) {
                double headFromTop = panelHeight * 0.12;
                double headPortionInImage = 0.2;
                double targetImageHeight = headFromTop / headPortionInImage;
                double scale = targetImageHeight / ih;
                
                double minScale = Math.max(0.3, panelHeight / (ih * 5.0));
                double maxScale = Math.min(2.0, panelHeight / (ih * 0.5));
                scale = Math.max(scale, minScale);
                scale = Math.min(scale, maxScale);                
                int w = (int) (iw * scale);
                int h = (int) (ih * scale);
                int x = (int)(panelWidth * 0.02);
                int y = panelHeight - (h / 2);
                
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.drawImage(tetoImage, x, y, w, h, this);
            }        }        // Calculate text area to be centered between the two character images
        int leftImageWidth = TETO_IMAGE_WIDTH + 20; // Teto image width + margin
        int rightImageWidth = TETO_IMAGE_WIDTH + 20; // Neuvillette image width + margin (assuming similar size)
        int textAreaX = dialogX + leftImageWidth;
        int textAreaWidth = dialogWidth - leftImageWidth - rightImageWidth;
        int textAreaHeight = DIALOG_HEIGHT - (2 * TEXT_PADDING);
        
        g2d.setColor(SHADOW_COLOR);
        g2d.fillRoundRect(textAreaX + 4, dialogY + 4, textAreaWidth, DIALOG_HEIGHT, 15, 15);
        
        g2d.setColor(DIALOG_BG);
        g2d.fillRoundRect(textAreaX, dialogY, textAreaWidth, DIALOG_HEIGHT, 15, 15);
        
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(BORDER_COLOR);
        g2d.drawRoundRect(textAreaX, dialogY, textAreaWidth, DIALOG_HEIGHT, 15, 15);
        
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(BORDER_COLOR.darker());
        g2d.drawRoundRect(textAreaX + 2, dialogY + 2, textAreaWidth - 4, DIALOG_HEIGHT - 4, 12, 12);
          g2d.setColor(TEXT_COLOR);
        g2d.setFont(dialogFont);
        
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        int maxLines = textAreaHeight / lineHeight;
        
        String[] words = currentMessage.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int currentLineCount = 0;
        int textY = dialogY + TEXT_PADDING + fm.getAscent();
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            
            if (fm.stringWidth(testLine) <= textAreaWidth - (2 * TEXT_PADDING)) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0 && currentLineCount < maxLines) {
                    g2d.drawString(currentLine.toString(), textAreaX + TEXT_PADDING, textY);
                    currentLineCount++;
                    textY += lineHeight;
                }
                currentLine = new StringBuilder(word);
                
                if (currentLineCount >= maxLines) {
                    break;
                }
            }
        }
        
        if (currentLine.length() > 0 && currentLineCount < maxLines) {
            g2d.drawString(currentLine.toString(), textAreaX + TEXT_PADDING, textY);
        }
        
        g2d.setFont(dialogFont.deriveFont(18f));
        g2d.setColor(TEXT_COLOR.brighter());
        String hintText = "Press E to close";
        int hintWidth = g2d.getFontMetrics().stringWidth(hintText);
        g2d.drawString(hintText, textAreaX + textAreaWidth - hintWidth - TEXT_PADDING, 
                      dialogY + DIALOG_HEIGHT - 8);
        
        g2d.dispose();
    }
      @Override
    public Dimension getPreferredSize() {
        return parentPanel != null ? parentPanel.getSize() : super.getPreferredSize();
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Trigger repaint when bounds change to update dynamic scaling
        if (isVisible) {
            repaint();
        }
    }
      /**
     * Update the parent panel reference and bounds
     */
    public void updateParent(JPanel newParent) {
        this.parentPanel = newParent;
        if (isVisible && parentPanel != null) {
            setBounds(0, 0, parentPanel.getWidth(), parentPanel.getHeight());
        }
    }
}
