package dev.gamekit.graphics;

/** {@link DrawCircle} is a draw call which renders a <b>center-origin</b> circle */
public class DrawCircle extends DrawOval {
  public DrawCircle(int x, int y, int diameter, boolean filled) {
    super(x, y, diameter, diameter, filled);
  }
}
