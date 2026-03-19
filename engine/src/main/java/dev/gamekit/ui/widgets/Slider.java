package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.EngineImage;
import dev.gamekit.utils.Position;

import java.awt.*;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Progress} widget extension which adjusts a value by moving a slider */
@WidgetBuilder
public class Slider extends Progress implements MouseEvent.Handler {
  public static final EngineImage DEFAULT_THUMB_BG =
    IO.getImageSliceWithInsets("default-sprites.png", 470, 346, 32, 32, 8, 8, 8, 8);

  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Slider.DEFAULT_THUMB_BG")
  public EngineImage thumbBackground;
  @WidgetBuilderField(fallback = "32")
  public Integer thumbWidth;
  @WidgetBuilderField(fallback = "32")
  public Integer thumbHeight;
  @WidgetBuilderField(comparable = false, themable = false)
  public ChangeEvent.Handler<Double> changeListener;

  private final Bounds thumbAbsoluteBounds;
  private final Position lastMousePosition;
  private boolean mouseDown = false;

  public Slider(SliderConfig config) {
    super(config);
    thumbAbsoluteBounds = new Bounds();
    lastMousePosition = new Position();
  }

  public static Slider create(SliderConfig.Updater updater) {
    return new Slider(Widgets.configureSlider(updater));
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(
      constraints.maxWidth(),
      trackBackground != null
        ? Math.max(trackBackground.getHeight(), thumbHeight)
        : thumbHeight
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  @Override
  protected void performPostLayout() {
    super.performPostLayout();

    double thumbPositionX = absoluteBounds.x +
      valueRatio * absoluteBounds.width - 0.5 * thumbWidth;

    double thumbPositionY = absoluteBounds.y +
      absoluteBounds.height / 2.0 - thumbHeight / 2.0;

    thumbAbsoluteBounds.set(
      thumbPositionX, thumbPositionY,
      thumbWidth, thumbHeight
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    super.performRender(g);

    if (thumbBackground != null)
      thumbBackground.render(g, thumbAbsoluteBounds);
  }

  @Override
  public void handleEvent(MouseEvent ev) {
    switch (ev.type) {
      case MOTION -> {
        if (mouseDown && changeListener != null) {
          double pixelDelta = ev.x - lastMousePosition.x;
          double valueDelta = (pixelDelta / absoluteBounds.width) * (maxValue - minValue);
          double newValue = clamp(value + valueDelta, minValue, maxValue);
          changeListener.handleEvent(new ChangeEvent<>(newValue));
        }

        lastMousePosition.set(ev.x, ev.y);
      }
      case DOWN -> {
        if (changeListener != null) {
          double pixelDelta = ev.x - absoluteBounds.x;
          double instantValue = (pixelDelta / absoluteBounds.width) * (maxValue - minValue);
          double newValue = clamp(instantValue, minValue, maxValue);
          changeListener.handleEvent(new ChangeEvent<>(newValue));
        }

        lastMousePosition.set(ev.x, ev.y);
        mouseDown = true;
      }
      case RELEASE -> mouseDown = false;
    }
  }
}
