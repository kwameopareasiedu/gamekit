package dev.gamekit.core;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Singleton class which controls which part of
 * the game world is rendered in the {@link Window}
 */
public final class Camera {
  private static final Camera INSTANCE = new Camera();
  static final Point pointCache = new Point();
  private final AffineTransform transform;
  private double x = 0;
  private double y = 0;
  private double zoom = 1;

  /** Creates a new Camera instance */
  private Camera() {
    transform = new AffineTransform(1, 0, 0, -1, 0, 0);
  }

  /** Returns the current instance of the camera */
  public static Camera getInstance() { return INSTANCE; }

  public Point transformPoint(int x, int y) {
    pointCache.setLocation(x, y);
    transform.transform(pointCache, pointCache);
    return pointCache;
  }

  /**
   * Pan the camera such that point (x, y)
   * is at the center of the {@link Window}
   * @param x The x-coordinate of the point
   * @param y The y-coordinate of the point
   */
  public void lookAt(double x, double y) {
    this.x = x;
    this.y = -y;
  }

  /**
   * Sets the zoom level of the camera. The zoom
   * level is clamped to a minimum value of 1
   * @param zoom The zoom level
   */
  public void setZoom(double zoom) {
    this.zoom = Math.max(zoom, 1);
  }

  /**
   * Applies the camera's position and zoom
   * to the current window's transform matrix
   */
  void update() {
    Window window = Window.getInstance();
    int centerX = window.getCenterX(), centerY = window.getCenterY();
    transform.setTransform(zoom, 0, 0, zoom, centerX - x, centerY - y);
    window.getSceneGraphics().setTransform(transform);
  }
}
