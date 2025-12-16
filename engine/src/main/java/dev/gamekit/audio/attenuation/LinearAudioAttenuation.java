package dev.gamekit.audio.attenuation;

import dev.gamekit.audio.AudioClip3D;
import dev.gamekit.audio.AudioListener;

/**
 * {@link LinearAudioAttenuation} defines how {@link AudioClip3D} falls off as it moves away from the
 * {@link AudioListener}
 */
public class LinearAudioAttenuation implements AudioAttenuation {
  @Override
  public double attenuate(double distance, double minDistance, double maxDistance) {
    return 1.0 - (distance - minDistance) / (maxDistance - minDistance);
  }
}
