package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

/** A {@link MultiChildParent} which stacks its children on top of each other */
public class Stack extends MultiChildParent {
  public Stack(Widget... children) {
    super(children);
  }

  public static Stack create(Widget... children) {
    return new Stack(children);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    double maxWidth = 0, maxHeight = 0;

    for (Widget child : children) {
      child.layout(
        new Constraints(
          0, constraints.maxWidth(),
          0, constraints.maxHeight()
        )
      );

      maxWidth = Math.max(maxWidth, child.computedBounds.width);
      maxHeight = Math.max(maxHeight, child.computedBounds.height);
    }

    intrinsicBounds.setSize(maxWidth, maxHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    for (Widget child : children) {
      child.computedBounds.setPosition(
        computedBounds.width / 2 - child.computedBounds.width / 2,
        computedBounds.height / 2 - child.computedBounds.height / 2
      );
    }
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Stack;
  }
}
