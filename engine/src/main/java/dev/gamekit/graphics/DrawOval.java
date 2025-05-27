package dev.gamekit.graphics;

import java.awt.*;

/** {@link DrawOval} renders a <b>center-origin</b> oval to the window */
public class DrawOval extends DrawShape {
  private final int x, y;
  private final int width;
  private final int height;
  private final boolean filled;

  public DrawOval(int x, int y, int width, int height, boolean filled) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.filled = filled;
  }

  @Override
  protected void draw(Graphics2D g) {
    int x0 = x - width / 2, y0 = y + height / 2;
    if (filled) g.fillOval(x0, -y0, width, height);
    else g.drawOval(x0, -y0, width, height);
  }
}
