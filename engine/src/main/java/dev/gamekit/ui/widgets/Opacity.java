package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/** A {@link SingleChildParent} which renders its child with transparency */
@WidgetBuilder
public class Opacity extends SingleChildParent {
  @WidgetBuilderField(fallback = "1.0")
  protected Double opacity;

  private AlphaComposite composite;
  private Composite originalComposite;

  public Opacity(OpacityConfig... config) {
    super(config);
  }

  public static Opacity create(OpacityConfig... config) {
    return new Opacity(config);
  }

  @Override
  protected void performInit() {
    if (opacity == null) throw new IllegalArgumentException("Opacity opacity cannot be null");

    if (this.composite == null || this.composite.getAlpha() != opacity)
      this.composite = AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER, opacity.floatValue()
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
  protected void preRender(Graphics2D g) {
    originalComposite = g.getComposite();
    g.setComposite(composite);
  }

  @Override
  protected void postRender(Graphics2D g) {
    g.setComposite(originalComposite);
  }
}
