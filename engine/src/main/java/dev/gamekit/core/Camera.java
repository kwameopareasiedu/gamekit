package dev.gamekit.core;

import dev.gamekit.utils.Position;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Singleton class which controls which part of
 * the game world is rendered in the {@link Window}
 */
public final class Camera {
  private static final Point POINT_CACHE = new Point();
  private static final Position POSITION_CACHE = new Position();

  private static final AffineTransform transform = new AffineTransform(1, 0, 0, -1, 0, 0);
  private static double x = 0;
  private static double y = 0;
  private static double zoom = 1;

  /** Transforms a screen-space point (x,y) to the world-space */
  public static Position screenToWorldPoint(int x, int y) {
    Window window = Window.getInstance();
    POINT_CACHE.setLocation(x - window.getRenderWidth(), -y);
    transform.transform(POINT_CACHE, POINT_CACHE);
    POSITION_CACHE.set(POINT_CACHE);
    return POSITION_CACHE;
  }

  /** Pan the camera such that point (x, y) is at the center of the {@link Window} */
  public static void lookAt(double x, double y) {
    Camera.x = x;
    Camera.y = -y;
  }

  /** Sets the zoom level of the camera, clamped to a min of 1 */
  public static void setZoom(double zoom) { Camera.zoom = Math.max(zoom, 1); }

  public static double getX() { return x; }

  public static double getY() { return y; }

  /** Applies the camera's position and zoom to the current window's transform matrix */
  static void update() {
    Window window = Window.getInstance();
    int centerX = window.getCenterX(), centerY = window.getCenterY();
    transform.setTransform(zoom, 0, 0, zoom, centerX - x, centerY - y);
    window.getSceneGraphics().setTransform(transform);
  }
}
