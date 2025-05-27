package dev.gamekit.rendering;

import dev.gamekit.core.Camera;
import dev.gamekit.utils.Bounds;

import java.awt.*;

/** {@link RenderClear} is a render call to clear the visible bounds of the window */
public class RenderClear extends RenderCall {
  private final Color color;

  public RenderClear(Color color) {
    this.color = color;
  }

  @Override
  protected void render(Graphics2D g) {
    Bounds rb = Camera.getRenderBounds();
    Color prevColor = g.getBackground();

    g.setBackground(color);
    g.clearRect((int) rb.x, (int) rb.y, (int) rb.width, (int) rb.height);
    g.setBackground(prevColor);
  }
}
