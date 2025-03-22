package dev.gamekit.ui;

import dev.gamekit.core.Renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Base class for all UI elements in the engine */
public abstract class Node {
  protected final Position position;
  protected final Size size;
  protected final Spacing padding;
  protected final Spacing margin;
  protected BufferedImage image;
  protected Graphics2D graphics;

  /** Creates a new node with default parameters */
  public Node() {
    position = new Position(0, 0);
    size = new Size(0, 0);
    padding = new Spacing(5);
    margin = new Spacing(0);
  }

  /**
   * Abstract method which the node uses to update itself on each frame.
   * The goal of this method is to compute the position, size and other
   * attributes needed for rendering.
   */
  public abstract void onUpdate();

  /**
   * Abstract method which returns the {@link BufferedImage} representing what this node looks like.
   * <p>
   * For container nodes, the children should be drawn on this image as well.
   * @return The {@link BufferedImage} representation of this node
   */
  public abstract BufferedImage getAppearance();

  /**
   * Renders the node's {@link #getAppearance() appearance}
   * on the current {@link dev.gamekit.core.Window Window}
   */
  public final void onRender() { Renderer.drawNode(this); }

  /**
   * Returns the position of the node
   * @return The node position
   */
  public Position getPosition() { return position; }

  /**
   * Returns the size of the node
   * @return The node size
   */
  public Size getSize() { return size; }

  /**
   * Returns the padding of the node
   * @return The node padding
   */
  public Spacing getPadding() { return padding; }

  /**
   * Returns the margin of the node
   * @return The node margin
   */
  public Spacing getMargin() { return margin; }
}
