package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/** A {@link Leaf} which leaves a gap space */
@WidgetBuilder
public class Gap extends Leaf {
  @WidgetBuilderField
  protected Integer width = 8;
  @WidgetBuilderField
  protected Integer height = 8;

  public Gap(GapConfig... config) {
    super(config);
  }

  public static Gap create(GapConfig... config) {
    return new Gap(config);
  }

  @Override
  protected void performInit() {
    GapConfig config = (GapConfig) super.config;

    this.width = config.width;
    this.height = config.height;

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(width, height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  @Override
  protected void performRender(Graphics2D g) { /* No-op */ }
}
