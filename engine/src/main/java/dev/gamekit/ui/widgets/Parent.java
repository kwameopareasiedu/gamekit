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
   * Since {@link Parent parents} contain one or more children, subclasses should be able to
   * control when their own appearance is rendered before that of the child or children
   */
  protected void renderAppearance(Graphics2D g) { /* No-op */ }
}
