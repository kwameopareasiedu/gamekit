package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;

/** A {@link Widget} which can be clicked to trigger an action */
@WidgetBuilder
public class Button extends SingleChildParent implements NinePatch, MouseEvent.Handler {
  public static final BufferedImage DEFAULT_BG = IO.getResourceImage("default-sprites.png", 64, 64, 350, 120);
  public static final BufferedImage HOVER_BG = IO.getResourceImage("default-sprites.png", 64, 232, 350, 120);
  public static final BufferedImage PRESSED_BG = IO.getResourceImage("default-sprites.png", 64, 400, 350, 120);

  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 64, 64, 350, 120)")
  protected BufferedImage defaultBackground;
  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 64, 232, 350, 120)")
  protected BufferedImage hoverBackground;
  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 64, 400, 350, 120)")
  protected BufferedImage pressedBackground;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(24)")
  protected Spacing edgeInsets;
  @WidgetBuilderField(comparable = false, themable = false)
  protected MouseEvent.Handler mouseListener;

  protected boolean mouseEntered;
  protected boolean mousePressed;

  public Button(ButtonConfig... config) {
    super(config);
  }

  public static Button create(ButtonConfig... config) {
    return new Button(config);
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

    if (bgImage != null && edgeInsets != null) {
      renderWith9PatchScaling(bgImage, absoluteBounds, edgeInsets, g);
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

    host.triggerRender();

    if (mouseListener != null)
      mouseListener.handleEvent(event);
  }
}
