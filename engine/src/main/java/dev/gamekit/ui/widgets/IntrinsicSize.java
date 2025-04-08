package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Axis;

import java.util.Objects;

/**
 * A {@link SingleChildParent} which limits the computed size of its single
 * child to the child's intrinsic size in the specified {@link Axis}
 */
public class IntrinsicSize extends SingleChildParent {
  protected Axis axis;

  protected IntrinsicSize(Axis axis, Widget child) {
    super(child);
    this.axis = axis;
  }

  public static IntrinsicSize create(Widget child) {
    return new IntrinsicSize(Axis.BOTH, child);
  }

  public static IntrinsicSize create(Axis axis, Widget child) {
    return new IntrinsicSize(axis, child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    switch (axis) {
      case VERTICAL -> intrinsicBounds.setSize(
        child.intrinsicBounds.width,
        child.intrinsicBounds.height
      );
      case HORIZONTAL -> intrinsicBounds.setSize(
        child.intrinsicBounds.width,
        child.computedBounds.height
      );
      case BOTH -> intrinsicBounds.setSize(
        child.computedBounds.width,
        child.intrinsicBounds.height
      );
    }

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
    if (widget instanceof IntrinsicSize intrinsicSizeWidget) {
      return Objects.equals(child, intrinsicSizeWidget.child) &&
        Objects.equals(axis, intrinsicSizeWidget.axis);
    }

    return false;
  }

  public IntrinsicSize withAxis(Axis axis) {
    this.axis = axis;
    return this;
  }
}
