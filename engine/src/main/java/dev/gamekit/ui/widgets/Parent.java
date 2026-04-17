package dev.gamekit.ui.widgets;

import java.awt.*;

/**
 * A parent is a {@link Widget} which can contain one or more children.
 * <p>
 * Parents can be children of other parent widgets with no change to how layout is performed due to the
 * box-constraint model used.
 */
public abstract class Parent extends Widget {
  public Parent(String key, Config config) {
    super(key, config);
  }

  /**
   * Called at the start of {@link #render} before {@link #renderSelf}.
   * Subclasses can override this to perform any pre-render actions
   */
  protected void preRender(Graphics2D g) { /* No-op */ }

  /**
   * Called after {@link #preRender} method to draw the parent's appearance before its child/children are
   * also rendered on top of it.
   */
  protected void renderSelf(Graphics2D g) { /* No-op */ }

  /**
   * Called at the end of {@link #render} to perform any post-render actions.
   * <p>
   * This is an ideal place to release any resources setup during {@link #preRender}
   */
  protected void postRender(Graphics2D g) { /* No-op */ }
}
