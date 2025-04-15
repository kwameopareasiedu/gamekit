package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;

import java.util.Objects;

/** A {@link SingleChildParent} which enforces a fixed size on its child */
public class Sized extends SingleChildParent {
  protected final int width;
  protected final int height;

  public Sized(int width, int height, Widget child) {
    super(child);
    this.width = width;
    this.height = height;
  }

  @SafeVarargs
  public static Sized create(Param<? super SizedParam>... params) {
    return new Sized(
      Param.getValue(params, "width", 64),
      Param.getValue(params, "height", 64),
      Param.getValue(params, "child", null)
    );
  }

  @Override
  protected void performLayout(Constraints constraints) {
    computedBounds.setSize(
      constraints.constrainWidth(width),
      constraints.constrainHeight(height)
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
    );
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Sized sizedWidget) {
      return Objects.equals(width, sizedWidget.width) &&
        Objects.equals(height, sizedWidget.height);
    }
    return false;
  }
}
