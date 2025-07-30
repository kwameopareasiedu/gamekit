package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;
import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which renders its child with transparency */
public class Opacity extends SingleChildParent {
  protected double opacity;

  private AlphaComposite composite;

  public Opacity(OpacityConfig config, Widget child) {
    super(config, child);
  }

  public static Opacity create(OpacityConfig config, Widget child) {
    return new Opacity(config, child);
  }

  public static OpacityConfig config() {
    return new OpacityConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Opacity opacityWidget)
      return Objects.equals(opacity, opacityWidget.opacity);

    return false;
  }

  @Override
  protected void performInit() {
    OpacityConfig config = (OpacityConfig) super.config;

    this.opacity = clamp(coalesce(config.opacity, 1.0), 0, 1);

    if (this.composite == null || this.composite.getAlpha() != opacity)
      this.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) this.opacity);

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
    Composite originalComposite = g.getComposite();
    g.setComposite(composite);
    super.performRender(g);
    g.setComposite(originalComposite);
  }

  public static class OpacityConfig extends SingleChildParentConfig {
    Double opacity;

    public OpacityConfig opacity(double opacity) {
      this.opacity = opacity;
      return this;
    }
  }
}
