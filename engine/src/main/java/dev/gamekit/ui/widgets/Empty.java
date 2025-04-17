package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;

import java.awt.*;

/**
 * A {@link Leaf} with zero size which renders nothing. Empty should be used
 * in places where {@code null} would have been preferable
 */
public class Empty extends Leaf {
  @SafeVarargs
  public static Empty create(Param<? super EmptyParam>... params) {
    return new Empty();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    computedBounds.setSize(
      constraints.constrainWidth(0),
      constraints.constrainHeight(0)
    );
  }

  @Override
  protected void performRender(Graphics2D g) { /* No-op */ }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Empty;
  }
}
