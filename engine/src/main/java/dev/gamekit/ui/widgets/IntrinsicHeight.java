package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.util.Objects;

/**
 * A {@link SingleChildParent} which limits the computed height of its single
 * child to the child's intrinsic height
 */
public class IntrinsicHeight extends SingleChildParent {
  protected IntrinsicHeight(Widget child) {
    super(child);
  }

  public static IntrinsicHeight create(Widget child) {
    return new IntrinsicHeight(child);
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
      child.computedBounds.width,
      child.intrinsicBounds.height
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
    if (widget instanceof IntrinsicHeight paddingWidget) {
      return Objects.equals(child, paddingWidget.child);
    }

    return false;
  }
}
