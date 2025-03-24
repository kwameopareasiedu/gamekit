package dev.gamekit.ui;

import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Size;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Base class for all UI elements in the engine */
public abstract class Node {
  private static final Logger LOGGER = LogManager.getLogger();

  protected final Position computedPosition;
  protected final Size intrinsicSize;
  protected final Size computedSize;
  protected Node parent;
  protected Constraints constraints;

  private Appearance appearance;

  /** Creates a new node with default parameters */
  public Node() {
    computedPosition = new Position(0, 0);
    intrinsicSize = new Size(0, 0);
    computedSize = new Size(0, 0);
    parent = null;
  }

  /**
   * Computes the layout for the node, respecting the given {@link Constraints} object
   * <p>
   * This is called by either the parent node if this node is a child
   * or by the {@link UI} if it is the root node.
   * <p>
   * The goal of this method is to set the {@link #computedSize} and
   * {@link #computedPosition} which is used during rendering phase.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override
   * the {@link #onLayout(Constraints)} method instead to perform their layout
   */
  public final void computeLayout(Constraints constraints) {
    this.constraints = constraints;
    this.onLayout(constraints);
  }

  /**
   * Delegate method which subclasses use to compute their layout when requested.
   * <p>
   * It is passed a {@link Constraints} object which it <b>must respect</b>.
   */
  protected abstract void onLayout(Constraints constraints);

  /**
   * Returns the {@link Appearance} of this node for rendering
   * <p>
   * This method is {@code final} and delegates the actual drawing to
   * {@link #onRender(Graphics2D)}.
   */
  public final Appearance getAppearance() {
    if (appearance == null ||
      appearance.image.getWidth() != computedSize.width ||
      appearance.image.getHeight() != computedSize.height) {
      LOGGER.debug("Creating appearance");
      BufferedImage image = new BufferedImage(computedSize.width, computedSize.height, BufferedImage.TYPE_INT_ARGB);
      appearance = new Appearance(image);
    }

    onRender(appearance.graphics);

    return appearance;
  }

  /**
   * Delegate method which subclasses use to draw themselves when requested.
   * <p>
   * It is passed a {@link Graphics2D} object from the appearance for drawing.
   */
  protected abstract void onRender(Graphics2D g);

  public Position getComputedPosition() { return computedPosition; }

  public Size getComputedSize() { return computedSize; }

  public Size getIntrinsicSize() { return intrinsicSize; }

  public void setParent(Node parent) { this.parent = parent; }

  /**
   * Appearance contains a {@link BufferedImage} and a
   * {@link Graphics2D} object which draws to the image
   */
  public static class Appearance {
    public final BufferedImage image;
    public final Graphics2D graphics;

    private Appearance(BufferedImage image) {
      this.image = image;
      this.graphics = image.createGraphics();
    }
  }
}
