package gui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * BGM Player untuk memainkan background music dengan looping
 * Mendukung HomeBase BGM dan Map BGM
 */
public class BGMPlayer {
  private static BGMPlayer instance;
  private Clip currentClip;
  private boolean isEnabled = true;
  private boolean isPlayingHomeBaseBGM = false;
  private boolean isPlayingMapBGM = false;
  private String currentBGMPath = null;

  // BGM paths
  private static final String HOME_BASE_BGM_PATH_1 = "assets/bgm/HomeBase.wav"; // File baru yang sudah diconvert -
                                                                                // primary
  private static final String HOME_BASE_BGM_PATH_2 = "assets/bgm/med.wav"; // Fallback file
  private static final String MAP_BGM_PATH = "assets/bgm/MapUtama.wav"; // Map BGM

  // ...existing code...

  private BGMPlayer() {
    // Private constructor for singleton
  }

  /**
   * Get singleton instance
   */
  public static BGMPlayer getInstance() {
    if (instance == null) {
      instance = new BGMPlayer();
    }
    return instance;
  }

  /**
   * Play HomeBase BGM with looping
   */
  public void playHomeBaseBGM() {
    if (!isEnabled || isPlayingHomeBaseBGM) {
      System.out
          .println("HomeBase BGM not starting: enabled=" + isEnabled + ", already playing=" + isPlayingHomeBaseBGM);
      return;
    }

    System.out.println("Attempting to start HomeBase BGM...");

    // Try to play wav file first, then fallback
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

    playBGM(bgmPath, true, false);
  }

  /**
   * Play Map BGM with looping
   */
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

    playBGM(MAP_BGM_PATH, false, true);
  }

  /**
   * Internal method to play BGM
   */
  private void playBGM(String bgmPath, boolean isHomeBase, boolean isMap) {
    System.out.println("Found BGM file: " + bgmPath);

    try {
      // Stop any currently playing BGM
      stopBGM();

      // Load and play the BGM
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(bgmPath));
      currentClip = AudioSystem.getClip();
      currentClip.open(audioInputStream);

      // Loop continuously
      currentClip.loop(Clip.LOOP_CONTINUOUSLY);
      currentClip.start();

      // Set flags
      isPlayingHomeBaseBGM = isHomeBase;
      isPlayingMapBGM = isMap;
      currentBGMPath = bgmPath;

      String bgmType = isHomeBase ? "HomeBase" : "Map";
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

  /**
   * Stop any currently playing BGM
   */
  public void stopBGM() {
    if (currentClip != null && currentClip.isRunning()) {
      currentClip.stop();
      currentClip.close();
      currentClip = null;
      System.out.println("Stopped BGM: " + currentBGMPath);
    }
    isPlayingHomeBaseBGM = false;
    isPlayingMapBGM = false;
    currentBGMPath = null;
  }

  /**
   * Stop HomeBase BGM specifically
   */
  public void stopHomeBaseBGM() {
    if (isPlayingHomeBaseBGM) {
      stopBGM();
    }
  }

  /**
   * Stop Map BGM specifically
   */
  public void stopMapBGM() {
    if (isPlayingMapBGM) {
      stopBGM();
    }
  }

  /**
   * Check if HomeBase BGM is currently playing
   */
  public boolean isPlayingHomeBaseBGM() {
    return isPlayingHomeBaseBGM && currentClip != null && currentClip.isRunning();
  }

  /**
   * Check if Map BGM is currently playing
   */
  public boolean isPlayingMapBGM() {
    return isPlayingMapBGM && currentClip != null && currentClip.isRunning();
  }

  /**
   * Enable or disable BGM globally
   */
  public void setEnabled(boolean enabled) {
    this.isEnabled = enabled;
    if (!enabled) {
      stopBGM();
    }
  }

  /**
   * Check if BGM is enabled
   */
  public boolean isEnabled() {
    return isEnabled;
  }

  /**
   * Set volume (0.0 to 1.0)
   */
  public void setVolume(float volume) {
    if (currentClip != null && currentClip.isOpen()) {
      try {
        FloatControl volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);

        // Convert linear volume to dB
        float dB = (float) (Math.log(Math.max(volume, 0.0001)) / Math.log(10.0) * 20.0);

        // Clamp to control's range
        dB = Math.max(dB, volumeControl.getMinimum());
        dB = Math.min(dB, volumeControl.getMaximum());

        volumeControl.setValue(dB);
      } catch (Exception e) {
        System.err.println("Could not set volume: " + e.getMessage());
      }
    }
  }

  /**
   * Cleanup resources when application closes
   */
  public void cleanup() {
    stopBGM();
  }
}
