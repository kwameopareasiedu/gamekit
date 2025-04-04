package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;

/**
 * A {@link Widget} with zero size which renders nothing. Empty should be used
 * in places where {@code null} would have been preferable
 */
public class Empty extends Widget {
  public static Empty create() {
    return new Empty();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    computedBounds.setSize(0, 0);
  }

  @Override
  protected void performRender(Graphics2D g) { /* No-op */ }

  @Override
  protected boolean stateEquals(Widget widget) {
    return widget instanceof Empty;
  }
}
