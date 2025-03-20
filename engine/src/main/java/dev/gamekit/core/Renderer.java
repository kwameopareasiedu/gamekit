package dev.gamekit.core;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Static class which provides draw methods to draw on the window scene. */
public final class Renderer {
  private static final GraphicsState INITIAL_STATE = new GraphicsState();
  private static final GraphicsState CURRENT_STATE = new GraphicsState();

  private static Graphics2D g;

  private Renderer() { }

  /**
   * Sets the background color for the next draw call
   * @param color {@link Color} The background color to use for the next draw call
   */
  public static void setBackground(Color color) { CURRENT_STATE.bgColor = color; }

  /**
   * Sets the stroke for the next draw call
   * @param stroke {@link Stroke} The stroke object to use for the next draw call
   */
  public static void setStroke(Stroke stroke) { CURRENT_STATE.stroke = stroke; }

  /**
   * Sets the paint for the next draw call
   * @param paint {@link Paint} The paint object to use for the next draw call
   */
  public static void setPaint(Paint paint) { CURRENT_STATE.paint = paint; }

  /**
   * Sets the color for the next draw call
   * @param color {@link Color} The foreground color to use for the next draw call
   */
  public static void setColor(Color color) { CURRENT_STATE.color = color; }

  /**
   * Sets the font for the next draw call
   * @param font {@link Font} The font to use for the next draw call
   */
  public static void setFont(Font font) { CURRENT_STATE.font = font; }

  /**
   * Configures the renderer to not reset options after next draw call.
   * <p>
   * Useful for multiple draw calls which share similar options
   */
  public static void beginGroup() { CURRENT_STATE.preserve(); }

  /** Ends a previously called {@code beginGroup()} */
  public static void endGroup() { CURRENT_STATE.discard(); }

  /** Fills the viewport with a specified color */
  public static void clear() {
    applyGraphicsState();
    int x = 0, y = 0, w = Window.getInstance().getWidth(), h = Window.getInstance().getHeight();
    var pt = Camera.getInstance().transformPoint(x, y);
    g.fillRect(-pt.x, -pt.y, w, h);
    resetGraphicsState();
  }

  /**
   * Draws a line from {@code P1:(x1, y1)} to {@code P2:(x2, y2)}
   * @param x1 The x-coordinate of P1
   * @param y1 The y-coordinate of P1
   * @param x2 The x-coordinate of P2
   * @param y2 The y-coordinate of P2
   */
  public static void drawLine(int x1, int y1, int x2, int y2) {
    applyGraphicsState();
    g.drawLine(x1, -y1, x2, -y2);
    resetGraphicsState();
  }

  /**
   * Draws a vertical line from {@code P1:(x, y1)} to {@code P2:(x, y2)}
   * @param x  The x-coordinate of the line
   * @param y1 The y-coordinate of start point
   * @param y2 The y-coordinate of the end point
   */
  public static void drawLineV(int x, int y1, int y2) {
    drawLine(x, y1, x, y2);
  }

  /**
   * Draws a horizontal line from {@code P1:(x1, y)} to {@code P2:(x2, y)}
   * @param x1 The x-coordinate of start point
   * @param x2 The x-coordinate of the end point
   * @param y  The y-coordinate of the line
   */
  public static void drawLineH(int x1, int y, int x2) {
    drawLine(x1, y, x2, y);
  }

  /**
   * Fills a center-origin rect at (x, y) with width and height
   * @param x      The x-coordinate of the rect center
   * @param y      The y-coordinate of the rect center
   * @param width  The width of the rect
   * @param height The height of the rect
   */
  public static void fillRect(int x, int y, int width, int height) {
    rect(x, y, width, height, true);
  }

  /**
   * Draws a center-origin rect at (x, y) with width and height
   * @param x      The x-coordinate of the rect center
   * @param y      The y-coordinate of the rect center
   * @param width  The width of the rect
   * @param height The height of the rect
   */
  public static void drawRect(int x, int y, int width, int height) {
    rect(x, y, width, height, false);
  }

