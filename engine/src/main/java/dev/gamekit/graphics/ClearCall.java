package dev.gamekit.graphics;

import dev.gamekit.core.Camera;
import dev.gamekit.utils.Bounds;

import java.awt.*;

/** {@link ClearCall} clears the visible bounds of the window */
public class ClearCall extends DrawCall<ClearCall> {
  private final Color color;

  public ClearCall(Color color) {
    this.color = color;
  }

  @Override
  protected void draw(Graphics2D g) {
    Bounds rb = Camera.getRenderBounds();
    Color prevColor = g.getBackground();

    g.setBackground(color);
    g.clearRect((int) rb.x, (int) rb.y, (int) rb.width, (int) rb.height);
    g.setBackground(prevColor);
  }
}
