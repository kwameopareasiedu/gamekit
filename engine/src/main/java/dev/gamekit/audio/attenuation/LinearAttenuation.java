package dev.gamekit.audio.attenuation;

import dev.gamekit.audio.AudioClip;
import dev.gamekit.audio.AudioListener;

/**
 * {@link LinearAttenuation} attenuates {@link AudioClip} at a constant uniform rate w.r.t the clip's distance from
 * the {@link AudioListener}
 */
public class LinearAttenuation extends AudioAttenuation {
  public LinearAttenuation(double minDistance, double maxDistance) {
    super(minDistance, maxDistance);
  }

  @Override
  public double attenuate(double distance) {
    return 1.0 - Math.min(distance - minDistance, maxDistance) / (maxDistance - minDistance);
  }
}
