/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/
package gui;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class BGMPlayer {
  private static BGMPlayer instance;
  private Clip currentClip;
  private boolean isEnabled = true;
  private boolean isPlayingHomeBaseBGM = false;
  private boolean isPlayingMapBGM = false;
  private boolean isPlayingKotaLainBGM = false;
  private String currentBGMPath = null;
  private static final String HOME_BASE_BGM_PATH_1 = "assets/bgm/HomeBase.wav";
  private static final String HOME_BASE_BGM_PATH_2 = "assets/bgm/med.wav";
  private static final String MAP_BGM_PATH = "assets/bgm/MapUtama.wav";
  private static final String KOTA_LAIN_BGM_PATH = "assets/bgm/KotaLain.wav";
  private BGMPlayer() {
  }
  public static BGMPlayer getInstance() {
    if (instance == null) {
      instance = new BGMPlayer();
    }
    return instance;
  }
  public void playHomeBaseBGM() {
    if (!isEnabled || isPlayingHomeBaseBGM) {
      System.out
          .println("HomeBase BGM not starting: enabled=" + isEnabled + ", already playing=" + isPlayingHomeBaseBGM);
      return;
    }
    System.out.println("Attempting to start HomeBase BGM...");
    String bgmPath = HOME_BASE_BGM_PATH_1;
    File bgmFile = new File(bgmPath);
    if (!bgmFile.exists()) {
      System.out.println("Primary HomeBase BGM file not found, trying fallback: " + bgmPath);
      bgmPath = HOME_BASE_BGM_PATH_2;
      bgmFile = new File(bgmPath);
      if (!bgmFile.exists()) {
        System.err.println("HomeBase BGM files not found: " + HOME_BASE_BGM_PATH_1 + " or " + HOME_BASE_BGM_PATH_2);
        return;
      }
    }
    playBGM(bgmPath, true, false, false);
  }
  public void playMapBGM() {
    if (!isEnabled || isPlayingMapBGM) {
      System.out.println("Map BGM not starting: enabled=" + isEnabled + ", already playing=" + isPlayingMapBGM);
      return;
    }
    System.out.println("Attempting to start Map BGM...");
    File bgmFile = new File(MAP_BGM_PATH);
    if (!bgmFile.exists()) {
      System.err.println("Map BGM file not found: " + MAP_BGM_PATH);
      return;
    }
    playBGM(MAP_BGM_PATH, false, true, false);
  }
  public void playKotaLainBGM() {
    if (!isEnabled || isPlayingKotaLainBGM) {
      System.out
          .println("Kota Lain BGM not starting: enabled=" + isEnabled + ", already playing=" + isPlayingKotaLainBGM);
      return;
    }
    System.out.println("Attempting to start Kota Lain BGM...");
    File bgmFile = new File(KOTA_LAIN_BGM_PATH);
    if (!bgmFile.exists()) {
      System.err.println("Kota Lain BGM file not found: " + KOTA_LAIN_BGM_PATH);
      return;
    }
    playBGM(KOTA_LAIN_BGM_PATH, false, false, true);
  }
  private void playBGM(String bgmPath, boolean isHomeBase, boolean isMap, boolean isKotaLain) {
    System.out.println("Found BGM file: " + bgmPath);
    try {
      stopBGM();
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(bgmPath));
      currentClip = AudioSystem.getClip();
      currentClip.open(audioInputStream);
      currentClip.loop(Clip.LOOP_CONTINUOUSLY);
      currentClip.start();
      isPlayingHomeBaseBGM = isHomeBase;
      isPlayingMapBGM = isMap;
      isPlayingKotaLainBGM = isKotaLain;
      currentBGMPath = bgmPath;
      String bgmType = isHomeBase ? "HomeBase" : (isKotaLain ? "KotaLain" : "Map");
      System.out.println("Started playing " + bgmType + " BGM: " + bgmPath);
    } catch (UnsupportedAudioFileException e) {
      System.err.println("Unsupported audio format: " + bgmPath);
      System.err.println("Error: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("Error reading BGM file: " + bgmPath);
      System.err.println("Error: " + e.getMessage());
    } catch (LineUnavailableException e) {
      System.err.println("Audio line unavailable for BGM: " + bgmPath);
      System.err.println("Error: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Unexpected error playing BGM: " + bgmPath);
      System.err.println("Error: " + e.getMessage());
    }
  }
  public void stopBGM() {
    if (currentClip != null && currentClip.isRunning()) {
      currentClip.stop();
      currentClip.close();
      currentClip = null;
      System.out.println("Stopped BGM: " + currentBGMPath);
    }
    isPlayingHomeBaseBGM = false;
    isPlayingMapBGM = false;
    isPlayingKotaLainBGM = false;
    currentBGMPath = null;
  }
  public void stopHomeBaseBGM() {
    if (isPlayingHomeBaseBGM) {
      stopBGM();
    }
  }
  public void stopMapBGM() {
    if (isPlayingMapBGM) {
      stopBGM();
    }
  }
  public void stopKotaLainBGM() {
    if (isPlayingKotaLainBGM) {
      stopBGM();
    }
  }
  public boolean isPlayingHomeBaseBGM() {
    return isPlayingHomeBaseBGM && currentClip != null && currentClip.isRunning();
  }
  public boolean isPlayingMapBGM() {
    return isPlayingMapBGM && currentClip != null && currentClip.isRunning();
  }
  public boolean isPlayingKotaLainBGM() {
    return isPlayingKotaLainBGM && currentClip != null && currentClip.isRunning();
  }
  public void setEnabled(boolean enabled) {
    this.isEnabled = enabled;
    if (!enabled) {
      stopBGM();
    }
  }
  public boolean isEnabled() {
    return isEnabled;
  }
  public void setVolume(float volume) {
    if (currentClip != null && currentClip.isOpen()) {
      try {
        FloatControl volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(Math.max(volume, 0.0001)) / Math.log(10.0) * 20.0);
        dB = Math.max(dB, volumeControl.getMinimum());
        dB = Math.min(dB, volumeControl.getMaximum());
        volumeControl.setValue(dB);
      } catch (Exception e) {
        System.err.println("Could not set volume: " + e.getMessage());
      }
    }
  }
  public void cleanup() {
    stopBGM();
  }
}
