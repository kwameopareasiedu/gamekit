package dev.gamekit.core;

import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.GMath;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * {@link Camera} controls which part of the game world is visible in the {@link Window}.
 * <p>
 * It does this by manipulating the window's {@link AffineTransform} object
 */
public final class Camera {
  private static final Logger LOGGER = LogManager.getLogger(Camera.class);

  static Camera current;

  private final Position intPositionCache = new Position();
  private final Vector doublePositionCache = new Vector();
  private final Bounds boundsCache = new Bounds();
  private final AffineTransform transform = new AffineTransform(1, 0, 0, -1, 0, 0);
  private double x = 0;
  private double y = 0;
  private double zoom = 1;
  private double invZoom = 1.0 / zoom;

  /** Returns the currently active camera, which would be that of the running {@link Scene} */
  public static Camera getCurrent() {
    return current;
  }

  /** Returns the visible render bounds based on the camera's parameters */
  public Bounds getRenderBounds() {
    Window win = Window.getInstance();

    boundsCache.set(
      (int) ((x - win.getCenterX()) * invZoom),
      (int) ((y - win.getCenterY()) * invZoom),
      (int) (win.getDisplayWidth() * invZoom),
      (int) (win.getDisplayHeight() * invZoom)
    );

    return boundsCache;
  }

  /** Transforms the screen-space point (sx,sy) into world-space position */
  public Vector screenToWorldPosition(int sx, int sy) {
    Window win = Window.getInstance();
    double wx = invZoom * (win.getCenterX() - sx - x);
    double wy = invZoom * (win.getCenterY() - sy - y);
    doublePositionCache.set(-wx, wy);
    return doublePositionCache;
  }

  /** Transforms the world-space point (wx,wy) into screen-space position */
  public Position worldToScreenPosition(double wx, double wy) {
    Window win = Window.getInstance();
    int sx = (int) (win.getCenterX() + wx * zoom - x);
    int sy = (int) (win.getCenterY() - wy * zoom - y);
    intPositionCache.set(sx, sy);
    return intPositionCache;
  }

  /** Pan the camera to center point (x, y) within the {@link Window} */
  public void lookAt(double x, double y) {
    this.x = x;
    this.y = -y;
  }

  /** Sets the zoom level of the camera, clamped to a min of 1 */
  public void setZoom(double zoom) {
    this.zoom = Math.max(zoom, GMath.EPSILON);
    this.invZoom = 1.0 / zoom;
  }

  /** Returns the {@code x} translation of the {@link Camera} */
  public double getX() {
    return x;
  }

  /** Returns the {@code y} translation of the {@link Camera} */
  public double getY() {
    return -y;
  }

  /** Applies the camera's position and zoom to the current window's transform matrix */
  void updateWindowTransform() {
    Window win = Window.getInstance();
    Graphics2D g = win.getDisplayGraphics();
    Bounds rb = getRenderBounds();
    transform.setTransform(zoom, 0, 0, zoom, win.getCenterX() - x, win.getCenterY() - y);
    g.setTransform(transform);
    g.setClip((int) rb.x, (int) rb.y, (int) rb.width, (int) rb.height);
  }
}
