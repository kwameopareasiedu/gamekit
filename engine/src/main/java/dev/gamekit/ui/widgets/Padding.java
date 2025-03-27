package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;

import java.util.Objects;

/** A {@link Parent} which adds padding around its single child */
public class Padding extends SingleChildParent {
  protected Spacing padding;

  protected Padding(Widget child, Spacing padding) {
    super(child);
    this.padding = padding;
  }

  public static Padding create(Widget child, Spacing padding) {
    return new Padding(child, padding);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.computeLayout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    int intrinsicWidth = child.computedBounds.width + padding.getHorizontal();
    int intrinsicHeight = child.computedBounds.height + padding.getVertical();
    intrinsicBounds.setSize(intrinsicWidth, intrinsicHeight);

    int computedWidth = constraints.constrainWidth(intrinsicWidth);
    int computedHeight = constraints.constrainHeight(intrinsicHeight);
    computedBounds.setSize(computedWidth, computedHeight);

    if (intrinsicWidth > computedWidth || intrinsicHeight > computedHeight) {
      child.computeLayout(
        new Constraints(
          0, computedWidth - padding.getHorizontal(),
          0, computedHeight - padding.getVertical()
        )
      );
    }

    child.computedBounds.setPosition(padding.left, padding.top);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Padding paddingWidget) {
      return Objects.equals(child, paddingWidget.child)
        && Objects.equals(padding, paddingWidget.padding);
    }

    return false;
  }
}
