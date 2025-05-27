package dev.gamekit.rendering;

/**
 * {@link RenderCircle} is a shape render call to render <b>center-origin</b> circle to the
 * window
 */
public class RenderCircle extends RenderOval {
  public RenderCircle(int x, int y, int diameter, boolean filled) {
    super(x, y, diameter, diameter, filled);
  }
}
