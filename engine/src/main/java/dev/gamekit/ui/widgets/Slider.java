package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Progress} widget extension which adjusts a value by moving a slider */
@WidgetBuilder
public class Slider extends Progress implements NinePatch, MouseEvent.Handler {
  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 470, 346, 32, 32)")
  protected BufferedImage thumbBackground;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(8)")
  protected Spacing thumbEdgeInsets;
  @WidgetBuilderField(fallback = "32")
  protected Integer thumbWidth;
  @WidgetBuilderField(fallback = "32")
  protected Integer thumbHeight;
  @WidgetBuilderField(comparable = false, themable = false)
  protected ChangeEvent.Handler<Double> changeListener;

  private final Bounds thumbAbsoluteBounds;
  private final Position lastMousePosition;
  private boolean mouseDown = false;

  public Slider(SliderConfig... config) {
    super(config);
    thumbAbsoluteBounds = new Bounds();
    lastMousePosition = new Position();
  }

  public static Slider create(SliderConfig... config) {
    return new Slider(config);
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
      renderWith9PatchScaling(thumbBackground, thumbAbsoluteBounds, thumbEdgeInsets, g);
  }

  @Override
  public void handleEvent(MouseEvent ev) {
    switch (ev.type) {
      case MOTION -> {
        if (mouseDown && changeListener != null) {
          double pixelDelta = ev.x - lastMousePosition.x;
          double valueDelta = pixelDelta / absoluteBounds.width * (maxValue - minValue);
          double newValue = clamp(value + valueDelta, minValue, maxValue);
          changeListener.handleEvent(new ChangeEvent<>(newValue));
        }

        lastMousePosition.set(ev.x, ev.y);
      }
      case DOWN -> {
        if (changeListener != null) {
          double pixelDelta = ev.x - absoluteBounds.x;
          double instantValue = pixelDelta / absoluteBounds.width * (maxValue - minValue);
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
