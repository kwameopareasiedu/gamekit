package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Bounds;
import dev.gamekit.ui.Constraints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A widget is an abstract representation of a portion of
 * a {@link dev.gamekit.core.Scene Scene's} user interface.
 * <p>
 * Subclasses must implement the {@link #performLayout} and
 * {@link #performRender(Graphics2D)} to compute their position and size
 * <p>
 * Widget layout is based on the
 * <a href="https://docs.flutter.dev/ui/layout/constraints">box-constraint</a>
 * model which is used in Flutter, where constraints go down the tree,
 * size go up and parents set positions
 */
public abstract class Widget {
  private static final boolean DEBUG_DRAW = false;
  private static final BasicStroke DEBUG_OUTLINE_STROKE = new BasicStroke(
    2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
  );

  protected final Logger logger = LogManager.getLogger(getClass());
  protected final Bounds computedBounds;
  protected final Bounds previousBounds;
  protected final Bounds intrinsicBounds;
  protected Widget parent;
  protected boolean needsRepaint;

  private Appearance appearance;

  public Widget() {
    computedBounds = new Bounds(0, 0, 0, 0);
    previousBounds = new Bounds(0, 0, 0, 0);
    intrinsicBounds = new Bounds(0, 0, 0, 0);
    parent = null;
  }

  public Bounds getComputedBounds() { return computedBounds; }

  public Widget getParent() { return parent; }

  public void setParent(Widget parent) { this.parent = parent; }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Widget widget) {
      return stateEquals(widget);
    }

    return false;
  }

  /** Delegate method which returns {@code true} if this widget has the same state as {@code widget} */
  protected abstract boolean stateEquals(Widget widget);

  /**
   * Computes the layout for the widget
   * <p>
   * This is called by either the parent widget or window and receives
   * the {@link Constraints} from its parent or the window, and the
   * resulting computed size must always respect this constraint
   * <p>
   * The goal of this method is to set the {@link #computedBounds}
   * which controls where on the screen the widget is rendered.
   * <p>
   * Since this method is marked as {@code final}, subclasses should
   * override the {@link #performLayout} method instead
   * to perform their layout
   */
  public final void computeLayout(Constraints constraints) {
    performLayout(constraints);
    needsRepaint = !computedBounds.equals(previousBounds);
  }

  /**
   * Delegate method which performs the actual layout and is passed the
   * constraints from {@link #computeLayout(Constraints)}.
   */
  protected abstract void performLayout(Constraints constraints);

  /**
   * Returns the {@link Appearance} of this widget which is used for rendering
   * <p>
   * This method is {@code final} and delegates the actual drawing to
   * {@link #performRender(Graphics2D)}.
   */
  public final Appearance getAppearance() {
    if (appearance == null || needsRepaint) {
      BufferedImage image = new BufferedImage(
        computedBounds.width,
        computedBounds.height,
        BufferedImage.TYPE_INT_ARGB
      );

      appearance = new Appearance(image);
    }

    performRender(appearance.graphics);

    if (DEBUG_DRAW) {
      appearance.graphics.setColor(Color.CYAN);
      appearance.graphics.setStroke(DEBUG_OUTLINE_STROKE);
      appearance.graphics.drawRect(0, 0, computedBounds.width, computedBounds.height);
    }

    previousBounds.set(computedBounds);
    needsRepaint = false;
    return appearance;
  }

  /**
   * Delegate method which performs the actual rendering and is
   * passed a {@link Graphics2D} object from the internal
   * {@link BufferedImage}.
   */
  protected abstract void performRender(Graphics2D g);

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
