package dev.gamekit.audio;

/**
 * {@link AudioAttenuation} defines how {@link AudioClip3D} falls off as it moves away from the
 * {@link AudioListener}
 */
public interface AudioAttenuation {
  AudioAttenuation LINEAR = (distance, minDistance, maxDistance) ->
    1.0 - (distance - minDistance) / (maxDistance - minDistance);

  /** Returns a value between 0 and 1 representing the falloff based on the distance */
  double attenuate(double distance, double minDistance, double maxDistance);
}
