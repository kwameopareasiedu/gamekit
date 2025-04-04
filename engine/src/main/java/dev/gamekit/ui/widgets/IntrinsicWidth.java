package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.util.Objects;

/**
 * A {@link SingleChildParent} which limits the computed width of its single
 * child to the child's intrinsic width
 */
public class IntrinsicWidth extends SingleChildParent {
  protected IntrinsicWidth(Widget child) {
    super(child);
  }

  public static IntrinsicWidth create(Widget child) {
    return new IntrinsicWidth(child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      child.intrinsicBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
    );
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof IntrinsicWidth paddingWidget) {
      return Objects.equals(child, paddingWidget.child);
    }

    return false;
  }
}
