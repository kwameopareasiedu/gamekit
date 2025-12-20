package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

/** A {@link SingleChildParent} which enforces a fractional size of itself on its child */
@WidgetBuilder
public class FractionalSize extends SingleChildParent {
  @WidgetBuilderField(fallback = "1.0")
  protected Double widthRatio;
  @WidgetBuilderField(fallback = "1.0")
  protected Double heightRatio;

  public FractionalSize(FractionalSizeConfig... config) {
    super(config);
  }

  public static FractionalSize create(FractionalSizeConfig... config) {
    return new FractionalSize(config);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    double width = widthRatio * constraints.maxWidth();
    double height = heightRatio * constraints.maxHeight();

    child.layout(new Constraints(width, width, height, height));

    intrinsicSize.set(width, height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }
}
