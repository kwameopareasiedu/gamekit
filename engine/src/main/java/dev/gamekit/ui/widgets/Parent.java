package dev.gamekit.ui.widgets;

import java.awt.*;

/**
 * A parent is a {@link Widget} which can contain one or more children.
 * <p>
 * Parents can be children of other parent widgets with no change to how layout
 * is performed due to the box-constraint model used.
 */
public abstract class Parent extends Widget {
  /**
   * Renders the appearance of this parent
   * <p>
   * By default, this clears the area of the {@link #computedBounds} with the
   * transparent color
   */
  protected void renderAppearance(Graphics2D g) { /* No-op */ }
}
