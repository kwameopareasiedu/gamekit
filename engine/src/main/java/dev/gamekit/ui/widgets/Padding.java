package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.Spacing;

import java.util.Objects;

/** A {@link SingleChildParent} which adds padding around its single child */
public class Padding extends SingleChildParent {
  protected final Spacing padding;

  public Padding(Spacing padding, Widget child) {
    super(child);
    this.padding = padding;
  }

  @SafeVarargs
  public static Padding create(Param<? super PaddingParam>... params) {
    return new Padding(
      Param.getValue(params, "padding", new Spacing()),
      Param.getValue(params, "child", null)
    );
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

    child.computedBounds.setPosition(
      computedBounds.width / 2 - child.computedBounds.width / 2,
      computedBounds.height / 2 - child.computedBounds.height / 2
    );
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
