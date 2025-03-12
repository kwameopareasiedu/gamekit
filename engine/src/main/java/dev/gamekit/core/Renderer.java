package dev.gamekit.core;

import java.awt.*;

import static dev.gamekit.utils.MathUtils.toInt;

/** Renderer exposes methods which the draw anything on the game window */
public class Renderer {
  private static Renderer instance;

  private final Graphics2D g;
  private final GraphicsState initialState;
  private final GraphicsState currentState;

  Renderer(Graphics2D g) {
    this.g = g;
    initialState = new GraphicsState();
    currentState = new GraphicsState();

    initialState.save(g);
    Renderer.instance = this;
  }

  public static void clearState() { instance.currentState.reset(); }

  public static void setBackground(Color color) { instance.currentState.bgColor = color; }

  public static void setStroke(Stroke stroke) { instance.currentState.stroke = stroke; }

  public static void setPaint(Paint paint) { instance.currentState.paint = paint; }

  public static void setColor(Color color) { instance.currentState.color = color; }

  public static void setFont(Font font) { instance.currentState.font = font; }

  public static void clear(int x, int y, int width, int height) {
    instance.applyCurrentState();
    instance.g.fillRect(x, y, width, height);
    instance.restoreInitialState();
  }

  public static void line(int x1, int y1, int x2, int y2) {
    instance.applyCurrentState();
    instance.g.drawLine(x1, y1, x2, y2);
    instance.restoreInitialState();
  }

  public static void rect(int x, int y, int width, int height, boolean fill) {
    instance.applyCurrentState();
    int x0 = x - toInt(0.5 * width), y0 = y - toInt(0.5 * height);
    if (fill) instance.g.fillRect(x0, y0, width, height);
    instance.g.drawRect(x0, y0, width, height);
    instance.restoreInitialState();
  }

  public static void rect(int x1, int y1, int width, int height) {
    rect(x1, y1, width, height, false);
  }

  public static void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill) {
    instance.applyCurrentState();
    int x0 = x - toInt(0.5 * width), y0 = y - toInt(0.5 * height);
    if (fill) instance.g.fillRoundRect(x0, y0, width, height, arcWidth, arcHeight);
    instance.g.drawRoundRect(x0, y0, width, height, arcWidth, arcHeight);
    instance.restoreInitialState();
  }

  public static void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    roundRect(x, y, width, height, arcWidth, arcHeight, false);
  }

  void applyCurrentState() {
    if (currentState.stroke == null)
      currentState.stroke = initialState.stroke;
    currentState.apply(g);
  }

  void restoreInitialState() { initialState.apply(g); }

  private static class GraphicsState {
    Color bgColor;
    Stroke stroke;
    Paint paint;
    Color color;
    Font font;

    private void save(Graphics2D g) {
      this.bgColor = g.getBackground();
      this.stroke = g.getStroke();
      this.paint = g.getPaint();
      this.color = g.getColor();
      this.font = g.getFont();
    }

    private void apply(Graphics2D g) {
      g.setBackground(this.bgColor);
      g.setStroke(this.stroke);
      g.setPaint(this.paint);
      g.setColor(this.color);
      g.setFont(this.font);
    }

    private void reset() {
      bgColor = null;
      stroke = null;
      paint = null;
      color = null;
      font = null;
    }
  }
}
