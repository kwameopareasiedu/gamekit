package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

/** A {@link SingleChildParent} which forces the maximum constrained size on its child */
public class Expanded extends SingleChildParent {
  public Expanded(Widget child) {
    super(child);
  }

  public static Expanded create(Widget child) {
    return new Expanded(child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    computedBounds.setSize(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    intrinsicBounds.setSize(
      computedBounds.width,
      computedBounds.height
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
    );
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Expanded;
  }
}
