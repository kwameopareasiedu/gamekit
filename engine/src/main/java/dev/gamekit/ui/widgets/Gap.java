package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/** A {@link Leaf} which leaves a gap space */
@WidgetBuilder
public class Gap extends Leaf {
  @WidgetBuilderField(fallback = "8")
  protected Integer width;
  @WidgetBuilderField(fallback = "8")
  protected Integer height;

  public Gap(GapConfig... config) {
    super(config);
  }

  public static Gap create(GapConfig... config) {
    return new Gap(config);
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
