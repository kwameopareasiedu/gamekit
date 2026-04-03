package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/** A {@link Leaf} with no size and appearance, used in places where {@code null} would have been preferable */
@WidgetBuilder
public class Empty extends Leaf {
  public Empty() {
    super(new EmptyConfig());
  }

  public static Empty create() {
    return new Empty();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(0, 0);

    computedBounds.setSize(
      constraints.constrainWidth(0),
      constraints.constrainHeight(0)
    );
  }

  @Override
  protected void performRender(Graphics2D g) { /* No-op */ }
}
