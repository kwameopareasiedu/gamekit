package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A {@link SingleChildParent} which uses the 9-patch algorithm to render a
 * {@link BufferedImage} as a background to its descendants
 */
public class Panel extends SingleChildParent implements NinePatch {
  protected final BufferedImage background;
  protected final Spacing padding;

  public Panel(PanelOptions options, Widget child) {
    super(child);
    this.background = options.background;
    this.padding = options.padding;
  }

  public static Panel create(PanelOptions options, Widget child) {
    return new Panel(options, child);
  }

  public static PanelOptions options() {
    return new PanelOptions();
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
      Math.max(background.getWidth(), child.computedBounds.width),
      Math.max(background.getHeight(), child.computedBounds.height)
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(
      padding.left - padding.right + computedBounds.width / 2 - child.computedBounds.width / 2,
      padding.top - padding.bottom + computedBounds.height / 2 - child.computedBounds.height / 2
    );
  }

  @Override
  public void renderAppearance(Graphics2D g) {
    super.renderAppearance(g);

    renderNinePatch(
      background,
      absoluteBounds,
      padding,
      g
    );
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Panel panelWidget) {
      return Objects.equals(background, panelWidget.background)
        && Objects.equals(padding, panelWidget.padding);
    }

    return false;
  }

  public static class PanelOptions {
    public BufferedImage background;
    public Spacing padding;

    public PanelOptions background(BufferedImage background) {
      this.background = background;
      return this;
    }

    public PanelOptions padding(Spacing padding) {
      this.padding = padding;
      return this;
    }
  }
}
