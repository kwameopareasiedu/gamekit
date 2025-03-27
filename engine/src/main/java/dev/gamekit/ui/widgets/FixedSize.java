package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.util.Objects;

/** A {@link SingleChildParent} which enforces a fixed size on its child */
public class FixedSize extends SingleChildParent {
  protected final int width;
  protected final int height;

  public FixedSize(int width, int height, Widget child) {
    super(child);
    this.width = width;
    this.height = height;
  }

  public static FixedSize create(int width, int height, Widget child) {
    return new FixedSize(width, height, child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    int computedWidth = constraints.constrainWidth(width);
    int computedHeight = constraints.constrainHeight(height);

    child.computeLayout(
      new Constraints(
        computedWidth, computedWidth, computedHeight, computedHeight
      )
    );

    computedBounds.setSize(computedWidth, computedHeight);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof FixedSize fixedSizeWidget) {
      return Objects.equals(width, fixedSizeWidget.width) &&
        Objects.equals(height, fixedSizeWidget.height);
    }
    return false;
  }
}
