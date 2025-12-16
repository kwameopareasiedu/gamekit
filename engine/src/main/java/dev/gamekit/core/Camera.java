package dev.gamekit.core;

import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.geom.AffineTransform;

/**
 * {@link Camera} controls which part of the game world is visible in the {@link Window}.
 * <p>
 * It does this by manipulating the window's {@link AffineTransform} object
 */
public final class Camera {
  private static final Logger LOGGER = LogManager.getLogger(Camera.class);
  private static final Position INT_POSITION_CACHE = new Position();
  private static final Vector DOUBLE_POSITION_CACHE = new Vector();
  private static final Bounds BOUNDS_CACHE = new Bounds();
  private static final AffineTransform TRANSFORM = new AffineTransform(1, 0, 0, -1, 0, 0);

  private static double x = 0;
  private static double y = 0;
  private static double zoom = 1;
  private static double invZoom = 1.0 / zoom;

  /** Returns the visible render bounds based on the camera's parameters */
  public static Bounds getRenderBounds() {
    Window.Info windowInfo = Window.getInstance().getInfo();
    int centerX = windowInfo.displayCenterX();
    int centerY = windowInfo.displayCenterY();

    BOUNDS_CACHE.set(
      (int) ((Camera.x - centerX) * invZoom),
      (int) ((Camera.y - centerY) * invZoom),
      (int) (windowInfo.displayWidth() * invZoom),
      (int) (windowInfo.displayHeight() * invZoom)
    );

    return BOUNDS_CACHE;
  }

  /** Transforms a screen-space point (sx,sy) into world-space position */
  public static Vector screenToWorldPosition(double sx, double sy) {
    Window.Info windowInfo = Window.getInstance().getInfo();
    int centerX = windowInfo.displayCenterX();
    int centerY = windowInfo.displayCenterY();
    double wx = invZoom * (centerX - sx - Camera.x);
    double wy = invZoom * (centerY - sy - Camera.y);
    DOUBLE_POSITION_CACHE.set(-wx, wy);
    return DOUBLE_POSITION_CACHE;
  }

  /** Transforms a world-space point (sx,sy) into screen-space position */
  public static Position worldToScreenPosition(double wx, double wy) {
    Window.Info windowInfo = Window.getInstance().getInfo();
    int centerX = windowInfo.displayCenterX();
    int centerY = windowInfo.displayCenterY();
    int sx = (int) (centerX - wx * zoom - Camera.x);
    int sy = (int) (centerY - wy * zoom - Camera.y);
    INT_POSITION_CACHE.set(-sx, sy);
    return INT_POSITION_CACHE;
  }

  /** Pan the camera to center point (x, y) within the {@link Window} */
  public static void lookAt(double x, double y) {
    Camera.x = x;
    Camera.y = -y;
  }

  /** Sets the zoom level of the camera, clamped to a min of 1 */
  public static void setZoom(double zoom) {
    Camera.zoom = Math.max(zoom, dev.gamekit.utils.Math.EPSILON);
    Camera.invZoom = 1.0 / Camera.zoom;
  }

  public static double getX() { return x; }

  public static double getY() { return y; }

  /** Applies the camera's position and zoom to the current window's transform matrix */
  static void updateWindow() {
    Window window = Window.getInstance();
    Window.Info windowInfo = Window.getInstance().getInfo();
    int centerX = windowInfo.displayCenterX();
    int centerY = windowInfo.displayCenterY();
    TRANSFORM.setTransform(zoom, 0, 0, zoom, centerX - x, centerY - y);
    window.getDisplayGraphics().setTransform(TRANSFORM);
  }

  static void reset() {
    x = y = 0;
    zoom = 1;
  }
}
