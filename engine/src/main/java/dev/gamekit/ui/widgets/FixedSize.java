package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

/** A {@link SingleChildParent} which enforces a fixed size on its child */
@WidgetBuilder
public class FixedSize extends SingleChildParent {
  @WidgetBuilderField(fallback = "64.0")
  protected Double width;
  @WidgetBuilderField(fallback = "64.0")
  protected Double height;

  public FixedSize(FixedSizeConfig... config) {
    super(config);
  }

  public static FixedSize create(FixedSizeConfig... config) {
    return new FixedSize(config);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(new Constraints(width, width, height, height));

    intrinsicSize.set(width, height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }
}
