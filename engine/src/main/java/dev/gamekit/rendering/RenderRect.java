package dev.gamekit.rendering;

/** {@link RenderRect} is a shape render call to render <b>center-origin</b> rect to the window */
public class RenderRect extends RenderRoundRect {
  public RenderRect(int x, int y, int width, int height, boolean filled) {
    super(x, y, width, height, 0, 0, filled);
  }

  public RenderRect(int x, int y, int width, int height) {
    super(x, y, width, height, 0, 0, false);
  }
}
