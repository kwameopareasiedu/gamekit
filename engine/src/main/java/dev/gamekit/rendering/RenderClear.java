package dev.gamekit.rendering;

import dev.gamekit.core.Camera;
import dev.gamekit.utils.Bounds;

import java.awt.*;

/** {@link RenderClear} is a render call to clear the visible bounds of the window */
public class RenderClear extends RenderCall {
  @Override
  protected void performRender(Graphics2D g) {
    Bounds rb = Camera.getRenderBounds();
    g.clearRect((int) rb.x, (int) rb.y, (int) rb.width, (int) rb.height);
  }
}
