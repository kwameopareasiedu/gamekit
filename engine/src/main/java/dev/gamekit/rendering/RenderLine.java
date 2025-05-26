package dev.gamekit.rendering;

import java.awt.*;

/** {@link RenderLine} is a shape render call to render a line to the window */
public class RenderLine extends ShapeRenderCall {
  private final int x1, y1, x2, y2;

  public RenderLine(int x1, int y1, int x2, int y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  @Override
  protected void performRender(Graphics2D g) {
    g.drawLine(x1, -y1, x2, -y2);
  }
}
