package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/** A {@link Leaf} which leaves a gap space */
@WidgetBuilder
public class Gap extends Leaf {
  @WidgetBuilderField(fallback = "8")
  public Integer width;
  @WidgetBuilderField(fallback = "8")
  public Integer height;

  public Gap(String key, GapConfig config) {
    super(key, config);
  }

  public static Gap create(String key, GapConfig.Updater updater) {
    return new Gap(key, Widgets.configureGap(updater));
  }

  public static Gap create(GapConfig.Updater updater) {
    return new Gap(null, Widgets.configureGap(updater));
  }

  public static Gap create(String key, int width, int height) {
    return Gap.create(
      key,
      props -> {
        props.width = width;
        props.height = height;
      }
    );
  }

  public static Gap create(int width, int height) {
    return Gap.create(
      props -> {
        props.width = width;
        props.height = height;
      }
    );
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(width, height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  @Override
  protected void performRender(Graphics2D g) { /* No-op */ }
}
