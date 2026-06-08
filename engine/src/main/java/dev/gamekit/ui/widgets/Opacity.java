package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

import static dev.gamekit.utils.GMath.clamp;

/** A {@link SingleChildParent} which renders its child with transparency */
@WidgetBuilder
public class Opacity extends SingleChildParent {
  @WidgetBuilderField(fallback = "1.0")
  public Double opacity;

  private AlphaComposite composite;
  private Composite originalComposite;

  public Opacity(String key, OpacityConfig config, Widget child) {
    super(key, config, child);
  }

  public static Opacity create(String key, OpacityConfig.Updater updater, Widget child) {
    return new Opacity(key, Widgets.configureOpacity(updater), child);
  }

  public static Opacity create(OpacityConfig.Updater updater, Widget child) {
    return new Opacity(null, Widgets.configureOpacity(updater), child);
  }

  public static Opacity create(Double opacity, Widget child) {
    return Opacity.create(props -> props.opacity = opacity, child);
  }

  @Override
  protected void performInit() {
    if (opacity == null) throw new IllegalArgumentException("Opacity opacity cannot be null");

    if (this.composite == null || this.composite.getAlpha() != opacity)
      this.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp(opacity.floatValue(), 0, 1));

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
