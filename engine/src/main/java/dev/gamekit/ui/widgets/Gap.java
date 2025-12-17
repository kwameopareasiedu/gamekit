package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;
import java.util.Objects;

/** A {@link Leaf} which leaves a gap space */
public class Gap extends Leaf {
  @WidgetBuilderField
  protected Integer width = 8;
  @WidgetBuilderField
  protected Integer height = 8;

  public Gap(GapConfig... config) {
    super(config);
  }

  public static Gap create(GapConfig... config) {
    return new Gap(config);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Gap imageWidget &&
      Objects.equals(width, imageWidget.width) &&
      Objects.equals(height, imageWidget.height);
  }

  @Override
  protected void performInit() {
    GapConfig config = (GapConfig) super.config;

    this.width = config.width;
    this.height = config.height;

    super.performInit();
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

//  public static class GapConfig extends LeafConfig {
//    protected Integer width;
//    protected Integer height;
//
//    private GapConfig width(Integer width) {
//      this.width = width;
//      return this;
//    }
//
//    private GapConfig height(Integer height) {
//      this.height = height;
//      return this;
//    }
//  }
}
