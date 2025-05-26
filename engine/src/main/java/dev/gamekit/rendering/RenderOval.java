package dev.gamekit.rendering;

import java.awt.*;

/** {@link RenderOval} is a shape render call to render <b>center-origin</b> oval to the window */
public class RenderOval extends ShapeRenderCall {
  private final int x, y;
  private final int width;
  private final int height;
  private final boolean filled;

  public RenderOval(int x, int y, int width, int height, boolean filled) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.filled = filled;
  }

  public RenderOval(int x, int y, int width, int height) {
    this(x, y, width, height, false);
  }

  @Override
  protected void performRender(Graphics2D g) {
    int x0 = x - width / 2, y0 = y + height / 2;
    if (filled) g.fillOval(x0, -y0, width, height);
    else g.drawOval(x0, -y0, width, height);
  }
}
