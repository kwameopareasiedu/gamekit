package dev.gamekit.ui.widgets;

import dev.gamekit.core.UI;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.InputEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.Param;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Widget} which can be clicked to trigger an event */
public class Button extends SingleChildParent implements Widget.InputHandler {
  protected final Spacing spacing;
  protected final BufferedImage defaultBackground;
  protected final BufferedImage hoverBackground;
  protected final BufferedImage pressedBackground;
  protected boolean mousePressed;

  public Button(
    Widget child,
    Spacing spacing,
    BufferedImage defaultBackground,
    BufferedImage hoverBackground,
    BufferedImage pressedBackground
  ) {
    super(child);
    this.spacing = spacing;
    this.defaultBackground = defaultBackground;
    this.hoverBackground = hoverBackground;
    this.pressedBackground = pressedBackground;
  }

  @SafeVarargs
  public static Button create(Param<? super ButtonParam>... params) {
    return new Button(
      Param.getValue(params, "child", Empty.create()),
      Param.getValue(params, "spacing", null),
      Param.getValue(params, "defaultBackground", Constants.DEFAULT_BUTTON_BG),
      Param.getValue(params, "hoverBackground", Constants.HOVER_BUTTON_BG),
      Param.getValue(params, "pressedBackground", Constants.PRESSED_BUTTON_BG)
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
  protected void renderBackground(Graphics2D g) {
    super.renderBackground(g);

    BufferedImage bgImage = defaultBackground;

    if (mousePressed)
      bgImage = pressedBackground;
    else if (mouseEntered)
      bgImage = hoverBackground;

    if (bgImage != null && spacing != null) {
      super.renderBackground(g);

      int dx2 = computedBounds.width, dy2 = computedBounds.height;

      g.setBackground(Constants.TRANSPARENT_COLOR);
      g.clearRect(0, 0, computedBounds.width, computedBounds.height);

      int nl = spacing.left;
      int nt = spacing.top;
      int iw = bgImage.getWidth();
      int ih = bgImage.getHeight();
      int nr = iw - spacing.right;
      int nb = ih - spacing.bottom;

      int[][] srcBounds = new int[][]{
        new int[]{ 0, 0, nl, nt },
        new int[]{ nl, 0, nr, nt },
        new int[]{ nr, 0, iw, nt },
        new int[]{ 0, nt, nl, nb },
        new int[]{ nl, nt, nr, nb },
        new int[]{ nr, nt, iw, nb },
        new int[]{ 0, nb, nl, ih },
        new int[]{ nl, nb, nr, ih },
        new int[]{ nr, nb, iw, ih },
      };

      nl = spacing.left;
      nt = spacing.top;
      nr = dx2 - spacing.right;
      nb = dy2 - spacing.bottom;

      int[][] destBounds = new int[][]{
        new int[]{ 0, 0, nl, nt },
        new int[]{ nl, 0, nr, nt },
        new int[]{ nr, 0, dx2, nt },
        new int[]{ 0, nt, nl, nb },
        new int[]{ nl, nt, nr, nb },
        new int[]{ nr, nt, dx2, nb },
        new int[]{ 0, nb, nl, dy2 },
        new int[]{ nl, nb, nr, dy2 },
        new int[]{ nr, nb, dx2, dy2 },
      };

      for (int i = 0; i < srcBounds.length; i++) {
        int[] src = srcBounds[i];
        int[] dest = destBounds[i];

        g.drawImage(
          bgImage, dest[0], dest[1], dest[2], dest[3],
          src[0], src[1], src[2], src[3], null
        );
      }
    }
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Button buttonWidget) {
      return Objects.equals(defaultBackground, buttonWidget.defaultBackground) &&
        Objects.equals(hoverBackground, buttonWidget.hoverBackground) &&
        Objects.equals(pressedBackground, buttonWidget.pressedBackground);
    }

    return false;
  }

  @Override
  public void handleEvent(InputEvent event) {
    super.handleEvent(event);

    if (event instanceof MouseEvent mouseEvent) {
      if (mouseEvent.type == MouseEvent.Type.PRESS) {
        mousePressed = true;
        UI.getInstance().triggerRender();
      } else if (mouseEvent.type == MouseEvent.Type.EXIT ||
        mouseEvent.type == MouseEvent.Type.RELEASE) {
        mousePressed = false;
        UI.getInstance().triggerRender();
      }
    }
  }
}
