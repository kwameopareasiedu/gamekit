package dev.gamekit.audio.shapes;

import dev.gamekit.utils.Vector;

import static dev.gamekit.utils.Math.clamp;

public class AudioShapeCircle extends AudioShape {
  public AudioShapeCircle(double minDistance, double maxDistance) {
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
