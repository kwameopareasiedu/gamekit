package dev.gamekit.audio;

/**
 * An interface which defines the falloff of sound as a {@link AudioClip3D}
 * moves away from the {@link AudioListener}
 */
public interface AudioAttenuation {
  AudioAttenuation LINEAR = (distance, minDistance, maxDistance) ->
    1.0 - (distance - minDistance) / (maxDistance - minDistance);

  /**
   * Returns a value between 0 and 1 representing the falloff of the
   * intensity based on the distance
   */
  double attenuate(double distance, double minDistance, double maxDistance);
}
