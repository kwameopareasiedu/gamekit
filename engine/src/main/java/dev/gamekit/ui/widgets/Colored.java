package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Leaf} which renders a solid color background */
@WidgetBuilder
public class Colored extends Leaf {
  @WidgetBuilderField
  protected Color color;
  @WidgetBuilderField
  protected Integer borderRadius;

  public Colored(ColoredConfig... config) {
    super(config);
  }

  public static Colored create(ColoredConfig... config) {
    return new Colored(config);
  }

  @Override
  protected void performInit() {
    super.performInit();

    ColoredConfig config = (ColoredConfig) super.config;

    this.color = coalesce(config.color, Color.GRAY);
    this.borderRadius = coalesce(config.borderRadius, 0);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  @Override
  public void performRender(Graphics2D g) {
    g.setColor(color);
    g.fillRoundRect(
      (int) absoluteBounds.x, (int) absoluteBounds.y,
      (int) absoluteBounds.width, (int) absoluteBounds.height,
      borderRadius, borderRadius
    );
  }
}