  /**
   * Fills a center-origin rounded rect at (x, y) with width and height and arc radii
   * @param x         The x-coordinate of the rect center
   * @param y         The y-coordinate of the rect center
   * @param width     The width of the rect
   * @param height    The height of the rect
   * @param arcWidth  The width of the corner arc
   * @param arcHeight The height of the corner arc
   */
  public static void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    roundRect(x, y, width, height, arcWidth, arcHeight, true);
  }

  /**
   * Draws a center-origin rounded rect at (x, y) with width and height and arc radii
   * @param x         The x-coordinate of the rect center
   * @param y         The y-coordinate of the rect center
   * @param width     The width of the rect
   * @param height    The height of the rect
   * @param arcWidth  The width of the corner arc
   * @param arcHeight The height of the corner arc
   */
  public static void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    roundRect(x, y, width, height, arcWidth, arcHeight, false);
  }

  /**
   * Fills a center-origin oval at (x, y) with width and height
   * @param x      The x-coordinate of the oval center
   * @param y      The y-coordinate of the oval center
   * @param width  The width of the oval
   * @param height The height of the oval
   */
  public static void fillOval(int x, int y, int width, int height) {
    oval(x, y, width, height, true);
  }

  /**
   * Draws a center-origin oval at (x, y) with width and height
   * @param x      The x-coordinate of the oval center
   * @param y      The y-coordinate of the oval center
   * @param width  The width of the oval
   * @param height The height of the oval
   */
  public static void drawOval(int x, int y, int width, int height) {
    oval(x, y, width, height, false);
  }

  /**
   * Fills a center-origin circle at (x, y) with radius
   * @param x      The x-coordinate of the circle
   * @param y      The y-coordinate of the circle
   * @param radius The radius of the circle
   */
  public static void fillCircle(int x, int y, int radius) {
    int diameter = 2 * radius;
    oval(x, y, diameter, diameter, true);
  }

  /**
   * Draws a center-origin circle at (x, y) with radius
   * @param x      The x-coordinate of the circle
   * @param y      The y-coordinate of the circle
   * @param radius The radius of the circle
   */
  public static void drawCircle(int x, int y, int radius) {
    int diameter = 2 * radius;
    oval(x, y, diameter, diameter, false);
  }

  /**
   * Draws a center-origin {@link BufferedImage} at (x, y).
   * The image is scaled down to fit within the bounds of the origin and dimensions
   * @param img    The image to draw
   * @param x      The x-coordinate of the circle
   * @param y      The y-coordinate of the circle
   * @param width  The screen width of the image
   * @param height The screen height of the image
   */
  public static void drawImage(BufferedImage img, int x, int y, int width, int height) {
    applyGraphicsState();
    int x0 = x - width / 2, y0 = y + height / 2;
    int x1 = x0 + width, y1 = y0 - height;
    g.drawImage(img, x0, -y0, x1, -y1, 0, 0, img.getWidth(), img.getHeight(), null);
    resetGraphicsState();
  }

  /**
   * Internal method for drawing or filling ovals
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
   * Internal method for drawing or filling rects
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
   * Internal method for drawing or filling rounded rects
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
    g = Window.getInstance().getGraphics();
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

    /**
     * Copies the state of a {@link Graphics2D} object
     * @param g The Graphics2D object
     */
    void save(Graphics2D g) {
      bgColor = g.getBackground();
      stroke = g.getStroke();
      paint = g.getPaint();
      color = g.getColor();
      font = g.getFont();
    }

    /**
     * Applies the internal state to a {@link Graphics2D} object
     * @param g The Graphics2D object
     */
    void apply(Graphics2D g) {
      g.setBackground(bgColor);
      g.setStroke(stroke != null ? stroke : DEFAULT_STROKE);
      g.setPaint(paint);
      g.setColor(color);
      g.setFont(font);
    }

    /**
     * Activates the preserve mode of this state.
     * <p>
     * In preserve mode, calls to {@link #reset()} are ignored
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
     * Resets the internal state to null.
     * <p>
     * If preserve mode is activated, this does nothing
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
