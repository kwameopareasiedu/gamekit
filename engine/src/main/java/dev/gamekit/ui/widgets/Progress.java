package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.EngineImage;
import dev.gamekit.utils.Spacing;

import java.awt.*;

/** A {@link Leaf} widget which displays a progress bar */
@WidgetBuilder
public class Progress extends Leaf {
  public static final EngineImage DEFAULT_TRACK_BG =
    IO.getImageSliceWithInsets("default-sprites.png", 470, 232, 96, 32, 8, 8, 8, 8);
  public static final EngineImage DEFAULT_FILL_BG =
    IO.getImageSliceWithInsets("default-sprites.png", 470, 289, 96, 32, 8, 8, 8, 8);

  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Progress.DEFAULT_TRACK_BG")
  public EngineImage trackBackground;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Progress.DEFAULT_FILL_BG")
  public EngineImage fillBackground;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(0)")
  public Spacing fillMargin;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Progress.FillMode.SCALE")
  public FillMode fillMode;
  @WidgetBuilderField(fallback = "0.0")
  public Double minValue;
  @WidgetBuilderField(fallback = "100.0")
  public Double maxValue;
  @WidgetBuilderField(fallback = "50.0")
  public Double value;

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
      trackBackground.render(g, absoluteBounds);

    if (fillBackground != null) {
      Shape originalClip = g.getClip();

      if (fillMode == FillMode.CLIP) {
        g.setClip(
          (int) fillAbsoluteBounds.x,
          (int) fillAbsoluteBounds.y,
          (int) fillAbsoluteBounds.width,
          (int) fillAbsoluteBounds.height
        );
      }

      fillBackground.render(
        g,
        switch (fillMode) {
          case SCALE -> fillAbsoluteBounds;
          case CLIP -> absoluteBounds;
        }
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
