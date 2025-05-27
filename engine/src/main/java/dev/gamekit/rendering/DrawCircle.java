package dev.gamekit.rendering;

/** {@link DrawCircle} renders a <b>center-origin</b> circle to the window */
public class DrawCircle extends DrawOval {
  public DrawCircle(int x, int y, int diameter, boolean filled) {
    super(x, y, diameter, diameter, filled);
  }
}
