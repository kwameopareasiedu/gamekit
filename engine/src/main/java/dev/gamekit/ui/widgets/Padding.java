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
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      child.computedBounds.width + padding.getHorizontal(),
      child.computedBounds.height + padding.getVertical()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    if (intrinsicBounds.width > computedBounds.width ||
      intrinsicBounds.height > computedBounds.height) {
      child.layout(
        new Constraints(
          0, computedBounds.width - padding.getHorizontal(),
          0, computedBounds.height - padding.getVertical()
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
