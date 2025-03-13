package dev.gamekit.core;

import java.awt.*;

/** Renderer exposes methods which the draw anything on the game window */
public final class Renderer {
  private static final Color TRANSPARENT = new Color(0x00000000, true);
  private static final GraphicsState SAVED_STATE = new GraphicsState();
  private static final GraphicsState CURRENT_STATE = new GraphicsState();

  private static Graphics2D g;

  private Renderer() { }

  public static void setBackground(Color color) { CURRENT_STATE.bgColor = color; }

  public static void setStroke(Stroke stroke) { CURRENT_STATE.stroke = stroke; }

  public static void setPaint(Paint paint) { CURRENT_STATE.paint = paint; }

  public static void setColor(Color color) { CURRENT_STATE.color = color; }

  public static void setFont(Font font) { CURRENT_STATE.font = font; }

  public static void beginGroup() { CURRENT_STATE.beginGroup(); }

  public static void endGroup() { CURRENT_STATE.endGroup(); }

  public static void clear() {
    updateGraphicsObject();
    int x = 0, y = 0, w = Window.getInstance().getWidth(), h = Window.getInstance().getHeight();
    var pt = Camera.getInstance().transformPoint(x, y);
    g.fillRect(-pt.x, -pt.y, w, h);
    //    g.fillRect(x, y, w, h);
    resetGraphicsObject();
  }

  public static void line(int x1, int y1, int x2, int y2) {
    updateGraphicsObject();
    g.drawLine(x1, -y1, x2, -y2);
    resetGraphicsObject();
  }

  public static void lineV(int x, int y1, int y2) {
    line(x, y1, x, y2);
  }

  public static void lineH(int x1, int y, int x2) {
    line(x1, y, x2, y);
  }

  public static void fillRect(int x, int y, int width, int height) {
    rect(x, y, width, height, true);
  }

  public static void drawRect(int x, int y, int width, int height) {
    rect(x, y, width, height, false);
  }

  public static void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    roundRect(x, y, width, height, arcWidth, arcHeight, true);
  }

  public static void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    roundRect(x, y, width, height, arcWidth, arcHeight, false);
  }

  public static void fillOval(int x, int y, int width, int height) {
    oval(x, y, width, height, true);
  }

  public static void drawOval(int x, int y, int width, int height) {
    oval(x, y, width, height, false);
  }

  public static void fillCircle(int x, int y, int radius) {
    int diameter = 2 * radius;
    oval(x, y, diameter, diameter, true);
  }

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

  //  public static void text(Object text, int x, int y) {
  //    if (text == null) return;
  //
  //    //    INSTANCE.updateGraphicsObject();
  //    //    INSTANCE.g.drawString(text.toString(), x, -y);
  //    //    INSTANCE.resetGraphicsObject();
  //  }

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
