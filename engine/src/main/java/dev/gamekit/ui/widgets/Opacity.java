package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;

import java.awt.*;
import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;

/** A {@link SingleChildParent} which renders its child with transparency */
public class Opacity extends SingleChildParent {
  protected final double opacity;

  private final AlphaComposite composite;

  public Opacity(double opacity, Widget child) {
    super(child);
    this.opacity = clamp(opacity, 0, 1);
    this.composite = AlphaComposite.getInstance(
      AlphaComposite.SRC_OVER, (float) this.opacity
    );
  }

  @SafeVarargs
  public static Opacity create(Param<? super OpacityParam>... params) {
    return new Opacity(
      Param.getValue(params, "opacity", 1.0),
      Param.getValue(params, "child", null)
    );
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
}
