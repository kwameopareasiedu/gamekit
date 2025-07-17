package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.InputEventHandler;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.core.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Widget} which can be clicked to trigger an event */
public class Button extends SingleChildParent implements NinePatch, InputEventHandler {
  protected final Spacing ninePatchBorder;
  protected final BufferedImage defaultBackground;
  protected final BufferedImage hoverBackground;
  protected final BufferedImage pressedBackground;
  protected final MouseEvent.Listener mouseListener;
  protected boolean mouseEntered;
  protected boolean mousePressed;

  public Button(ButtonOptions options, Widget child) {
    super(child);
    this.ninePatchBorder = options.ninePatchBorder;
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
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(child.computedBounds.width, child.computedBounds.height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(
      computedBounds.width / 2 - intrinsicBounds.width / 2,
      computedBounds.height / 2 - intrinsicBounds.height / 2
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

    if (bgImage != null && ninePatchBorder != null) {
      super.renderAppearance(g);
      renderNinePatch(bgImage, absoluteBounds, ninePatchBorder, g);
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
    Spacing ninePatchBorder = new Spacing(24);
    BufferedImage defaultBackground = Constants.DEFAULT_BUTTON_BG;
    BufferedImage hoverBackground = Constants.HOVER_BUTTON_BG;
    BufferedImage pressedBackground = Constants.PRESSED_BUTTON_BG;
    MouseEvent.Listener mouseListener = e -> { };

    public ButtonOptions ninePatch(Spacing border) {
      this.ninePatchBorder = border;
      return this;
    }

    public ButtonOptions ninePatch(int all) {
      this.ninePatchBorder = new Spacing(all);
      return this;
    }

    public ButtonOptions ninePatch(int horizontal, int vertical) {
      this.ninePatchBorder = new Spacing(horizontal, vertical);
      return this;
    }

    public ButtonOptions ninePatch(int top, int right, int bottom, int left) {
      this.ninePatchBorder = new Spacing(top, right, bottom, left);
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
