package dev.gamekit.audio.shapes;

import dev.gamekit.audio.AudioClip3D;
import dev.gamekit.audio.AudioListener;
import dev.gamekit.utils.Vector;

/**
 * {@link AudioShape} models the area around an {@link AudioClip3D} in which attenuation occurs. It does this by
 * defining the min and max attenuation distances w.r.t some abstract shape (E.g. circle, box, capsule, etc.)
 * <p>
 * Subclasses must implement the abstract {@link #getDistance} which calculates the distance of the
 * {@link AudioClip3D} to the {@link AudioListener} w.r.t the subclass shape and clamped to the minimum and maximum
 * attenuation distances.
 */
public abstract class AudioShape {
  public final double minDistance;
  public final double maxDistance;

  protected final double distanceDiff;
  protected final double minDistanceSquared;
  protected final double maxDistanceSquared;
  protected final double squaredDistanceDiff;

  public AudioShape(double minDistance, double maxDistance) {
    if (minDistance < 0 || minDistance > maxDistance)
      throw new IllegalArgumentException("AudioShape maxDistance must be greater than minDistance");

    this.minDistance = minDistance;
    this.maxDistance = maxDistance;
    distanceDiff = maxDistance - minDistance;
    minDistanceSquared = minDistance * minDistance;
    maxDistanceSquared = maxDistance * maxDistance;
    squaredDistanceDiff = maxDistanceSquared - minDistanceSquared;
  }

  /**
   * Abstract method which computes the distance from the source and listener
   * based on the subclass shape type, clamped between the min and max
   * attenuation distances
   *
   * @param sourcePos   Position of the {@link AudioClip3D}
   * @param listenerPos Position of the {@link AudioListener}
   */
  public abstract double getDistance(Vector sourcePos, Vector listenerPos);
}
