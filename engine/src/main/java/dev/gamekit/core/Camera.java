package dev.gamekit.core;

import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.geom.AffineTransform;

/**
 * {@link Camera} controls which part of the game world is visible in the {@link Window}.
 * It does this by manipulating the window's {@link AffineTransform} object
 */
public final class Camera {
  private static final Logger LOGGER = LogManager.getLogger(Camera.class);
  private static final Position POSITION_CACHE = new Position();
  private static final Bounds BOUNDS_CACHE = new Bounds();
  private static final AffineTransform TRANSFORM = new AffineTransform(1, 0, 0, -1, 0, 0);

  private static double x = 0;
  private static double y = 0;
  private static double zoom = 1;
  private static double invZoom = 1.0 / zoom;

  /** Returns the visible render bounds based on the camera's parameters */
  public static Bounds getRenderBounds() {
    Window window = Window.getInstance();
    int centerX = window.getDisplayCenterX();
    int centerY = window.getDisplayCenterY();

    BOUNDS_CACHE.set(
      (int) ((Camera.x - centerX) * invZoom),
      (int) ((Camera.y - centerY) * invZoom),
      (int) (window.getDisplayWidth() * invZoom),
      (int) (window.getDisplayHeight() * invZoom)
    );

    return BOUNDS_CACHE;
  }

  /** Transforms a screen-space point (sx,sy) into world-space position */
  public static Position screenToWorldPosition(double sx, double sy) {
    Window window = Window.getInstance();
    int centerX = window.getDisplayCenterX();
    int centerY = window.getDisplayCenterY();
    int wx = (int) (invZoom * (centerX - sx - Camera.x));
    int wy = (int) (invZoom * (centerY - sy - Camera.y));
    POSITION_CACHE.set(-wx, wy);
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

  /** Applies the camera's position and zoom to the current window's transform matrix */
  static void applyTransformation() {
    Window window = Window.getInstance();
    int centerX = window.getDisplayCenterX();
    int centerY = window.getDisplayCenterY();
    TRANSFORM.setTransform(zoom, 0, 0, zoom, centerX - x, centerY - y);
    window.getDisplayGraphics().setTransform(TRANSFORM);
  }
}
