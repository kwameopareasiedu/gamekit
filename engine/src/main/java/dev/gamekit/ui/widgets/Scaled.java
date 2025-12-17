package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which scales the computed size of its child */
@WidgetBuilder
public class Scaled extends SingleChildParent {
  @WidgetBuilderField
  protected Double scale;

  public Scaled(ScaledConfig... config) {
    super(config);
  }

  public static Scaled create(ScaledConfig... config) {
    return new Scaled(config);
  }

  @Override
  protected void performInit() {
    ScaledConfig config = (ScaledConfig) super.config;

    scale = Math.max(0, coalesce(config.scale, 1.0));

    super.performInit();
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

    intrinsicSize.set(scaledWidth, scaledHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
    );
  }
}
