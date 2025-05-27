package dev.gamekit.rendering;

/** {@link DrawRect} renders a <b>center-origin</b> rect to the window */
public class DrawRect extends DrawRoundRect {
  public DrawRect(int x, int y, int width, int height, boolean filled) {
    super(x, y, width, height, 0, 0, filled);
  }
}
