package dev.gamekit.rendering;

import java.awt.*;

/**
 * {@link RenderCall} represents the instructions and data needed to manipulate pixels of the
 * window using its {@link Graphics2D} object.
 */
public abstract class RenderCall {
  public final void apply(Graphics2D g) {
    setup(g);
    render(g);
    cleanup(g);
  }

  protected void setup(Graphics2D g) { }

  protected abstract void render(Graphics2D g);

  protected void cleanup(Graphics2D g) { }
}
