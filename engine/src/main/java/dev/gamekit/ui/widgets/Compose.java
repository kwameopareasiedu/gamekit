package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.utils.Constraints;

/**
 * A {@link Widget} which is an abstract base for creating custom widgets.
 * <p>
 * {@link Compose} delegates its layout and rendering to the supplied widget tree
 */
@WidgetBuilder
public abstract class Compose extends SingleChildParent {
  protected Compose(Widget child) {
    super(new ComposeConfig(), child);
  }

  @Override
  protected final void performLayout(Constraints constraints) {
    Widget child = getChild();
    child.layout(constraints);

    computedBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    child.computedBounds.setPosition(0, 0);
  }
}
