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

/** A {@link Leaf} input widget which adjusts a value moving a slider */
public class Slider extends Leaf implements NinePatch {
  public static final BufferedImage TRACK_BG =
    IO.getResourceImage("default-sprites.png", 470, 232, 96, 32);
  public static final BufferedImage FILL_BG =
    IO.getResourceImage("default-sprites.png", 470, 289, 96, 32);
  public static final BufferedImage THUMB_BG =
    IO.getResourceImage("default-sprites.png", 470, 346, 32, 32);

  protected BufferedImage trackBackground;
  protected BufferedImage fillBackground;
  protected BufferedImage thumbBackground;
  protected Spacing trackNinePatchSpacing;
  protected Spacing fillNinePatchSpacing;
  protected Spacing thumbNinePatchSpacing;
  protected FillMode fillMode;
  protected Integer thumbSize;
  protected Double minValue;
  protected Double maxValue;
  protected double value;

  private final Bounds fillAbsoluteBounds;
  private final Bounds thumbAbsoluteBounds;
  private double valueRatio = 0;

  public Slider(SliderConfig config, Double value) {
    super(config.value(value));
    fillAbsoluteBounds = new Bounds();
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
    return widget instanceof Slider sliderWidget &&
      Objects.equals(trackBackground, sliderWidget.trackBackground) &&
      Objects.equals(fillBackground, sliderWidget.fillBackground) &&
      Objects.equals(thumbBackground, sliderWidget.thumbBackground) &&
      Objects.equals(trackNinePatchSpacing, sliderWidget.trackNinePatchSpacing) &&
      Objects.equals(fillNinePatchSpacing, sliderWidget.fillNinePatchSpacing) &&
      Objects.equals(thumbNinePatchSpacing, sliderWidget.thumbNinePatchSpacing) &&
      Objects.equals(fillMode, sliderWidget.fillMode) &&
      Objects.equals(thumbSize, sliderWidget.thumbSize) &&
      Objects.equals(minValue, sliderWidget.minValue) &&
      Objects.equals(maxValue, sliderWidget.maxValue) &&
      Objects.equals(value, sliderWidget.value);
  }

  @Override
  protected void performInit() {
    SliderConfig config = (SliderConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    if (config.value == null)
      throw new IllegalArgumentException("Slider value cannot be null");
    if (config.minValue == null)
      throw new IllegalArgumentException("Slider minValue cannot be null");
    else if (config.maxValue == null)
      throw new IllegalArgumentException("Slider maxValue cannot be null");
    else if (config.minValue > config.maxValue)
      throw new IllegalArgumentException("Slider minValue cannot be more than maxValue");
    else if (config.value < config.minValue || config.value > config.maxValue)
      throw new IllegalArgumentException("Slider value must be between minValue and maxValue");

    this.trackBackground = coalesce(config.trackBackground, TRACK_BG);
    this.fillBackground = coalesce(config.fillBackground, FILL_BG);
    this.thumbBackground = coalesce(config.thumbBackground, THUMB_BG);
    this.trackNinePatchSpacing = coalesce(config.trackNinePatchSpacing, Spacing.create(8));
    this.fillNinePatchSpacing = coalesce(config.fillNinePatchSpacing, Spacing.create(8));
    this.thumbNinePatchSpacing = coalesce(config.thumbNinePatchSpacing, Spacing.create(8));
    this.fillMode = coalesce(config.fillMode, FillMode.SCALE);
    this.thumbSize = coalesce(config.thumbSize, 32);
    this.minValue = coalesce(config.minValue);
    this.maxValue = coalesce(config.maxValue);
    this.value = coalesce(config.value);

    valueRatio = (value - minValue) / (maxValue - minValue);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
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
    fillAbsoluteBounds.set(
      absoluteBounds.x, absoluteBounds.y,
      valueRatio * absoluteBounds.width, absoluteBounds.height
    );

    double relativeThumbX = valueRatio * (absoluteBounds.width - thumbSize);

    thumbAbsoluteBounds.set(
      absoluteBounds.x + relativeThumbX, absoluteBounds.y,
      thumbSize, thumbSize
    );

    super.performPostLayout();
  }

  @Override
  protected void performRender(Graphics2D g) {
    if (trackBackground != null)
      renderWith9PatchScaling(trackBackground, absoluteBounds, trackNinePatchSpacing, g);

    boolean fillShownUnderThumb = valueRatio * absoluteBounds.width > 0.5 * thumbSize;

    if (fillBackground != null && fillShownUnderThumb) {
      Shape originalClip = g.getClip();

      if (fillMode == FillMode.CLIP)
        g.setClip(
          (int) absoluteBounds.x,
          (int) absoluteBounds.y,
          (int) fillAbsoluteBounds.width,
          (int) fillAbsoluteBounds.height
        );

      renderWith9PatchScaling(
        fillBackground,
        switch (fillMode) {
          case SCALE -> fillAbsoluteBounds;
          case CLIP -> absoluteBounds;
        },
        fillNinePatchSpacing, g
      );

      if (fillMode == FillMode.CLIP)
        g.setClip(originalClip);
    }

    if (thumbBackground != null)
      renderWith9PatchScaling(thumbBackground, thumbAbsoluteBounds, thumbNinePatchSpacing, g);
  }

  public static class SliderConfig extends LeafConfig {
    protected BufferedImage trackBackground;
    protected BufferedImage fillBackground;
    protected BufferedImage thumbBackground;
    protected Spacing trackNinePatchSpacing;
    protected Spacing fillNinePatchSpacing;
    protected Spacing thumbNinePatchSpacing;
    protected FillMode fillMode;
    protected Integer thumbSize;
    protected Double minValue;
    protected Double maxValue;
    protected Double value;

    private SliderConfig value(double value) {
      this.value = value;
      return this;
    }

    public SliderConfig range(double minValue, double maxValue) {
      this.minValue = minValue;
      this.maxValue = maxValue;
      return this;
    }

    public SliderConfig fillMode(FillMode fillMode) {
      this.fillMode = fillMode;
      return this;
    }
  }

  /** Enumeration which determines how a {@link Slider} fill is rendered */
  public enum FillMode {
    /**
     * Mode to render the {@link Slider#fillBackground} without scaling but use a clip to control
     * the visible portion
     */
    CLIP,
    /**
     * Mode to render the {@link Slider#fillBackground}, scaling its entirety using 9-patch scaling
     */
    SCALE
  }
}
