package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

/** A {@link Leaf} which leaves a gap space */
public class Gap extends Leaf {
  protected Integer width;
  protected Integer height;

  public Gap(GapConfig config) {
    super(config);
  }

  public static Gap create(Integer size, Integer height) {
    return new Gap(new GapConfig().width(size).height(height));
  }

  public static GapConfig config() {
    return new GapConfig();
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
    intrinsicBounds.setSize(width, height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  @Override
  protected void performRender(Graphics2D g) { /* No-op */ }

  public static class GapConfig extends LeafConfig {
    protected Integer width;
    protected Integer height;

    private GapConfig width(Integer width) {
      this.width = width;
      return this;
    }

    private GapConfig height(Integer height) {
      this.height = height;
      return this;
    }
  }
}
