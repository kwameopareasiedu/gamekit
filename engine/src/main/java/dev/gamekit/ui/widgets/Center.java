package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;

/** An {@link SingleChildParent} which centers its single child within itself */
public class Center extends SingleChildParent {
  public Center(Widget child) {
    super(child);
  }

  @SafeVarargs
  public static Center create(Param<? super CenterParam>... params) {
    return new Center(
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
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    int hOffset = computedBounds.width / 2 - intrinsicBounds.width / 2;
    int vOffset = computedBounds.height / 2 - intrinsicBounds.height / 2;
    child.computedBounds.setPosition(hOffset, vOffset);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    return widget instanceof Align;
  }
}
