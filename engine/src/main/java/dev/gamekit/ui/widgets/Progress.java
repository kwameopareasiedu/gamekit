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

/** A {@link Leaf} widget which displays a progress bar */
public class Progress extends Leaf implements NinePatch {
  public static final BufferedImage TRACK_BG =
    IO.getResourceImage("default-sprites.png", 470, 232, 96, 32);
  public static final BufferedImage FILL_BG =
    IO.getResourceImage("default-sprites.png", 470, 289, 96, 32);

  protected BufferedImage trackBackground;
  protected BufferedImage fillBackground;
  protected Spacing trackNinePatchSpacing;
  protected Spacing fillNinePatchSpacing;
  protected Spacing fillMargin;
  protected FillMode fillMode;
  protected Double minValue;
  protected Double maxValue;
  protected double value;

  private final Bounds fillAbsoluteBounds;
  private double valueRatio = 0;

  public Progress(ProgressConfig<?> config, Double value) {
    super(config.value(value));
    fillAbsoluteBounds = new Bounds();
  }

  public static Progress create(ProgressConfig<?> config, double value) {
    return new Progress(config, value);
  }

  public static ProgressConfig<?> config() {
    return new ProgressConfig<>();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Progress progressWidget &&
      Objects.equals(trackBackground, progressWidget.trackBackground) &&
      Objects.equals(fillBackground, progressWidget.fillBackground) &&
      Objects.equals(trackNinePatchSpacing, progressWidget.trackNinePatchSpacing) &&
      Objects.equals(fillNinePatchSpacing, progressWidget.fillNinePatchSpacing) &&
      Objects.equals(fillMargin, progressWidget.fillMargin) &&
      Objects.equals(fillMode, progressWidget.fillMode) &&
      Objects.equals(minValue, progressWidget.minValue) &&
      Objects.equals(maxValue, progressWidget.maxValue) &&
      Objects.equals(value, progressWidget.value);
  }

  @Override
  protected void performInit() {
    super.performInit();

    ProgressConfig<?> config = (ProgressConfig<?>) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    if (config.value == null)
      throw new IllegalArgumentException("Progress value cannot be null");
    if (config.minValue == null)
      throw new IllegalArgumentException("Progress minValue cannot be null");
    else if (config.maxValue == null)
      throw new IllegalArgumentException("Progress maxValue cannot be null");
    else if (config.minValue > config.maxValue)
      throw new IllegalArgumentException("Progress minValue cannot be more than maxValue");
    else if (config.value < config.minValue || config.value > config.maxValue)
      throw new IllegalArgumentException("Progress value must be between minValue and maxValue");

    this.trackBackground = coalesce(config.trackBackground, TRACK_BG);
    this.fillBackground = coalesce(config.fillBackground, FILL_BG);
    this.trackNinePatchSpacing = coalesce(config.trackNinePatchSpacing, Spacing.create(8));
    this.fillNinePatchSpacing = coalesce(config.fillNinePatchSpacing, Spacing.create(8));
    this.fillMargin = coalesce(config.fillMargin, Spacing.create(0));
    this.fillMode = coalesce(config.fillMode, FillMode.SCALE);
    this.minValue = coalesce(config.minValue);
    this.maxValue = coalesce(config.maxValue);
    this.value = coalesce(config.value);

    valueRatio = (value - minValue) / (maxValue - minValue);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicBounds.setSize(
      constraints.maxWidth(),
      trackBackground != null
        ? trackBackground.getHeight()
        : 0
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  @Override
  protected void performPostLayout() {
    super.performPostLayout();

    fillAbsoluteBounds.set(
      absoluteBounds.x + fillMargin.left,
      absoluteBounds.y + fillMargin.top,
      valueRatio * (absoluteBounds.width - fillMargin.getHorizontal()),
      absoluteBounds.height - fillMargin.getVertical()
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    if (trackBackground != null)
      renderWith9PatchScaling(trackBackground, absoluteBounds, trackNinePatchSpacing, g);

    if (fillBackground != null && isFillVisible()) {
      Shape originalClip = g.getClip();

      if (fillMode == FillMode.CLIP)
        g.setClip(
          (int) fillAbsoluteBounds.x,
          (int) fillAbsoluteBounds.y,
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
  }

  /** Returns a boolean indicating whether the fill background should be rendered */
  protected boolean isFillVisible() {
    return true;
  }

  @SuppressWarnings("unchecked")
  public static class ProgressConfig<T extends ProgressConfig<T>> extends LeafConfig {
    protected BufferedImage trackBackground;
    protected BufferedImage fillBackground;
    protected Spacing trackNinePatchSpacing;
    protected Spacing fillNinePatchSpacing;
    protected Spacing fillMargin;
    protected FillMode fillMode;
    protected Double minValue;
    protected Double maxValue;
    protected Double value;

    private T value(double value) {
      this.value = value;
      return (T) this;
    }

    public T range(double minValue, double maxValue) {
      this.minValue = minValue;
      this.maxValue = maxValue;
      return (T) this;
    }

    public T track(BufferedImage trackBackground, Spacing trackNinePatchSpacing) {
      this.trackBackground = trackBackground;
      this.trackNinePatchSpacing = trackNinePatchSpacing;
      return (T) this;
    }

    public T fillMargin(Spacing fillMargin) {
      this.fillMargin = fillMargin;
      return (T) this;
    }

    public T fill(BufferedImage fillBackground, Spacing fillNinePatchSpacing) {
      this.fillBackground = fillBackground;
      this.fillNinePatchSpacing = fillNinePatchSpacing;
      return (T) this;
    }

    public T fillMode(FillMode fillMode) {
      this.fillMode = fillMode;
      return (T) this;
    }
  }

  /** Enumeration which determines how a {@link Progress} fill is rendered */
  public enum FillMode {
    /**
     * Mode to render the {@link Progress#fillBackground} without scaling but use a clip to control
     * the visible portion
     */
    CLIP,
    /**
     * Mode to render the {@link Progress#fillBackground}, scaling its entirety using 9-patch scaling
     */
    SCALE
  }
}
