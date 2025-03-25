package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Size;

import java.util.Objects;

/** A {@link Parent} which arranges its children vertically */
public class Column extends MultiChildParent {
  protected Column(Widget... children) {
    super(children);
  }

  public static Column create(Widget... children) {
    return new Column(children);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints cc = new Constraints(0, constraints.maxWidth(), 0, constraints.maxHeight());
    int currentY = 0;
    int maxWidth = 0;

    for (var child : children) {
      child.computeLayout(cc);
      child.getComputedPosition().set(0, currentY);

      Size childSize = child.getComputedSize();

      currentY += childSize.height;
      maxWidth = Math.max(maxWidth, childSize.width);
      cc = new Constraints(
        0, cc.maxWidth(),
        0, cc.maxHeight() - childSize.height
      );
    }

    intrinsicSize.set(maxWidth, currentY);
    computedSize.set(maxWidth, currentY);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Column columnWidget) {
      return Objects.equals(children, columnWidget.children);
    }

    return false;
  }
}
