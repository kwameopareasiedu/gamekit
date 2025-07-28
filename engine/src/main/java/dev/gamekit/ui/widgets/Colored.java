package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

/** A {@link Leaf} which renders a solid color background */
public class Colored extends Leaf {
  protected Color color;
  protected int borderRadius;

  private final Config config;

  public Colored(Config config) {
    this.config = config;
  }

  public static Colored create(Config config) {
    return new Colored(config);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Colored panelWidget) {
      return Objects.equals(color, panelWidget.color) &&
        Objects.equals(borderRadius, panelWidget.borderRadius);
    }

    return false;
  }

  @Override
  protected void performUpdateState(Widget widget) {
    this.color = ((Colored) widget).color;
    this.borderRadius = ((Colored) widget).borderRadius;
  }

  @Override
  protected void performMounted() {
    super.performMounted();
    this.color = config.color;
    this.borderRadius = config.borderRadius;
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

  public static class Config {
    Color color = Color.GRAY;
    int borderRadius = 0;

    Config() { }

    public Config color(Color color) {
      this.color = color;
      return this;
    }

    public Config borderRadius(int borderRadius) {
      this.borderRadius = borderRadius;
      return this;
    }
  }
}
