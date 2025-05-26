package dev.gamekit.rendering;

import java.awt.*;

/**
 * {@link RenderCall} represents the instructions and data needed to manipulate pixels of the
 * window using its {@link Graphics2D} object.
 */
public abstract class RenderCall {
  public final void render(Graphics2D g) {
    preRender(g);
    performRender(g);
    postRender(g);
  }

  protected void preRender(Graphics2D g) { }

  protected abstract void performRender(Graphics2D g);

  protected void postRender(Graphics2D g) { }
}
