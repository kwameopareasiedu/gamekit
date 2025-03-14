package dev.gamekit.core;

import java.awt.*;

/** Static class containing all supported draw calls of the engine. */
public final class Renderer {
  private static final Color TRANSPARENT = new Color(0x00000000, true);
  private static final GraphicsState SAVED_STATE = new GraphicsState();
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
  public static void beginGroup() { CURRENT_STATE.beginGroup(); }

  /** Ends a previously called {@code beginGroup()} */
  public static void endGroup() { CURRENT_STATE.endGroup(); }

  /** Fills the viewport with a specified color */
  public static void clear() {
    updateGraphicsObject();
    int x = 0, y = 0, w = Window.getInstance().getWidth(), h = Window.getInstance().getHeight();
    var pt = Camera.getInstance().transformPoint(x, y);
    g.fillRect(-pt.x, -pt.y, w, h);
    resetGraphicsObject();
  }

  /**
   * Draws a line from {@code P1:(x1, y1)} to {@code P2:(x2, y2)}
   * @param x1 The x-coordinate of P1
   * @param y1 The y-coordinate of P1
   * @param x2 The x-coordinate of P2
   * @param y2 The y-coordinate of P2
   */
  public static void drawLine(int x1, int y1, int x2, int y2) {
    updateGraphicsObject();
    g.drawLine(x1, -y1, x2, -y2);
    resetGraphicsObject();
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

  private static void oval(int x, int y, int width, int height, boolean fill) {
    updateGraphicsObject();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillOval(x0, -y0, width, height);
    else g.drawOval(x0, -y0, width, height);
    resetGraphicsObject();
  }

  private static void rect(int x, int y, int width, int height, boolean fill) {
    updateGraphicsObject();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillRect(x0, -y0, width, height);
    else g.drawRect(x0, -y0, width, height);
    resetGraphicsObject();
  }

  private static void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill) {
    updateGraphicsObject();
    int x0 = x - width / 2, y0 = y + height / 2;
    if (fill) g.fillRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    else g.drawRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    resetGraphicsObject();
  }

  private static void updateGraphicsObject() {
    g = Window.getInstance().getSceneGraphics();
    SAVED_STATE.copyFrom(g);
    CURRENT_STATE.applyTo(g);
  }

  private static void resetGraphicsObject() {
    SAVED_STATE.applyTo(g);
    CURRENT_STATE.reset();
    SAVED_STATE.reset();
  }

  private static class GraphicsState {
    private static final Stroke DEFAULT_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    boolean preserve;
    Color bgColor;
    Stroke stroke;
    Paint paint;
    Color color;
    Font font;

    private void beginGroup() {
      reset();
      preserve = true;
    }

    private void endGroup() {
      reset();
      preserve = false;
    }

    private void copyFrom(Graphics2D g) {
      bgColor = g.getBackground();
      stroke = g.getStroke();
      paint = g.getPaint();
      color = g.getColor();
      font = g.getFont();
    }

    private void applyTo(Graphics2D g) {
      g.setBackground(bgColor);
      g.setStroke(stroke != null ? stroke : DEFAULT_STROKE);
      g.setPaint(paint);
      g.setColor(color);
      g.setFont(font);
    }

    private void reset() {
      if (preserve) return;

      bgColor = null;
      stroke = null;
      paint = null;
      color = null;
      font = null;
    }
  }
}
