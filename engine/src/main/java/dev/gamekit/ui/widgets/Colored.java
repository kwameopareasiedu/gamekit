package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;
import java.util.Objects;

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
  public boolean stateEquals(Widget widget) {
    return widget instanceof Colored coloredWidget &&
      Objects.equals(color, coloredWidget.color) &&
      Objects.equals(borderRadius, coloredWidget.borderRadius);
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

//  public static class ColoredConfig extends LeafConfig {
//    protected Color color;
//    protected Integer borderRadius;
//
//    public ColoredConfig color(Color color) {
//      this.color = color;
//      return this;
//    }
//
//    public ColoredConfig borderRadius(int borderRadius) {
//      this.borderRadius = borderRadius;
//      return this;
//    }
//  }
}
