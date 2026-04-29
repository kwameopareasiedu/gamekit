package dev.gamekit.audio.attenuation;

import dev.gamekit.audio.AudioClip;
import dev.gamekit.audio.AudioListener;

/**
 * {@link AudioAttenuation} defines how the volume of {@link AudioClip} instances falls off as it moves away from the
 * {@link AudioListener}
 */
public abstract class AudioAttenuation {
  protected final double minDistance;
  protected final double maxDistance;

  public AudioAttenuation(double minDistance, double maxDistance) {
    this.minDistance = minDistance;
    this.maxDistance = maxDistance;
  }

  /** Returns a value between 0 and 1 representing the falloff based on the squared distance */
  public abstract double attenuate(double distance);
}
