package dev.gamekit.core;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Static class which provides draw methods to draw on the window scene. */
public final class Renderer {
  private static final GraphicsState INITIAL_STATE = new GraphicsState();
  private static final GraphicsState CURRENT_STATE = new GraphicsState();

  private static Graphics2D g;

  private Renderer() { }

  public static void setBackground(Color color) { CURRENT_STATE.bgColor = color; }

  public static void setStroke(Stroke stroke) { CURRENT_STATE.stroke = stroke; }

  public static void setPaint(Paint paint) { CURRENT_STATE.paint = paint; }

  public static void setColor(Color color) { CURRENT_STATE.color = color; }

  /**
   * By default, the state (I.e. fg/bg color, stroke or paint) of the renderer
   * resets after each draw method.
   * <p>
   * This disables this default behaviour and preserves the state until
   * {@link #endGroup()} is called
   */
  public static void beginGroup() { CURRENT_STATE.preserve(); }

  /**
   * Restores the default behaviour of clearing the state after each draw method.
   * @see #beginGroup()
   */
  public static void endGroup() { CURRENT_STATE.discard(); }

  /** Clears the {@link Window} scene buffer with current state background color */
  public static void clear() {
    applyGraphicsState();
    int x = 0, y = 0, w = Window.getInstance().getDisplayWidth(), h = Window.getInstance().getDisplayHeight();
    var pt = Camera.screenToWorldPoint(x, y);
    g.clearRect(pt.x, -pt.y, w, h);
    resetGraphicsState();
  }

  /** Draws a line from {@code (x1, y1)} to {@code (x2, y2)} */
  public static void drawLine(int x1, int y1, int x2, int y2) {
    applyGraphicsState();
    g.drawLine(x1, -y1, x2, -y2);
    resetGraphicsState();
  }

  /** Draws a vertical line from {@code (x, y1)} to {@code (x, y2)} */
  public static void drawLineV(int x, int y1, int y2) {
    drawLine(x, y1, x, y2);
  }

  /** Draws a horizontal line from {@code (x1, y)} to {@code (x2, y)} */
  public static void drawLineH(int x1, int y, int x2) {
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
   * Fills a <b>center-origin</b> rounded rect at (x, y) with width and height
   * and corner width and height
   */
  public static void fillRoundRect(int x, int y, int width, int height, int cornerWidth, int cornerHeight) {
    roundRect(x, y, width, height, cornerWidth, cornerHeight, true);
  }

  /**
   * Draws a <b>center-origin</b> rounded rect at (x, y) with width and height
   * and corner width and height
   */
  public static void drawRoundRect(int x, int y, int width, int height, int cornerWidth, int cornerHeight) {
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
   * Draws a <b>center-origin</b> {@link BufferedImage} at (x, y) with width
   * and height. The image is scaled to fit within the provided bounds
   */
  public static void drawImage(BufferedImage img, int x, int y, int width, int height) {
    applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    int x1 = x0 + width, y1 = y0 - height;
    g.drawImage(img, x0, -y0, x1, -y1, 0, 0, img.getWidth(), img.getHeight(), null);
    resetGraphicsState();
  }

  /**
   * Internal method for drawing and filling ovals
   * @see #drawOval(int, int, int, int)
   * @see #fillOval(int, int, int, int)
   * @see #drawCircle(int, int, int)
   * @see #fillCircle(int, int, int)
   */
  private static void oval(int x, int y, int width, int height, boolean fill) {
    applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillOval(x0, -y0, width, height);
    else g.drawOval(x0, -y0, width, height);
    resetGraphicsState();
  }

  /**
   * Internal method for drawing and filling rects
   * @see #drawRect(int, int, int, int)
   * @see #fillRect(int, int, int, int)
   */
  private static void rect(int x, int y, int width, int height, boolean fill) {
    applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillRect(x0, -y0, width, height);
    else g.drawRect(x0, -y0, width, height);
    resetGraphicsState();
  }

  /**
   * Internal method for drawing and filling rounded rects
   * @see #drawRoundRect(int, int, int, int, int, int)
   * @see #fillRoundRect(int, int, int, int, int, int)
   */
  private static void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill) {
    applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    else g.drawRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    resetGraphicsState();
  }

  /** Applies the current graphics state to the current graphics object */
  private static void applyGraphicsState() {
    g = Window.getInstance().getSceneGraphics();
    INITIAL_STATE.save(g);
    CURRENT_STATE.apply(g);
  }

  /** Restores the initial graphics state to the current graphics object */
  private static void resetGraphicsState() {
    INITIAL_STATE.apply(g);
    CURRENT_STATE.reset();
    INITIAL_STATE.reset();
  }

  /**
   * G2DState maintains the state of a {@link Graphics2D} object.
   * <p>
   * This state includes the following:
   * <ul>
   *   <li>Foreground {@link Color color}</li>
   *   <li>Background {@link Color color}</li>
   *   <li>{@link Stroke Stroke}</li>
   *   <li>{@link Paint Paint}</li>
   * </ul>
   */
  private static class GraphicsState {
    public static final Stroke DEFAULT_STROKE = new BasicStroke(
      1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
    );

    Color bgColor;
    Stroke stroke;
    Paint paint;
    Color color;
    Font font;
    private boolean preserve;

    /** Copies the state of a {@link Graphics2D} object */
    void save(Graphics2D g) {
      bgColor = g.getBackground();
      stroke = g.getStroke();
      paint = g.getPaint();
      color = g.getColor();
      font = g.getFont();
    }

    /** Applies the internal state to a {@link Graphics2D} object */
    void apply(Graphics2D g) {
      g.setBackground(bgColor);
      g.setStroke(stroke != null ? stroke : DEFAULT_STROKE);
      g.setPaint(paint);
      g.setColor(color);
      g.setFont(font);
    }

    /**
     * Activates the preserve mode of this state. In preserve mode, calls to
     * {@link #reset()} are ignored
     * @see #discard()
     */
    void preserve() {
      reset();
      preserve = true;
    }

    /**
     * Deactivates the preserve mode of this state.
     * @see #preserve()
     */
    void discard() {
      preserve = false;
      reset();
    }

    /**
     * Resets the internal state to null, if preserve mode is not <b>active</b>
     * @see #preserve()
     * @see #discard()
     */
    void reset() {
      if (preserve) return;

      bgColor = null;
      stroke = null;
      paint = null;
      color = null;
      font = null;
    }
  }
}
