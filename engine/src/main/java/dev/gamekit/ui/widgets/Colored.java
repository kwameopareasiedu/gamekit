package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

/** A {@link Leaf} which renders a solid color background */
public class Colored extends Leaf {
  protected final Color color;
  protected final int borderRadius;

  public Colored(ColoredOptions options) {
    this.color = options.color;
    this.borderRadius = options.borderRadius;
  }

  public static Colored create(ColoredOptions options) {
    return new Colored(options);
  }

  public static Colored create(Color color, int borderRadius) {
    return new Colored(new ColoredOptions().color(color).borderRadius(borderRadius));
  }

  public static ColoredOptions options() {
    return new ColoredOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicBounds.setSize(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  @Override
  public void performRender(Graphics2D g) {
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
