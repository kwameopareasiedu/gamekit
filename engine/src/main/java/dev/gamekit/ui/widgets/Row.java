package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

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
    Constraints cc = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    int currentX = 0;
    int maxHeight = 0;

    for (var child : children) {
      child.computeLayout(cc);
      child.computedBounds.setPosition(currentX, 0);

      currentX += child.computedBounds.width;
      maxHeight = Math.max(maxHeight, child.computedBounds.height);
      cc = new Constraints(
        0, cc.maxWidth() - child.computedBounds.width,
        0, cc.maxHeight()
      );
    }

    intrinsicBounds.setSize(currentX, maxHeight);
    computedBounds.setSize(currentX, maxHeight);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    return true;
  }
}
