package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Leaf} which renders a solid color background */
public class Colored extends Leaf {
  protected Color color;
  protected int borderRadius;

  public Colored(ColoredConfig config) {
    super(config);
  }

  public static Colored create(ColoredConfig config) {
    return new Colored(config);
  }

  public static ColoredConfig config() {
    return new ColoredConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Colored panelWidget)
      return Objects.equals(color, panelWidget.color) &&
        Objects.equals(borderRadius, panelWidget.borderRadius);

    return false;
  }

  @Override
  protected void performInit() {
    ColoredConfig config = (ColoredConfig) super.config;

    this.color = coalesce(config.color, Color.GRAY);
    this.borderRadius = coalesce(config.borderRadius, 0);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicBounds.setSize(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
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

  public static class ColoredConfig extends LeafConfig {
    protected Color color;
    protected Integer borderRadius;

    public ColoredConfig color(Color color) {
      this.color = color;
      return this;
    }

    public ColoredConfig borderRadius(int borderRadius) {
      this.borderRadius = borderRadius;
      return this;
    }
  }
}
