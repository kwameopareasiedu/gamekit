package dev.gamekit.core;

import dev.gamekit.utils.Position;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Camera controls which part of the game world is visible in the {@link Window}.
 * It does this by manipulating the window's {@link AffineTransform} object
 */
public final class Camera {
  private static final Point POINT_CACHE = new Point();
  private static final Position POSITION_CACHE = new Position();

  private static final AffineTransform transform = new AffineTransform(1, 0, 0, -1, 0, 0);
  private static double x = 0;
  private static double y = 0;
  private static double zoom = 1;
  private static double invZoom = 1.0 / zoom;

  /** Transforms a screen-space point (x,y) into world-space */
  public static Position screenToWorldPosition(int x, int y) {
    Window window = Window.getInstance();
    Position center = window.getCenter();
    int tx = (int) (invZoom * (center.x - x - Camera.x));
    int ty = (int) (invZoom * (center.y - y - Camera.y));
    POSITION_CACHE.set(-tx, ty);
    return POSITION_CACHE;
  }

  /** Transforms a point (x,y) relative to top-left origin into world-space */
  public static Position pointToWorldPosition(int x, int y) {
    POINT_CACHE.setLocation(x, y);
    transform.transform(POINT_CACHE, POINT_CACHE);
    POSITION_CACHE.set(POINT_CACHE);
    return POSITION_CACHE;
  }

  /** Pan the camera to center point (x, y) within the {@link Window} */
  public static void lookAt(double x, double y) {
    Camera.x = x;
    Camera.y = -y;
  }

  /** Sets the zoom level of the camera, clamped to a min of 1 */
  public static void setZoom(double zoom) {
    Camera.zoom = Math.max(zoom, 1);
    Camera.invZoom = 1.0 / Camera.zoom;
  }

  public static double getX() { return x; }

  public static double getY() { return y; }

  /**
   * Applies the camera's position and zoom to the current window's transform
   * matrix
   */
  static void update() {
    Window window = Window.getInstance();
    Position center = window.getCenter();
    transform.setTransform(zoom, 0, 0, zoom, center.x - x, center.y - y);
    window.getSceneGraphics().setTransform(transform);
  }
}
