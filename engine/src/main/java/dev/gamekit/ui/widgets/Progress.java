package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Leaf} widget which displays a progress bar */
@WidgetBuilder
public class Progress extends Leaf implements NinePatch {
  public static final BufferedImage TRACK_BG = IO.getResourceImage("default-sprites.png", 470, 232, 96, 32);
  public static final BufferedImage FILL_BG = IO.getResourceImage("default-sprites.png", 470, 289, 96, 32);

  @WidgetBuilderField
  protected BufferedImage trackBackground;
  @WidgetBuilderField
  protected BufferedImage fillBackground;
  @WidgetBuilderField
  protected Spacing trackEdgeInsets;
  @WidgetBuilderField
  protected Spacing fillEdgeInsets;
  @WidgetBuilderField
  protected Spacing fillMargin;
  @WidgetBuilderField
  protected FillMode fillMode;
  @WidgetBuilderField
  protected Double minValue;
  @WidgetBuilderField
  protected Double maxValue;
  @WidgetBuilderField
  protected Double value;

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
  protected void performInit() {
    super.performInit();

    ProgressConfig config = (ProgressConfig) super.config;
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
    this.minValue = coalesce(config.minValue, 0.0);
    this.maxValue = coalesce(config.maxValue, 100.0);
    this.value = coalesce(config.value, 50.0);

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

  /** Enumeration which determines how a {@link Progress#fillBackground} fill is rendered */
  public enum FillMode {
    /** Mode to render without scaling but use a clip to control the visible portion */
    CLIP,
    /** Mode to render, scaling its entirety using 9-patch scaling */
    SCALE
  }
}
