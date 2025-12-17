package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.utils.Constraints;

/** A {@link SingleChildParent} which resizes to the intrinsic size of its child */
@WidgetBuilder
public class IntrinsicSize extends SingleChildParent {
  public IntrinsicSize(IntrinsicSizeConfig... config) {
    super(config);
  }

  public static IntrinsicSize create(IntrinsicSizeConfig... config) {
    return new IntrinsicSize(config);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    double width = child.computedBounds.width;
    double height = child.computedBounds.height;

    child.layout(new Constraints(width, width, height, height));

    intrinsicSize.set(width, height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }
}
