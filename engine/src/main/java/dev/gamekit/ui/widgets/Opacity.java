package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;
import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which renders its child with transparency */
public class Opacity extends SingleChildParent {
  protected double opacity;

  private final Config config;
  private AlphaComposite composite;

  public Opacity(Config config, Widget child) {
    super(child);
    this.config = config;
  }

  public static Opacity create(Config config, Widget child) {
    return new Opacity(config, child);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  protected void performMounted() {
    this.opacity = clamp(coalesce(config.opacity, 1.0), 0, 1);
    this.composite = AlphaComposite.getInstance(
      AlphaComposite.SRC_OVER, (float) this.opacity
    );
    super.performMounted();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    Composite c = g.getComposite();
    g.setComposite(composite);
    super.performRender(g);
    g.setComposite(c);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Opacity opacityWidget) {
      return Objects.equals(opacity, opacityWidget.opacity);
    }

    return false;
  }

  public static class Config {
    Double opacity;

    Config() { }

    public Config opacity(double opacity) {
      this.opacity = opacity;
      return this;
    }
  }
}
