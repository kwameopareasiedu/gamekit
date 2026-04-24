package dev.gamekit.audio;

import static dev.gamekit.utils.GMath.clamp;

/**
 * An {@link AudioGroup} is a channel that feeds audio into the system's audio mixer. Input audio from all groups are
 * then mixed into the final output audio sent to the sound output devices.
 * <p>
 * {@link AudioGroup} provides the ability to adjust controls for all audio streams channelled through it. These
 * controls include volume, pan, speed, pitch and reverberation.
 * <p>
 * A common use-case is setting the volume of all background music and all sound effects in a game
 */
public class AudioGroup {
  public static final AudioGroup MUSIC = new AudioGroup("Music", 0.85);
  public static final AudioGroup EFFECTS = new AudioGroup("SFX", 0.75);

  private final String name;
  private boolean muted;
  private double volume;
  private double pitch;
  private double speed;
  private double reverb;

  public AudioGroup(String name, double volume) {
    this.name = name;
    this.volume = volume;
    this.muted = false;
    this.pitch = 1;
    this.speed = 1;
    this.reverb = 1;
  }

  /** Returns the name of this {@link AudioGroup} */
  public String getName() {
    return name;
  }

  /** Returns the mute status of this {@link AudioGroup} */
  boolean isMuted() {
    return muted;
  }

  /** Mutes/Unmutes this {@link AudioGroup}, affecting any associated {@link AudioClip} */
  public void setMuted(boolean muted) {
    this.muted = muted;
  }

  /** Returns the max volume of this {@link AudioGroup} */
  double getVolume() {
    return volume;
  }

  /** Sets the max volume of this {@link AudioGroup}, affecting any associated {@link AudioClip} */
  public void setVolume(double volume) {
    this.volume = clamp(volume, 0, 1);
  }
}
