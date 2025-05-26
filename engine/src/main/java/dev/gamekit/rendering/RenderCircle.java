package dev.gamekit.rendering;

/**
 * {@link RenderCircle} is a shape render call to render <b>center-origin</b> circle to the
 * window
 */
public class RenderCircle extends RenderOval {
  public RenderCircle(int x, int y, int radius, boolean filled) {
    super(x, y, radius, radius, filled);
  }

  public RenderCircle(int x, int y, int radius) {
    super(x, y, radius, radius, false);
  }
}
