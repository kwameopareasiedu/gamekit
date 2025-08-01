package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Position;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;
import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Progress} widget extension which adjusts a value by moving a slider */
public class Slider extends Progress implements NinePatch, MouseEvent.Handler {
  public static final BufferedImage THUMB_BG =
    IO.getResourceImage("default-sprites.png", 470, 346, 32, 32);

  protected BufferedImage thumbBackground;
  protected Spacing thumbEdgeInsets;
  protected Integer thumbWidth;
  protected Integer thumbHeight;
  protected ChangeEvent.Handler<Double> changeListener;

  private final Bounds thumbAbsoluteBounds;
  private final Position lastMousePosition;
  private boolean mouseDown = false;

  public Slider(SliderConfig config, Double value) {
    super(config, value);
    thumbAbsoluteBounds = new Bounds();
    lastMousePosition = new Position();
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
      Objects.equals(thumbWidth, sliderWidget.thumbWidth) &&
      Objects.equals(thumbHeight, sliderWidget.thumbHeight);
  }

  @Override
  protected void performInit() {
    super.performInit();

    SliderConfig config = (SliderConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.thumbBackground = coalesce(config.thumbBackground, theme.sliderThumbBackground, THUMB_BG);
    this.thumbEdgeInsets =
      coalesce(config.thumbEdgeInsets, theme.sliderThumbEdgeInsets, new Spacing(8));
    this.thumbWidth = coalesce(config.thumbWidth, theme.sliderThumbWidth, 32);
    this.thumbHeight = coalesce(config.thumbHeight, theme.sliderThumbHeight, 32);
    this.changeListener = coalesce(config.changeListener, null);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    //    super.performLayout(constraints);

    intrinsicBounds.setSize(
      constraints.maxWidth(),
      trackBackground != null
        ? Math.max(trackBackground.getHeight(), thumbHeight)
        : thumbHeight
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
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

  public static class SliderConfig extends ProgressConfig<SliderConfig> {
    protected BufferedImage thumbBackground;
    protected Spacing thumbEdgeInsets;
    protected Integer thumbWidth;
    protected Integer thumbHeight;
    protected ChangeEvent.Handler<Double> changeListener;

    public SliderConfig thumbBackground(BufferedImage thumbBackground) {
      this.thumbBackground = thumbBackground;
      return this;
    }

    public SliderConfig thumbEdgeInsets(int top, int right, int bottom, int left) {
      this.thumbEdgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public SliderConfig thumbSize(int thumbWidth, int thumbHeight) {
      this.thumbWidth = thumbWidth;
      this.thumbHeight = thumbHeight;
      return this;
    }

    public SliderConfig changeListener(ChangeEvent.Handler<Double> changeListener) {
      this.changeListener = changeListener;
      return this;
    }
  }
}
