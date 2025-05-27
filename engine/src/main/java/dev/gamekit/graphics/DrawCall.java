package dev.gamekit.graphics;

import java.awt.*;

/** {@link DrawCall} instructs the engine to render something on the window */
public abstract class DrawCall {
  public final void apply(Graphics2D g) {
    setup(g);
    draw(g);
    cleanup(g);
  }

  protected void setup(Graphics2D g) { }

  protected abstract void draw(Graphics2D g);

  protected void cleanup(Graphics2D g) { }
}
