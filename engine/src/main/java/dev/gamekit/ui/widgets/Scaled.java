package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/** A {@link SingleChildParent} which scales the computed size of its child */
@WidgetBuilder
public class Scaled extends SingleChildParent {
  @WidgetBuilderField(fallback = "1.0")
  public Double scale;

  private Double invScale;

  public Scaled(ScaledConfig config, Widget child) {
    super(config, child);
  }

  public static Scaled create(ScaledConfig.Updater updater, Widget child) {
    return new Scaled(Widgets.configureScaled(updater), child);
  }

  public static Scaled create(Double scale, Widget child) {
    return Scaled.create(props -> props.scale = scale, child);
  }

  @Override
  protected void performInit() {
    invScale = 1.0 / scale;

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

    intrinsicSize.set(child.computedBounds.width, child.computedBounds.height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    child.computedBounds.setPosition(
      0.5 * (computedBounds.width - child.computedBounds.width),
      0.5 * (computedBounds.height - child.computedBounds.height)
    );
  }

  @Override
  protected void preRender(Graphics2D g) {
    if (scale != 1) {
      double px = absoluteBounds.x + 0.5 * absoluteBounds.width;
      double py = absoluteBounds.y + 0.5 * absoluteBounds.height;

      g.translate(px, py);
      g.scale(scale, scale);
      g.translate(-px, -py);
    }
  }

  @Override
  protected void postRender(Graphics2D g) {
    if (scale != 1) {
      double px = absoluteBounds.x + 0.5 * absoluteBounds.width;
      double py = absoluteBounds.y + 0.5 * absoluteBounds.height;

      g.translate(px, py);
      g.scale(invScale, invScale);
      g.translate(-px, -py);
    }
  }
}
