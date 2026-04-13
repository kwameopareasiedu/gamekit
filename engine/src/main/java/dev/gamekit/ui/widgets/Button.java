package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Picture;

import java.awt.*;

/** A {@link Widget} which can be clicked to trigger an action */
@WidgetBuilder
public class Button extends SingleChildParent implements MouseEvent.Handler {
  public static final Picture DEFAULT_BG =
    IO.getImageSliceWithInsets("default-sprites.png", 64, 64, 350, 120, 24, 24, 24, 24);
  public static final Picture DEFAULT_HOVER_BG =
    IO.getImageSliceWithInsets("default-sprites.png", 64, 232, 350, 120, 24, 24, 24, 24);
  public static final Picture DEFAULT_PRESSED_BG =
    IO.getImageSliceWithInsets("default-sprites.png", 64, 400, 350, 120, 24, 24, 24, 24);

  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Button.DEFAULT_BG")
  public Picture defaultBackground;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Button.DEFAULT_HOVER_BG")
  public Picture hoverBackground;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Button.DEFAULT_PRESSED_BG")
  public Picture pressedBackground;
  @WidgetBuilderField(comparable = false, themable = false)
  public MouseEvent.Handler mouseListener;

  protected boolean mouseEntered;
  protected boolean mousePressed;

  public Button(ButtonConfig config, Widget child) {
    super(config, child);
  }

  public static Button create(ButtonConfig.Updater updater, Widget child) {
    return new Button(Widgets.configureButton(updater), child);
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
  protected void renderSelf(Graphics2D g) {
    Picture bgImage = defaultBackground;

    if (mousePressed)
      bgImage = pressedBackground;
    else if (mouseEntered)
      bgImage = hoverBackground;

    if (bgImage != null)
      bgImage.render(g, absoluteBounds);
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
