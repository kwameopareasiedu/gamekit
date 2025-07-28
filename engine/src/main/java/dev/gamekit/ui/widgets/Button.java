package dev.gamekit.ui.widgets;

import dev.gamekit.core.Constants;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Widget} which can be clicked to trigger an event */
public class Button extends SingleChildParent implements NinePatch, MouseEvent.Handler {
  protected Spacing ninePatchBorder;
  protected BufferedImage defaultBackground;
  protected BufferedImage hoverBackground;
  protected BufferedImage pressedBackground;
  protected MouseEvent.Handler mouseListener;
  protected boolean mouseEntered;
  protected boolean mousePressed;

  private Config config;

  public Button(Config config, Widget child) {
    super(child);
    this.config = config;
  }

  public static Button create(Config config, Widget child) {
    return new Button(config, child);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Button buttonWidget) {
      return Objects.equals(ninePatchBorder, buttonWidget.ninePatchBorder) &&
        Objects.equals(defaultBackground, buttonWidget.defaultBackground) &&
        Objects.equals(hoverBackground, buttonWidget.hoverBackground) &&
        Objects.equals(pressedBackground, buttonWidget.pressedBackground);
    }

    return false;
  }

  @Override
  protected void performUpdateState(Widget widget) {
    this.config = ((Button) widget).config;
    this.ninePatchBorder = ((Button) widget).ninePatchBorder;
    this.defaultBackground = ((Button) widget).defaultBackground;
    this.hoverBackground = ((Button) widget).hoverBackground;
    this.pressedBackground = ((Button) widget).pressedBackground;
    this.mouseListener = ((Button) widget).mouseListener;
  }

  @Override
  protected void performMounted() {
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.ninePatchBorder =
      coalesce(config.ninePatchBorder, theme.buttonNinePatchBorder, new Spacing(24));
    this.defaultBackground =
      coalesce(config.defaultBackground, theme.buttonDefaultBackground, Constants.DEFAULT_BUTTON_BG);
    this.hoverBackground =
      coalesce(config.hoverBackground, theme.buttonHoverBackground, Constants.HOVER_BUTTON_BG);
    this.pressedBackground =
      coalesce(config.pressedBackground, theme.buttonPressedBackground, Constants.PRESSED_BUTTON_BG);
    this.mouseListener = coalesce(config.mouseListener, null);

    super.performMounted();
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

    if (mousePressed)
      bgImage = pressedBackground;
    else if (mouseEntered)
      bgImage = hoverBackground;

    if (bgImage != null && ninePatchBorder != null) {
      super.renderAppearance(g);
      renderNinePatch(bgImage, absoluteBounds, ninePatchBorder, g);
    }
  }

  @Override
  public void handleEvent(MouseEvent event) {
    switch (event.type) {
      case ENTER -> mouseEntered = true;
      case DOWN -> mousePressed = true;
      case RELEASE -> mousePressed = false;
      case EXIT -> {
        mouseEntered = false;
        mousePressed = false;
      }
    }

    uiBridge.triggerRender();

    if (mouseListener != null)
      mouseListener.handleEvent(event);
  }

  public static class Config {
    Spacing ninePatchBorder;
    BufferedImage defaultBackground;
    BufferedImage hoverBackground;
    BufferedImage pressedBackground;
    MouseEvent.Handler mouseListener;

    Config() { }

    public Config ninePatch(Spacing border) {
      this.ninePatchBorder = border;
      return this;
    }

    public Config ninePatch(int all) {
      this.ninePatchBorder = new Spacing(all);
      return this;
    }

    public Config ninePatch(int horizontal, int vertical) {
      this.ninePatchBorder = new Spacing(horizontal, vertical);
      return this;
    }

    public Config ninePatch(int top, int right, int bottom, int left) {
      this.ninePatchBorder = new Spacing(top, right, bottom, left);
      return this;
    }

    public Config defaultBackground(BufferedImage defaultBackground) {
      this.defaultBackground = defaultBackground;
      return this;
    }

    public Config hoverBackground(BufferedImage hoverBackground) {
      this.hoverBackground = hoverBackground;
      return this;
    }

    public Config pressedBackground(BufferedImage pressedBackground) {
      this.pressedBackground = pressedBackground;
      return this;
    }

    public Config mouseListener(MouseEvent.Handler mouseListener) {
      this.mouseListener = mouseListener;
      return this;
    }
  }
}
