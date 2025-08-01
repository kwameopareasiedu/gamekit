package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Progress} widget extension which adjusts a value by moving a slider */
public class Slider extends Progress implements NinePatch {
  public static final BufferedImage THUMB_BG =
    IO.getResourceImage("default-sprites.png", 470, 346, 32, 32);

  protected BufferedImage thumbBackground;
  protected Spacing thumbEdgeInsets;
  protected Integer thumbSize;

  private final Bounds thumbAbsoluteBounds;
  private double valueRatio = 0;

  public Slider(SliderConfig config, Double value) {
    super(config, value);
    thumbAbsoluteBounds = new Bounds();
  }

  public static Slider create(SliderConfig config, double value) {
    return new Slider(config, value);
  }

  public static SliderConfig config() {
    return new SliderConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Slider sliderWidget && super.stateEquals(widget) &&
      Objects.equals(thumbBackground, sliderWidget.thumbBackground) &&
      Objects.equals(thumbEdgeInsets, sliderWidget.thumbEdgeInsets) &&
      Objects.equals(thumbSize, sliderWidget.thumbSize);
  }

  @Override
  protected void performInit() {
    super.performInit();

    SliderConfig config = (SliderConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.thumbBackground = coalesce(config.thumbBackground, THUMB_BG);
    this.thumbEdgeInsets = coalesce(config.thumbEdgeInsets, Spacing.create(8));
    this.thumbSize = coalesce(config.thumbSize, 32);

    valueRatio = (value - minValue) / (maxValue - minValue);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    //    super.performLayout(constraints);

    intrinsicBounds.setSize(
      constraints.maxWidth(),
      trackBackground != null
        ? Math.max(trackBackground.getHeight(), thumbSize)
        : thumbSize
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  @Override
  protected void performPostLayout() {
    double relativeThumbX = valueRatio * (absoluteBounds.width - thumbSize);

    thumbAbsoluteBounds.set(
      absoluteBounds.x + relativeThumbX, absoluteBounds.y,
      thumbSize, thumbSize
    );

    super.performPostLayout();
  }

  @Override
  protected void performRender(Graphics2D g) {
    super.performRender(g);

    if (thumbBackground != null)
      renderWith9PatchScaling(thumbBackground, thumbAbsoluteBounds, thumbEdgeInsets, g);
  }

  @Override
  protected boolean isFillVisible() {
    return valueRatio * absoluteBounds.width > 0.5 * thumbSize;
  }

  public static class SliderConfig extends ProgressConfig<SliderConfig> {
    protected BufferedImage thumbBackground;
    protected Spacing thumbEdgeInsets;
    protected Integer thumbSize;

    public SliderConfig thumbBackground(BufferedImage thumbBackground) {
      this.thumbBackground = thumbBackground;
      return this;
    }

    public SliderConfig thumbEdgeInsets(Spacing thumbEdgeInsets) {
      this.thumbEdgeInsets = thumbEdgeInsets;
      return this;
    }

    public SliderConfig thumbSize(int thumbSize) {
      this.thumbSize = thumbSize;
      return this;
    }
  }
}
