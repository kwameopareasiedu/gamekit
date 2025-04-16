package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;

import java.util.Objects;

/** A {@link SingleChildParent} which scales the computed size of its child */
public class Scaled extends SingleChildParent {
  protected final double scale;

  public Scaled(double scale, Widget child) {
    super(child);
    this.scale = Math.max(0, scale);
  }

  @SafeVarargs
  public static Scaled create(Param<? super ScaledParam>... params) {
    return new Scaled(
      Param.getValue(params, "scale", 1.0),
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

    int scaledWidth = (int) (child.computedBounds.width * scale);
    int scaledHeight = (int) (child.computedBounds.width * scale);

    intrinsicBounds.setSize(scaledWidth, scaledHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
    );
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Scaled scaledWidget) {
      return Objects.equals(scale, scaledWidget.scale);
    }
    return false;
  }
}
