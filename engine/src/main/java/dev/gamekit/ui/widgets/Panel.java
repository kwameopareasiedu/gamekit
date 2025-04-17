package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.Spacing;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A {@link SingleChildParent} which uses the 9-patch algorithm to render a
 * {@link BufferedImage} as a background to its descendants
 */
public class Panel extends SingleChildParent implements NinePatch {
  protected final BufferedImage background;
  protected final Spacing spacing;

  public Panel(BufferedImage background, Spacing spacing, Widget child) {
    super(child);

    if (background == null)
      throw new NullPointerException("Image cannot be null");

    this.background = background;
    this.spacing = spacing;
  }

  @SafeVarargs
  public static Panel create(Param<? super PanelParam>... params) {
    return new Panel(
      Param.getValue(params, "background", Constants.DEFAULT_PANEL_BG),
      Param.getValue(params, "spacing", new Spacing(12)),
      Param.getValue(params, "child", null)
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
      Math.max(background.getWidth(), child.computedBounds.width),
      Math.max(background.getHeight(), child.computedBounds.height)
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
  public void renderAppearance(Graphics2D g) {
    super.renderAppearance(g);

    renderNinePatch(background, absoluteBounds, spacing, g);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Panel panelWidget) {
      return Objects.equals(background, panelWidget.background)
        && Objects.equals(spacing, panelWidget.spacing);
    }

    return false;
  }
}
