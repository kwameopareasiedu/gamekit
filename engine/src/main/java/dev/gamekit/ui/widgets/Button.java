package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.InputEventHandler;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Widget} which can be clicked to trigger an event */
public class Button extends SingleChildParent implements NinePatch,
  InputEventHandler {
  protected final Spacing spacing;
  protected final BufferedImage defaultBackground;
  protected final BufferedImage hoverBackground;
  protected final BufferedImage pressedBackground;
  protected final MouseEvent.Listener mouseListener;
  protected boolean mouseEntered;
  protected boolean mousePressed;

  public Button(
    Spacing spacing,
    BufferedImage defaultBackground,
    BufferedImage hoverBackground,
    BufferedImage pressedBackground,
    MouseEvent.Listener mouseListener,
    Widget child
  ) {
    super(child);
    this.spacing = spacing;
    this.defaultBackground = defaultBackground;
    this.hoverBackground = hoverBackground;
    this.pressedBackground = pressedBackground;
    this.mouseListener = mouseListener;
  }

  @SafeVarargs
  public static Button create(Param<? super ButtonParam>... params) {
    return new Button(
      Param.getValue(params, "spacing", new Spacing(24)),
      Param.getValue(params, "defaultBackground", Constants.DEFAULT_BUTTON_BG),
      Param.getValue(params, "hoverBackground", Constants.HOVER_BUTTON_BG),
      Param.getValue(params, "pressedBackground", Constants.PRESSED_BUTTON_BG),
      Param.getValue(params, "mouseListener", e -> { }),
      Param.getValue(params, "child", Empty.create())
    );
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(
      computedBounds.width / 2 - child.computedBounds.width / 2,
      computedBounds.height / 2 - child.computedBounds.height / 2
    );
  }

  @Override
  protected void renderAppearance(Graphics2D g) {
    super.renderAppearance(g);

    BufferedImage bgImage = defaultBackground;

    if (mousePressed) {
      bgImage = pressedBackground;
    } else if (mouseEntered) {
      bgImage = hoverBackground;
    }

    if (bgImage != null && spacing != null) {
      super.renderAppearance(g);
      renderNinePatch(bgImage, absoluteBounds, spacing, g);
    }
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Button buttonWidget) {
      return Objects.equals(defaultBackground, buttonWidget.defaultBackground) &&
        Objects.equals(hoverBackground, buttonWidget.hoverBackground) &&
        Objects.equals(pressedBackground, buttonWidget.pressedBackground);
    }

    return false;
  }

  @Override
  public MouseEvent.Listener getMouseListener() {
    return mouseListener;
  }

  @Override
  public void setMouseEntered(boolean mouseEntered) {
    this.mouseEntered = mouseEntered;
  }

  @Override
  public void setMousePressed(boolean mousePressed) {
    this.mousePressed = mousePressed;
  }
}
