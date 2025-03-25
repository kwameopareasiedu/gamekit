package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Size;
import dev.gamekit.ui.Spacing;

import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;

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

    Size childSize = child.getComputedSize();

    int intrinsicWidth = childSize.width + padding.getHorizontal();
    int intrinsicHeight = childSize.height + padding.getVertical();
    intrinsicSize.set(intrinsicWidth, intrinsicHeight);

    int computedWidth = clamp(intrinsicWidth, constraints.minWidth(), constraints.maxWidth());
    int computedHeight = clamp(intrinsicHeight, constraints.minHeight(), constraints.maxHeight());
    computedSize.set(computedWidth, computedHeight);

    if (intrinsicWidth > computedWidth || intrinsicHeight > computedHeight) {
      child.computeLayout(
        new Constraints(
          0, computedWidth - padding.getHorizontal(),
          0, computedHeight - padding.getVertical()
        )
      );
    }

    child.getComputedPosition().set(padding.left, padding.top);
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
