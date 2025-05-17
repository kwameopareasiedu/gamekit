package dev.gamekit.core;

import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Math.degToRad;

/** {@link Renderer} provides draw methods to draw on the current {@link Window} */
public final class Renderer {
  private static final GraphicsState DEFAULT_STATE = new GraphicsState();
  private static final GraphicsState CURRENT_STATE = new GraphicsState();

  private Renderer() { }

  public static void setBackground(Color color) { CURRENT_STATE.bgColor = color; }

  public static void setStroke(Stroke stroke) { CURRENT_STATE.stroke = stroke; }

  public static void setPaint(Paint paint) { CURRENT_STATE.paint = paint; }

  public static void setColor(Color color) { CURRENT_STATE.color = color; }

  public static void resetOptions() { CURRENT_STATE.reset(); }

  /** Clears the {@link Window} scene buffer with current state background color */
  public static void clear() {
    Graphics2D g = applyGraphicsState();
    Bounds rb = Camera.getRenderBounds();
    g.clearRect((int) rb.x, (int) rb.y, (int) rb.width, (int) rb.height);
    resetGraphicsState(g);
  }

  /** Draws a line from {@code (x1, y1)} to {@code (x2, y2)} */
  public static void drawLine(int x1, int y1, int x2, int y2) {
    Graphics2D g = applyGraphicsState();
    g.drawLine(x1, -y1, x2, -y2);
    resetGraphicsState(g);
  }

  /** Draws a vertical line from {@code (x, y1)} to {@code (x, y2)} */
  public static void drawLineV(int x, int y1, int y2) {
    drawLine(x, y1, x, y2);
  }

  /** Draws a horizontal line from {@code (x1, y)} to {@code (x2, y)} */
  public static void drawLineH(int x1, int x2, int y) {
    drawLine(x1, y, x2, y);
  }

  /** Fills a <b>center-origin</b> rect at (x, y) with width and height */
  public static void fillRect(int x, int y, int width, int height) {
    rect(x, y, width, height, true);
  }

  /** Draws a <b>center-origin</b> rect at (x, y) with width and height */
  public static void drawRect(int x, int y, int width, int height) {
    rect(x, y, width, height, false);
  }

  /**
   * Fills a <b>center-origin</b> rounded rect at (x, y) with width and height and corner width
   * and height
   */
  public static void fillRoundRect(
    int x, int y, int width, int height, int cornerWidth, int cornerHeight
  ) {
    roundRect(x, y, width, height, cornerWidth, cornerHeight, true);
  }

  /**
   * Draws a <b>center-origin</b> rounded rect at (x, y) with width and height and corner width
   * and height
   */
  public static void drawRoundRect(
    int x, int y, int width, int height, int cornerWidth, int cornerHeight
  ) {
    roundRect(x, y, width, height, cornerWidth, cornerHeight, false);
  }

  /** Fills a <b>center-origin</b> oval at (x, y) with width and height */
  public static void fillOval(int x, int y, int width, int height) {
    oval(x, y, width, height, true);
  }

  /** Draws a <b>center-origin</b> oval at (x, y) with width and height */
  public static void drawOval(int x, int y, int width, int height) {
    oval(x, y, width, height, false);
  }

  /** Fills a <b>center-origin</b> circle at (x, y) with radius */
  public static void fillCircle(int x, int y, int radius) {
    int diameter = 2 * radius;
    oval(x, y, diameter, diameter, true);
  }

  /** Draws a <b>center-origin</b> circle at (x, y) with radius */
  public static void drawCircle(int x, int y, int radius) {
    int diameter = 2 * radius;
    oval(x, y, diameter, diameter, false);
  }

  /**
   * Draws a <b>center-origin</b> {@link BufferedImage} at (x, y) with width and height.
   * The image is scaled to fit within the provided bounds
   */
  public static void drawImage(
    BufferedImage img, int x, int y, int width, int height
  ) {
    Graphics2D g = applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    int x1 = x0 + width, y1 = y0 - height;
    g.drawImage(img, x0, -y0, x1, -y1, 0, 0, img.getWidth(), img.getHeight(), null);
    resetGraphicsState(g);
  }

  /**
   * Rotates the {@link Graphics2D} object by {@code deg} about point {@code (x, y)}, performs
   * the operations defined by {@code action} and restores the rotation of the {@link Graphics2D}
   * object afterward
   */
  public static void withRotation(
    int x, int y, double deg,
    Runnable action
  ) {
    Graphics2D g = Window.getInstance().getSceneGraphics();
    double rad = degToRad(deg);

    g.translate(x, -y);
    g.rotate(rad);
    g.translate(-x, y);

    action.run();

    g.translate(x, -y);
    g.rotate(-rad);
    g.translate(-x, y);
  }

  /**
   * Internal method for drawing and filling ovals
   * @see #drawOval(int, int, int, int)
   * @see #fillOval(int, int, int, int)
   * @see #drawCircle(int, int, int)
   * @see #fillCircle(int, int, int)
   */
  private static void oval(int x, int y, int width, int height, boolean fill) {
    Graphics2D g = applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillOval(x0, -y0, width, height);
    else g.drawOval(x0, -y0, width, height);
    resetGraphicsState(g);
  }

  /**
   * Internal method for drawing and filling rects
   * @see #drawRect(int, int, int, int)
   * @see #fillRect(int, int, int, int)
   */
  private static void rect(int x, int y, int width, int height, boolean fill) {
    Graphics2D g = applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillRect(x0, -y0, width, height);
    else g.drawRect(x0, -y0, width, height);
    resetGraphicsState(g);
  }

  /**
   * Internal method for drawing and filling rounded rects
   * @see #drawRoundRect(int, int, int, int, int, int)
   * @see #fillRoundRect(int, int, int, int, int, int)
   */
  private static void roundRect(
    int x, int y, int width, int height,
    int arcWidth, int arcHeight, boolean fill
  ) {
    Graphics2D g = applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    else g.drawRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    resetGraphicsState(g);
  }

  /** Applies the current graphics state to the current graphics object */
  private static Graphics2D applyGraphicsState() {
    Bounds rb = Camera.getRenderBounds();
    Graphics2D g = Window.getInstance().getSceneGraphics();
    g.setClip((int) rb.x, (int) rb.y, (int) rb.width, (int) rb.height);
    CURRENT_STATE.apply(g);
    return g;
  }

  /** Restores the initial graphics state to the current graphics object */
  private static void resetGraphicsState(Graphics2D g) {
    g.setClip(null);
    DEFAULT_STATE.apply(g);
    CURRENT_STATE.reset();
  }

  /** G2DState maintains the state of a {@link Graphics2D} object */
  private static class GraphicsState {
    public static final Stroke DEFAULT_STROKE = new BasicStroke(
      1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
    );

    Color bgColor;
    Stroke stroke;
    Paint paint;
    Color color;
    Font font;
    Shape clip;

    /** Applies the internal state to a {@link Graphics2D} object */
    void apply(Graphics2D g) {
      g.setBackground(bgColor);
      g.setStroke(stroke != null ? stroke : DEFAULT_STROKE);
      g.setPaint(paint);
      g.setColor(color);
      g.setFont(font);
      g.setClip(clip);
    }

    void reset() {
      bgColor = null;
      stroke = null;
      paint = null;
      color = null;
      font = null;
      clip = null;
    }
  }
}
