package dev.gamekit.ui.widgets;

import dev.gamekit.utils.Constants;

import java.awt.*;

/**
 * A parent is a {@link Widget} which can contain one or more children.
 * <p>
 * Since a scene can only have one root widget, parents are responsible for
 * rendering their children unto themselves which in turn are rendered to their
 * parent or the window
 * <p>
 * Parents can be children of other parent widgets with no change to how layout
 * is performed due to the box-constraint model used.
 */
public abstract class Parent extends Widget {
  protected Color clearColor;

  public Parent() {
    clearColor = Constants.TRANSPARENT_COLOR;
  }

  /**
   * Renders the background of this parent before descendants are rendered
   * onto itself
   * <p>
   * By default, this clears the area of the {@link #computedBounds} with the
   * transparent color
   */
  protected void renderBackground(Graphics2D g) {
    g.setBackground(Constants.TRANSPARENT_COLOR);
    g.clearRect(0, 0, computedBounds.width, computedBounds.height);
  }
}
