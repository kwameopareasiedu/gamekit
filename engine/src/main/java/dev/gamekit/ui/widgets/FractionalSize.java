package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which enforces a fractional size of itself on its child */
@WidgetBuilder
public class FractionalSize extends SingleChildParent {
  @WidgetBuilderField
  protected Double widthRatio = 1.0;
  @WidgetBuilderField
  protected Double heightRatio = 1.0;

  public FractionalSize(FractionalSizeConfig... config) {
    super(config);
  }

  public static FractionalSize create(FractionalSizeConfig... config) {
    return new FractionalSize(config);
  }

  @Override
  protected void performInit() {
    FractionalSizeConfig config = (FractionalSizeConfig) super.config;

    this.widthRatio = coalesce(config.widthRatio, 1.0);
    this.heightRatio = coalesce(config.heightRatio, 1.0);

    super.performInit();
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
