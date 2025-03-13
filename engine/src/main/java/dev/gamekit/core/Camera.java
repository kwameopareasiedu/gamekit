package dev.gamekit.core;

import java.awt.*;
import java.awt.geom.AffineTransform;

public final class Camera {
  static final Point pointCache = new Point();
  private static final Camera INSTANCE = new Camera();

  private final AffineTransform transform;
  private double x = 0;
  private double y = 0;
  private double zoom = 1;

  private Camera() {
    transform = new AffineTransform(1, 0, 0, -1, 0, 0);
  }

  public static Camera getInstance() { return INSTANCE; }

  public Point transformPoint(int x, int y) {
    pointCache.setLocation(x, y);
    transform.transform(pointCache, pointCache);
    return pointCache;
  }

  public void lookAt(double x, double y) {
    this.x = x;
    this.y = -y;
  }

  public void setZoom(double zoom) { this.zoom = zoom; }

  void update() {
    Window window = Window.getInstance();
    int centerX = window.getCenterX(), centerY = window.getCenterY();
    transform.setTransform(zoom, 0, 0, zoom, centerX - x, centerY - y);
    window.getSceneGraphics().setTransform(transform);
  }
}
