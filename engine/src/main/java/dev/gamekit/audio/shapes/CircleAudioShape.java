package dev.gamekit.audio.shapes;

import dev.gamekit.audio.AudioClip3D;
import dev.gamekit.utils.Vector;

import static dev.gamekit.utils.Math.clamp;

/** A {@link AudioShape} which defines a circular attenuation area around {@link AudioClip3D} */
public class CircleAudioShape extends AudioShape {
  public CircleAudioShape(double minDistance, double maxDistance) {
    super(minDistance, maxDistance);
  }

  @Override
  public double getDistance(Vector sourcePos, Vector listenerPos) {
    double clampedSquaredDistance = clamp(
      Vector.squaredDistance(sourcePos, listenerPos),
      minDistanceSquared, maxDistanceSquared
    );

    return minDistance + (clampedSquaredDistance - minDistanceSquared) /
      squaredDistanceDiff * distanceDiff;
  }
}
