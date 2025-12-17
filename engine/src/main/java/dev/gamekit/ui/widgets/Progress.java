package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

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

  @WidgetBuilderField
  protected BufferedImage trackBackground = TRACK_BG;
  @WidgetBuilderField
  protected BufferedImage fillBackground = FILL_BG;
  @WidgetBuilderField
  protected Spacing trackEdgeInsets = new Spacing(8);
  @WidgetBuilderField
  protected Spacing fillEdgeInsets = new Spacing(8);
  @WidgetBuilderField
  protected Spacing fillMargin = new Spacing(0);
  @WidgetBuilderField
  protected FillMode fillMode = FillMode.SCALE;
  @WidgetBuilderField
  protected Double minValue = 0.0;
  @WidgetBuilderField
  protected Double maxValue = 100.0;
  @WidgetBuilderField
  protected Double value = 50.0;

  protected double valueRatio = 0;

  private final Bounds fillAbsoluteBounds;

  public Progress(ProgressConfig... config) {
    super(config);

    fillAbsoluteBounds = new Bounds();
  }

  public static Progress create(ProgressConfig... config) {
    return new Progress(config);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Progress progressWidget &&
      Objects.equals(trackBackground, progressWidget.trackBackground) &&
      Objects.equals(fillBackground, progressWidget.fillBackground) &&
      Objects.equals(trackEdgeInsets, progressWidget.trackEdgeInsets) &&
      Objects.equals(fillEdgeInsets, progressWidget.fillEdgeInsets) &&
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

    this.trackBackground = coalesce(config.trackBackground, theme.progressTrackBackground, TRACK_BG);
    this.fillBackground = coalesce(config.fillBackground, theme.progressFillBackground, FILL_BG);
    this.trackEdgeInsets = coalesce(config.trackEdgeInsets, theme.progressTrackEdgeInsets, new Spacing(8));
    this.fillEdgeInsets = coalesce(config.fillEdgeInsets, theme.progressFillEdgeInsets, new Spacing(8));
    this.fillMargin = coalesce(config.fillMargin, theme.progressFillMargin, new Spacing(0));
    this.fillMode = coalesce(config.fillMode, theme.progressFillMode, FillMode.SCALE);
    this.minValue = config.minValue;
    this.maxValue = config.maxValue;
    this.value = config.value;

    valueRatio = (value - minValue) / (maxValue - minValue);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(
      constraints.maxWidth(),
      trackBackground != null
        ? trackBackground.getHeight()
        : 0
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
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
      renderWith9PatchScaling(trackBackground, absoluteBounds, trackEdgeInsets, g);

    if (fillBackground != null) {
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
        fillEdgeInsets, g
      );

      if (fillMode == FillMode.CLIP)
        g.setClip(originalClip);
    }
  }

  //  @SuppressWarnings("unchecked")
  //  public static class ProgressConfig<T extends ProgressConfig<T>> extends LeafConfig {
  //    protected BufferedImage trackBackground;
  //    protected BufferedImage fillBackground;
  //    protected Spacing trackEdgeInsets;
  //    protected Spacing fillEdgeInsets;
  //    protected Spacing fillMargin;
  //    protected FillMode fillMode;
  //    protected Double minValue;
  //    protected Double maxValue;
  //    protected Double value;
  //
  //    private T value(double value) {
  //      this.value = value;
  //      return (T) this;
  //    }
  //
  //    public T range(double minValue, double maxValue) {
  //      this.minValue = minValue;
  //      this.maxValue = maxValue;
  //      return (T) this;
  //    }
  //
  //    public T trackBackground(BufferedImage trackBackground) {
  //      this.trackBackground = trackBackground;
  //      return (T) this;
  //    }
  //
  //    public T trackEdgeInsets(int top, int right, int bottom, int left) {
  //      this.trackEdgeInsets = new Spacing(top, right, bottom, left);
  //      return (T) this;
  //    }
  //
  //    public T fillBackground(BufferedImage fillBackground) {
  //      this.fillBackground = fillBackground;
  //      return (T) this;
  //    }
  //
  //    public T fillEdgeInsets(int top, int right, int bottom, int left) {
  //      this.fillEdgeInsets = new Spacing(top, right, bottom, left);
  //      return (T) this;
  //    }
  //
  //    public T fillMargin(int top, int right, int bottom, int left) {
  //      this.fillMargin = new Spacing(top, right, bottom, left);
  //      return (T) this;
  //    }
  //
  //    public T fillMode(FillMode fillMode) {
  //      this.fillMode = fillMode;
  //      return (T) this;
  //    }
  //  }

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
