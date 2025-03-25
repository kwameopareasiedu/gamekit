package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Size;

import java.util.Objects;

/** A {@link Parent} which arranges its children horizontally */
public class Row extends MultiChildParent {
  protected Row(Widget... children) {
    super(children);
  }

  public static Row create(Widget... children) {
    return new Row(children);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints cc = new Constraints(0, constraints.maxWidth(), 0, constraints.maxHeight());
    int currentX = 0;
    int maxHeight = 0;

    for (var child : children) {
      child.computeLayout(cc);
      child.getComputedPosition().set(currentX, 0);

      Size childSize = child.getComputedSize();

      currentX += childSize.width;
      maxHeight = Math.max(maxHeight, childSize.height);
      cc = new Constraints(
        0, cc.maxWidth() - childSize.width,
        0, cc.maxHeight()
      );
    }

    intrinsicSize.set(currentX, maxHeight);
    computedSize.set(currentX, maxHeight);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Row rowWidget) {
      return Objects.equals(children, rowWidget.children);
    }

    return false;
  }
}
