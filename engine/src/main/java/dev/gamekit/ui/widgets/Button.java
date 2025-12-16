package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Widget} which can be clicked to trigger an action */
public class Button extends SingleChildParent implements NinePatch, MouseEvent.Handler {
  public static final BufferedImage DEFAULT_BG =
    IO.getResourceImage("default-sprites.png", 64, 64, 350, 120);
  public static final BufferedImage HOVER_BG =
    IO.getResourceImage("default-sprites.png", 64, 232, 350, 120);
  public static final BufferedImage PRESSED_BG =
    IO.getResourceImage("default-sprites.png", 64, 400, 350, 120);

  protected BufferedImage defaultBackground;
  protected BufferedImage hoverBackground;
  protected BufferedImage pressedBackground;
  protected Spacing edgeInsets;
  protected MouseEvent.Handler mouseListener;
  protected boolean mouseEntered;
  protected boolean mousePressed;

  public Button(ButtonConfig config, Widget child) {
    super(config, child);
  }

  public static Button create(ButtonConfig config, Widget child) {
    return new Button(config, child);
  }

  public static ButtonConfig config() {
    return new ButtonConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Button buttonWidget &&
      Objects.equals(defaultBackground, buttonWidget.defaultBackground) &&
      Objects.equals(hoverBackground, buttonWidget.hoverBackground) &&
      Objects.equals(pressedBackground, buttonWidget.pressedBackground) &&
      Objects.equals(edgeInsets, buttonWidget.edgeInsets);
  }

  @Override
  protected void performInit() {
    ButtonConfig config = (ButtonConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.edgeInsets =
      coalesce(config.edgeInsets, theme.buttonEdgeInsets, new Spacing(24));
    this.defaultBackground =
      coalesce(config.defaultBackground, theme.buttonDefaultBackground, DEFAULT_BG);
    this.hoverBackground =
      coalesce(config.hoverBackground, theme.buttonHoverBackground, HOVER_BG);
    this.pressedBackground =
      coalesce(config.pressedBackground, theme.buttonPressedBackground, PRESSED_BG);
    this.mouseListener = coalesce(config.mouseListener, null);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicSize.set(child.computedBounds.width, child.computedBounds.height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    child.computedBounds.setPosition(
      computedBounds.width / 2 - intrinsicSize.width / 2,
      computedBounds.height / 2 - intrinsicSize.height / 2
    );
  }

  @Override
  protected void renderAppearance(Graphics2D g) {
    BufferedImage bgImage = defaultBackground;

    if (mousePressed)
      bgImage = pressedBackground;
    else if (mouseEntered)
      bgImage = hoverBackground;

    if (bgImage != null && edgeInsets != null)
      renderWith9PatchScaling(bgImage, absoluteBounds, edgeInsets, g);
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

    host.triggerRender();

    if (mouseListener != null)
      mouseListener.handleEvent(event);
  }

  public static class ButtonConfig extends SingleChildParentConfig {
    protected BufferedImage defaultBackground;
    protected BufferedImage hoverBackground;
    protected BufferedImage pressedBackground;
    protected Spacing edgeInsets;
    protected MouseEvent.Handler mouseListener;

    public ButtonConfig defaultBackground(BufferedImage defaultBackground) {
      this.defaultBackground = defaultBackground;
      return this;
    }

    public ButtonConfig hoverBackground(BufferedImage hoverBackground) {
      this.hoverBackground = hoverBackground;
      return this;
    }

    public ButtonConfig pressedBackground(BufferedImage pressedBackground) {
      this.pressedBackground = pressedBackground;
      return this;
    }

    public ButtonConfig edgeInsets(int top, int right, int bottom, int left) {
      this.edgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public ButtonConfig mouseListener(MouseEvent.Handler mouseListener) {
      this.mouseListener = mouseListener;
      return this;
    }
  }
}
