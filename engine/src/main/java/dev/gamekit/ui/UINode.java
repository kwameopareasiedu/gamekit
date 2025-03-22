package dev.gamekit.ui;

import dev.gamekit.core.Renderer;

import java.awt.image.BufferedImage;

/** Base class for all UI elements in the engine */
public abstract class UINode {
  protected final Position position;
  protected final Size size;
  protected final Spacing padding;
  protected final Spacing margin;

  public UINode() {
    position = new Position(0, 0);
    size = new Size(0, 0);
    padding = new Spacing(5);
    margin = new Spacing(0);
  }

  public abstract BufferedImage getAppearance();

  public abstract void onUpdate();

  public void onRender() { Renderer.drawUiNode(this); }

  public Position getPosition() { return position; }

  public Size getSize() { return size; }

  public Spacing getPadding() { return padding; }

  public Spacing getMargin() { return margin; }
}
