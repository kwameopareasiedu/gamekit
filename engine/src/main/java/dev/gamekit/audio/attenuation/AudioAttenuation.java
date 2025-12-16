package dev.gamekit.audio.attenuation;

import dev.gamekit.audio.AudioClip3D;
import dev.gamekit.audio.AudioListener;

/**
 * {@link AudioAttenuation} defines how {@link AudioClip3D} falls off as it moves away from the
 * {@link AudioListener}
 */
public interface AudioAttenuation {
  /** Returns a value between 0 and 1 representing the falloff based on the distance */
  double attenuate(double distance, double minDistance, double maxDistance);
}
