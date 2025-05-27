package dev.gamekit.graphics;

import java.awt.*;

/** {@link DrawRoundRect} renders a <b>center-origin</b> rounded rectangle to the window */
public class DrawRoundRect extends DrawShape {
  private final int x, y;
  private final int width;
  private final int height;
  private final int arcWidth;
  private final int arcHeight;
  private final boolean filled;

  public DrawRoundRect(
    int x, int y, int width, int height, int arcWidth, int arcHeight, boolean filled
  ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.arcWidth = arcWidth;
    this.arcHeight = arcHeight;
    this.filled = filled;
  }

  @Override
  protected void draw(Graphics2D g) {
    int x0 = x - width / 2, y0 = y + height / 2;
    if (filled) g.fillRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    else g.drawRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
  }
}
