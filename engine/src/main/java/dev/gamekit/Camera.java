package dev.gamekit;

import java.awt.geom.AffineTransform;

public final class Camera {
  final AffineTransform transform;

  public Camera() {
    transform = new AffineTransform(1, 0, 0, 1, 0, 0);
  }

  public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
    transform.setTransform(m00, m10, m01, m11, m02, m12);
  }
}
