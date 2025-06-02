/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package gui;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class LoadingScreen extends JFrame {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Font medievalFont;
    private Timer loadingTimer;
    private int progress = 0;
    
    private final String[] loadingMessages = {
        "Memulai permainan...",
        "Memuat font dan aset visual...",
        "Menyiapkan sistem audio...",
        "Memuat tekstur dan sprite...",
        "Menginisialisasi map manager...",
        "Menyiapkan sistem perdagangan...",
        "Memuat data player dan inventory...",
        "Mempersiapkan antarmuka pengguna...",
        "Finalisasi sistem game...",
        "Selamat datang di Medieval Tycoon!"
    };
    
    private final Runnable onLoadingComplete;

    public LoadingScreen(Runnable onComplete) {
        this.onLoadingComplete = onComplete;
        initializeComponents();
        setupUI();
        startLoading();
    }

    private void initializeComponents() {
        setTitle("Medieval Tycoon - Loading");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.BOLD, 14));
        progressBar.setForeground(new Color(139, 69, 19));
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorderPainted(true);
        progressBar.setString("0%");
        
        statusLabel = new JLabel("Memulai Medieval Tycoon...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Serif", Font.BOLD, 16));
        statusLabel.setForeground(new Color(139, 69, 19));
    }

    
    private void setupUI() {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                try {
                    Image bgImg = new ImageIcon(getClass().getResource("/assets/backgrounds/MainMenu.png")).getImage();
                    g2d.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                    
                    g2d.setColor(new Color(0, 0, 0, 100));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                } catch (Exception e) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(245, 222, 179),
                        0, getHeight(), new Color(210, 180, 140)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                
                g2d.setColor(new Color(139, 69, 19));
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRect(15, 15, getWidth() - 30, getHeight() - 30);
                
                g2d.setColor(new Color(218, 165, 32));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(20, 20, getWidth() - 40, getHeight() - 40);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("MEDIEVAL TYCOON", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(218, 165, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        
        statusLabel.setForeground(new Color(255, 255, 255));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        centerPanel.add(statusLabel, BorderLayout.NORTH);
        
        progressBar.setPreferredSize(new Dimension(400, 25));
        progressBar.setFont(new Font("Arial", Font.BOLD, 16));
        progressBar.setForeground(new Color(139, 69, 19));
        progressBar.setBackground(new Color(245, 222, 179));
        progressBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        centerPanel.add(progressPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));


        backgroundPanel.add(titleLabel, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }

    private void startLoading() {
        loadingTimer = new Timer(500, (_) -> {
            if (progress < 100) {
                if (progress < 10) {
                    progress += 3;
                    verifyInitialResources();
                } else if (progress < 25) {
                    progress += 5;
                    verifyFontResources();
                } else if (progress < 40) {
                    progress += 4;
                    verifyAudioResources();
                } else if (progress < 55) {
                    progress += 6;
                    verifyImageResources();
                } else if (progress < 70) {
                    progress += 5;
                    verifyMapResources();
                } else if (progress < 85) {
                    progress += 4;
                    verifyGameData();
                } else if (progress < 95) {
                    progress += 3;
                    finalizeSystem();
                } else {
                    progress += 2;
                }
                
                progress = Math.min(progress, 100);
                
                progressBar.setValue(progress);
                progressBar.setString(progress + "%");
                
                int messageIndex = Math.min((progress * loadingMessages.length) / 100, loadingMessages.length - 1);
                statusLabel.setText(loadingMessages[messageIndex]);
                
                if (progress >= 100) {
                    loadingTimer.stop();
                    
                    Timer completionTimer = new Timer(1500, (evt) -> {
                        ((Timer) evt.getSource()).stop();
                        dispose();
                        
                        if (onLoadingComplete != null) {
                            onLoadingComplete.run();
                        }
                    });
                    completionTimer.setRepeats(false);
                    completionTimer.start();
                }
            }
        });
        
        loadingTimer.start();
    }

    private void verifyInitialResources() {
        File assetsDir = new File("assets");
        if (!assetsDir.exists()) {
            System.out.println("Loading: Checking assets directory...");
        }
    }

    private void verifyFontResources() {
        File fontFile = new File("assets/fonts/medieval.ttf");
        File fontFileOtf = new File("assets/fonts/medieval.otf");
        if (fontFile.exists() || fontFileOtf.exists()) {
            System.out.println("Loading: Medieval fonts located");
        }
    }

    private void verifyAudioResources() {
        File bgmDir = new File("assets/bgm");
        if (bgmDir.exists()) {
            System.out.println("Loading: Audio system initialized");
        }
    }

    private void verifyImageResources() {
        File spritesDir = new File("assets/sprites");
        File iconsDir = new File("assets/icons");
        File backgroundsDir = new File("assets/backgrounds");
        if (spritesDir.exists() && iconsDir.exists() && backgroundsDir.exists()) {
            System.out.println("Loading: Visual assets loaded");
        }
    }

    private void verifyMapResources() {
        File tilesDir = new File("assets/tiles");
        if (tilesDir.exists()) {
            System.out.println("Loading: Map system ready");
        }
    }

    private void verifyGameData() {
        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdirs();
            System.out.println("Loading: Created saves directory");
        }
        System.out.println("Loading: Game systems initialized");
    }

    private void finalizeSystem() {
        System.out.println("Loading: Finalizing Medieval Tycoon...");
    }

    public void forceComplete() {
        if (loadingTimer != null) {
            loadingTimer.stop();
        }
        dispose();
        if (onLoadingComplete != null) {
            onLoadingComplete.run();
        }
    }
}
