package dev.gamekit.ui;

import dev.gamekit.core.Renderer;
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

  private Appearance appearance;

  /** Creates a new node with default parameters */
  public Node() {
    computedPosition = new Position(0, 0);
    intrinsicSize = new Size(0, 0);
    computedSize = new Size(0, 0);
  }

  /**
   * Abstract method which the node uses to lay itself out.
   * It receives a {@link Constraints} object which it must respect
   * when determining its size.
   * <p>
   * The goal of this method is to compute attributes needed for rendering.
   * @param constraints Constraints from the parent or window
   */
  public abstract void onLayout(Constraints constraints);

  /**
   * Creates and returns the {@link Appearance} object of the node.
   * The node renders its content onto the {@link Appearance#image}
   * @return The {@link BufferedImage} representation of this node
   */
  public Appearance getAppearance() {
    if (appearance == null ||
      appearance.image.getWidth() != computedSize.width ||
      appearance.image.getHeight() != computedSize.height) {
      LOGGER.debug("Creating appearance");
      BufferedImage image = new BufferedImage(computedSize.width, computedSize.height, BufferedImage.TYPE_INT_ARGB);
      appearance = new Appearance(image);
    }

    return appearance;
  }

  /**
   * Renders the node's {@link #getAppearance() appearance}
   * on the current {@link dev.gamekit.core.Window Window}
   * <p>
   * <i>This is only called if the node is at the root level of the scene's UI hierarchy</i>
   */
  public final void onRender() { Renderer.drawNode(this); }

  /**
   * Returns the computed position of the node
   * @return The computed position
   */
  public Position getComputedPosition() { return computedPosition; }

  /**
   * Returns the computed size of the node
   * @return The computed size
   */
  public Size getComputedSize() { return computedSize; }

  /**
   * Returns the intrinsic size of the node
   * @return The intrinsic size
   */
  public Size getIntrinsicSize() { return intrinsicSize; }

  /**
   * Appearance contains a {@link BufferedImage} and a
   * {@link Graphics2D} object which draws to the image
   */
  public static class Appearance {
    public final BufferedImage image;
    public final Graphics2D graphics;

    /** Creates a new appearance from the given image */
    private Appearance(BufferedImage image) {
      this.image = image;
      this.graphics = image.createGraphics();
    }
  }
}
