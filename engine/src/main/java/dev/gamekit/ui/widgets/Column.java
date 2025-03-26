package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

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
    Constraints cc = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    int currentY = 0;
    int maxWidth = 0;

    for (var child : children) {
      child.computeLayout(cc);
      child.computedBounds.setPosition(0, currentY);

      currentY += child.computedBounds.height;
      maxWidth = Math.max(maxWidth, child.computedBounds.width);
      cc = new Constraints(
        0, cc.maxWidth(),
        0, cc.maxHeight() - child.computedBounds.height
      );
    }

    intrinsicBounds.setSize(maxWidth, currentY);
    computedBounds.setSize(maxWidth, currentY);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    return true;
  }
}
