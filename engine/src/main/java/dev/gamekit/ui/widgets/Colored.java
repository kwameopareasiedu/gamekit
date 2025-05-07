package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

/** A {@link SingleChildParent} which renders a solid color background */
public class Colored extends SingleChildParent {
  protected final Color color;
  protected final int borderRadius;

  public Colored(ColoredOptions options, Widget child) {
    super(child);
    this.color = options.color;
    this.borderRadius = options.borderRadius;
  }

  public static Colored create(ColoredOptions options, Widget child) {
    return new Colored(options, child);
  }

  public static ColoredOptions options() {
    return new ColoredOptions();
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

    child.computedBounds.setPosition(0, 0);
  }

  @Override
  public void renderAppearance(Graphics2D g) {
    super.renderAppearance(g);

    g.setColor(color);
    g.fillRoundRect(
      (int) absoluteBounds.x, (int) absoluteBounds.y,
      (int) absoluteBounds.width, (int) absoluteBounds.height,
      borderRadius, borderRadius
    );
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Colored panelWidget) {
      return Objects.equals(color, panelWidget.color) &&
        Objects.equals(borderRadius, panelWidget.borderRadius);
    }

    return false;
  }

  public static class ColoredOptions {
    public Color color = Color.GRAY;
    public int borderRadius = 0;

    public ColoredOptions color(Color color) {
      this.color = color;
      return this;
    }

    public ColoredOptions borderRadius(int borderRadius) {
      this.borderRadius = borderRadius;
      return this;
    }
  }
}
