package dev.gamekit.ui.widgets;

import java.awt.*;

/**
 * A parent is a {@link Widget} which can contain one or more children.
 * <p>
 * Parents can be children of other parent widgets with no change to how layout is performed due
 * to the box-constraint model used.
 */
public abstract class Parent extends Widget {
  public Parent(Config... config) {
    super(config);
  }

  /**
   * Called in the {@link #render} method to provide the parent's appearance before its
   * child/children are also rendered on top of it.
   */
  protected void renderAppearance(Graphics2D g) { /* No-op */ }

//  public static abstract class ParentConfig extends WidgetConfig { }
}
