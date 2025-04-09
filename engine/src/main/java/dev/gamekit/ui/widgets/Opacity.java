package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;

/** A {@link SingleChildParent} which renders its child with transparency */
public class Opacity extends SingleChildParent {
  protected double opacity;

  private final AlphaComposite composite;

  protected Opacity(double opacity, Widget child) {
    super(child);
    this.opacity = clamp(opacity, 0, 1);
    composite =
      AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) this.opacity);
  }

  public static Opacity create(double opacity, Widget child) {
    return new Opacity(opacity, child);
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
  public void performRender(Graphics2D g) {
    g.setComposite(composite);
    super.performRender(g);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Opacity opacityWidget) {
      return Objects.equals(opacity, opacityWidget.opacity);
    }
    return false;
  }
}
