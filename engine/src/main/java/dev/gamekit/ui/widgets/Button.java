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

/** A {@link Widget} which can be clicked to trigger an action */
public class Button extends SingleChildParent implements NinePatch, MouseEvent.Handler {
  protected Spacing ninePatchSpacing;
  protected BufferedImage defaultBackground;
  protected BufferedImage hoverBackground;
  protected BufferedImage pressedBackground;
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
    if (widget instanceof Button buttonWidget)
      return Objects.equals(ninePatchSpacing, buttonWidget.ninePatchSpacing) &&
        Objects.equals(defaultBackground, buttonWidget.defaultBackground) &&
        Objects.equals(hoverBackground, buttonWidget.hoverBackground) &&
        Objects.equals(pressedBackground, buttonWidget.pressedBackground);

    return false;
  }

  @Override
  protected void performInit() {
    ButtonConfig config = (ButtonConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.ninePatchSpacing =
      coalesce(config.ninePatchSpacing, theme.buttonNinePatchSpacing, Spacing.create(24));
    this.defaultBackground =
      coalesce(config.defaultBackground, theme.buttonDefaultBackground, Constants.DEFAULT_BUTTON_BG);
    this.hoverBackground =
      coalesce(config.hoverBackground, theme.buttonHoverBackground, Constants.HOVER_BUTTON_BG);
    this.pressedBackground =
      coalesce(config.pressedBackground, theme.buttonPressedBackground, Constants.PRESSED_BUTTON_BG);
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
    BufferedImage bgImage = defaultBackground;

    if (mousePressed)
      bgImage = pressedBackground;
    else if (mouseEntered)
      bgImage = hoverBackground;

    if (bgImage != null && ninePatchSpacing != null)
      renderWith9PatchScaling(bgImage, absoluteBounds, ninePatchSpacing, g);
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

  public static class ButtonConfig extends SingleChildParentConfig {
    private Spacing ninePatchSpacing;
    private BufferedImage defaultBackground;
    private BufferedImage hoverBackground;
    private BufferedImage pressedBackground;
    private MouseEvent.Handler mouseListener;

    public ButtonConfig ninePatchSpacing(Spacing ninePatchSpacing) {
      this.ninePatchSpacing = ninePatchSpacing;
      return this;
    }

    public ButtonConfig background(
      BufferedImage defaultBackground,
      BufferedImage hoverBackground,
      BufferedImage pressedBackground
    ) {
      this.defaultBackground = defaultBackground;
      this.hoverBackground = hoverBackground;
      this.pressedBackground = pressedBackground;
      return this;
    }

    public ButtonConfig mouseListener(MouseEvent.Handler mouseListener) {
      this.mouseListener = mouseListener;
      return this;
    }
  }
}
