package dev.gamekit.audio;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@link AudioGroup} are attached to multiple a group of {@link AudioClip} objects in order to
 * group control their properties
 * <p>
 * A common use-case is setting the volume of all background music and all sound effects in a game
 */
public class AudioGroup {
  public static final AudioGroup MUSIC = new AudioGroup("Music", 0.85);
  public static final AudioGroup EFFECTS = new AudioGroup("SFX", 0.75);

  private final String name;
  private boolean muted;
  private double maxVolume;

  public AudioGroup(String name, double maxVolume) {
    this.name = name;
    this.maxVolume = maxVolume;
    this.muted = false;
  }

  public String getName() { return name; }

  public boolean isMuted() { return muted; }

  public void setMuted(boolean muted) { this.muted = muted; }

  public double getMaxVolume() { return maxVolume; }

  public void setMaxVolume(double maxVolume) {
    this.maxVolume = clamp(maxVolume, 0, 1);
  }
}
