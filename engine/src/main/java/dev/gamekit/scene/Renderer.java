package dev.gamekit.scene;

import java.awt.*;

/** Renderer exposes methods which the draw anything on the game window */
public class Renderer {
  static final GraphicsState SAVED_STATE = new GraphicsState();
  static final GraphicsState CURRENT_STATE = new GraphicsState();
  static Graphics2D g;

  private Renderer() { }

  public static void setBackground(Color color) { CURRENT_STATE.bgColor = color; }

  public static void setStroke(Stroke stroke) { CURRENT_STATE.stroke = stroke; }

  public static void setPaint(Paint paint) { CURRENT_STATE.paint = paint; }

  public static void setColor(Color color) { CURRENT_STATE.color = color; }

  public static void setFont(Font font) { CURRENT_STATE.font = font; }

  public static void drawLine(int x1, int y1, int x2, int y2) { }

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

    private void restore(Graphics2D g) {
      g.setBackground(this.bgColor);
      g.setStroke(this.stroke);
      g.setPaint(this.paint);
      g.setColor(this.color);
      g.setFont(this.font);
    }
  }
}
