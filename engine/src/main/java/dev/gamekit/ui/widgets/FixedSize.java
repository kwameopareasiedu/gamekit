package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which enforces a fixed size on its child */
@WidgetBuilder
public class FixedSize extends SingleChildParent {
  @WidgetBuilderField
  protected Double width;
  @WidgetBuilderField
  protected Double height;

  public FixedSize(FixedSizeConfig... config) {
    super(config);
  }

  public static FixedSize create(FixedSizeConfig... config) {
    return new FixedSize(config);
  }

  @Override
  protected void performInit() {
    FixedSizeConfig config = (FixedSizeConfig) super.config;

    this.width = coalesce(config.width, 64.0);
    this.height = coalesce(config.height, 64.0);

    super.performInit();
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
