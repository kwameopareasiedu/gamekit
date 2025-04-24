package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.InputEventHandler;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Widget} which can be clicked to trigger an event */
public class Button extends SingleChildParent implements NinePatch, InputEventHandler {
  protected final Spacing padding;
  protected final BufferedImage defaultBackground;
  protected final BufferedImage hoverBackground;
  protected final BufferedImage pressedBackground;
  protected final MouseEvent.Listener mouseListener;
  protected boolean mouseEntered;
  protected boolean mousePressed;

  public Button(ButtonOptions options, Widget child) {
    super(child);
    this.padding = options.padding;
    this.defaultBackground = options.defaultBackground;
    this.hoverBackground = options.hoverBackground;
    this.pressedBackground = options.pressedBackground;
    this.mouseListener = options.mouseListener;
  }

  public static Button create(ButtonOptions options, Widget child) {
    return new Button(options, child);
  }

  public static ButtonOptions options() {
    return new ButtonOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth() - padding.getHorizontal(),
        0, constraints.maxHeight() - padding.getVertical()
      )
    );

    intrinsicBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width + padding.getHorizontal()),
      constraints.constrainHeight(intrinsicBounds.height + padding.getVertical())
    );

    child.computedBounds.setPosition(
      padding.left - padding.right + computedBounds.width / 2 - child.computedBounds.width / 2,
      padding.top - padding.bottom + computedBounds.height / 2 - child.computedBounds.height / 2
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

    if (bgImage != null && padding != null) {
      super.renderAppearance(g);
      renderNinePatch(bgImage, absoluteBounds, padding, g);
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

  public static class ButtonOptions {
    Spacing padding = new Spacing(24);
    BufferedImage defaultBackground = Constants.DEFAULT_BUTTON_BG;
    BufferedImage hoverBackground = Constants.HOVER_BUTTON_BG;
    BufferedImage pressedBackground = Constants.PRESSED_BUTTON_BG;
    MouseEvent.Listener mouseListener = e -> { };

    public ButtonOptions padding(Spacing padding) {
      this.padding = padding;
      return this;
    }

    public ButtonOptions defaultBackground(BufferedImage defaultBackground) {
      this.defaultBackground = defaultBackground;
      return this;
    }

    public ButtonOptions hoverBackground(BufferedImage hoverBackground) {
      this.hoverBackground = hoverBackground;
      return this;
    }

    public ButtonOptions pressedBackground(BufferedImage pressedBackground) {
      this.pressedBackground = pressedBackground;
      return this;
    }

    public ButtonOptions mouseListener(MouseEvent.Listener mouseListener) {
      this.mouseListener = mouseListener;
      return this;
    }
  }
}
