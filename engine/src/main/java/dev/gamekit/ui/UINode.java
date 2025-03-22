package dev.gamekit.ui;

import dev.gamekit.core.Renderer;

import java.awt.image.BufferedImage;

/** Base class for all UI elements in the engine */
public abstract class UINode {
  protected final Spacing padding;
  protected final Spacing margin;
  protected int x, y;
  protected int width, height;

  public UINode() {
    x = y = 0;
    width = height = 1;
    padding = new Spacing(5);
    margin = new Spacing(0);
  }

  public abstract BufferedImage getAppearance();

  public abstract void onUpdate();

  public void onRender() {
    Renderer.drawUiNode(this);
  }

  public int getX() { return x; }

  public int getY() { return y; }

  public int getWidth() { return width; }

  public int getHeight() { return height; }

  public Spacing getPadding() { return padding; }

  public Spacing getMargin() { return margin; }
}
