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

/** A {@link Leaf} widget which displays a progress bar */
@WidgetBuilder
public class Progress extends Leaf implements NinePatch {
  public static final BufferedImage TRACK_BG = IO.getResourceImage("default-sprites.png", 470, 232, 96, 32);
  public static final BufferedImage FILL_BG = IO.getResourceImage("default-sprites.png", 470, 289, 96, 32);

  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 470, 232, 96, 32)")
  protected BufferedImage trackBackground;
  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 470, 289, 96, 32)")
  protected BufferedImage fillBackground;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(8)")
  protected Spacing trackEdgeInsets;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(8)")
  protected Spacing fillEdgeInsets;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(0)")
  protected Spacing fillMargin;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Progress.FillMode.SCALE")
  protected FillMode fillMode;
  @WidgetBuilderField(fallback = "0.0")
  protected Double minValue;
  @WidgetBuilderField(fallback = "100.0")
  protected Double maxValue;
  @WidgetBuilderField(fallback = "50.0")
  protected Double value;

  protected double valueRatio = 0;

  private final Bounds fillAbsoluteBounds;

  public Progress(ProgressConfig config) {
    super(config);

    fillAbsoluteBounds = new Bounds();
  }

  public static Progress create(ProgressConfig.Updater updater) {
    return new Progress(Widgets.configureProgress(updater));
  }

  @Override
  protected void performInit() {
    super.performInit();

    if (value == null) throw new IllegalArgumentException("Progress value cannot be null");
    if (minValue == null) throw new IllegalArgumentException("Progress minValue cannot be null");
    else if (maxValue == null) throw new IllegalArgumentException("Progress maxValue cannot be null");
    else if (minValue > maxValue) throw new IllegalArgumentException("Progress minValue cannot be more than maxValue");
    else if (value < minValue || value > maxValue)
      throw new IllegalArgumentException("Progress value must be between minValue and maxValue");

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
