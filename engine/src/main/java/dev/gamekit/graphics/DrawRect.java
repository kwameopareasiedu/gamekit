package dev.gamekit.graphics;

/** {@link DrawRect} is a draw call which renders a <b>center-origin</b> rect */
public class DrawRect extends DrawRoundRect {
  public DrawRect(int x, int y, int width, int height, boolean filled) {
    super(x, y, width, height, 0, 0, filled);
  }
}
