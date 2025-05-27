package dev.gamekit.rendering;

import java.awt.*;

/**
 * {@link RenderRoundRect} is a shape render call to render <b>center-origin</b> rounded rectangle to
 * the window
 */
public class RenderRoundRect extends ShapeRenderCall {
  private final int x, y;
  private final int width;
  private final int height;
  private final int arcWidth;
  private final int arcHeight;
  private final boolean filled;

  public RenderRoundRect(
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
  protected void render(Graphics2D g) {
    int x0 = x - width / 2, y0 = y + height / 2;
    if (filled) g.fillRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    else g.drawRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
  }
}
