package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;

/**
 * A {@link Widget} which is an abstract base for creating custom widgets.
 * <p>
 * {@link Compose} delegates its layout and rendering to the supplied widget tree
 */
public abstract class Compose extends SingleChildParent {
  protected Compose(Widget child) {
    super(child);
  }

  @Override
  protected final void performLayout(Constraints constraints) {
    Widget child = getChild();
    child.performLayout(constraints);

    computedBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    child.computedBounds.setPosition(0, 0);
  }

  @Override
  public final void performRender(Graphics2D g) {
    getChild().performRender(g);
  }
}
