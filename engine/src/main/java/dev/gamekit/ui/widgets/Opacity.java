package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;

/** A {@link SingleChildParent} which renders its child with transparency */
public class Opacity extends SingleChildParent {
  protected final double opacity;

  private final AlphaComposite composite;

  public Opacity(OpacityOptions options, Widget child) {
    super(child);
    this.opacity = clamp(options.opacity, 0, 1);
    this.composite = AlphaComposite.getInstance(
      AlphaComposite.SRC_OVER, (float) this.opacity
    );
  }

  public static Opacity create(OpacityOptions options, Widget child) {
    return new Opacity(options, child);
  }

  public static OpacityOptions options() {
    return new OpacityOptions();
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

  public static class OpacityOptions {
    double opacity = 1.0;

    public OpacityOptions opacity(double opacity) {
      this.opacity = opacity;
      return this;
    }
  }
}
