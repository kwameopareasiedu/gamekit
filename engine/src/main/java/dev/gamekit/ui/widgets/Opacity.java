package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

import static dev.gamekit.utils.Math.clamp;
import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which renders its child with transparency */
@WidgetBuilder
public class Opacity extends SingleChildParent {
  @WidgetBuilderField
  protected Double opacity;

  private AlphaComposite composite;

  public Opacity(OpacityConfig... config) {
    super(config);
  }

  public static Opacity create(OpacityConfig... config) {
    return new Opacity(config);
  }

  @Override
  protected void performInit() {
    OpacityConfig config = (OpacityConfig) super.config;

    this.opacity = clamp(coalesce(config.opacity, 1.0), 0, 1);

    if (this.composite == null || this.composite.getAlpha() != opacity)
      this.composite = AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER, this.opacity.floatValue()
      );

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

    intrinsicSize.set(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
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
}
