package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.awt.*;
import java.util.Objects;

/**
 * A {@link SingleChildParent} which paints provides a shape, background
 * color and border decorations
 */
public class Decorated extends SingleChildParent {
  protected Color bgColor;
  protected Color borderColor;
  protected int borderWidth;
  protected int borderRadius;

  private Stroke borderStroke;

  protected Decorated(Widget child) {
    super(child);

    bgColor = new Color(0xAA822AE0, true);
    borderColor = new Color(0xAA3E006B, true);
    borderWidth = 1;
    borderRadius = 0;
    borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND,
      BasicStroke.JOIN_ROUND);
  }

  public static Decorated create(Widget child) {
    return new Decorated(child);
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

    if (intrinsicBounds.width > computedBounds.width ||
      intrinsicBounds.height > computedBounds.height) {
      child.layout(
        new Constraints(
          0, computedBounds.width,
          0, computedBounds.height
        )
      );
    }

    child.computedBounds.setPosition(0, 0);
  }

  @Override
  public void performRender(Graphics2D g) {
    super.performRender(g);

    if (borderWidth > 0) {
      Stroke defaultStroke = g.getStroke();
      g.setStroke(borderStroke);
      g.setColor(borderColor);
      g.drawRoundRect(0, 0, computedBounds.width, computedBounds.height,
        borderRadius, borderRadius);
      g.setStroke(defaultStroke);
    }
  }

  @Override
  protected void renderBackground(Graphics2D g) {
    super.renderBackground(g);

    g.setColor(bgColor);
    g.fillRoundRect(0, 0, computedBounds.width, computedBounds.height,
      borderRadius, borderRadius);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Decorated decoratedWidget) {
      return Objects.equals(bgColor, decoratedWidget.bgColor) &&
        Objects.equals(borderColor, decoratedWidget.borderColor) &&
        Objects.equals(borderWidth, decoratedWidget.borderWidth);
    }

    return false;
  }

  public Decorated withBgColor(Color bgColor) {
    this.bgColor = bgColor;
    return this;
  }

  public Decorated withBorderColor(Color borderColor) {
    this.borderColor = borderColor;
    borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND,
      BasicStroke.JOIN_ROUND);
    return this;
  }

  public Decorated withBorderWidth(int borderWidth) {
    this.borderWidth = Math.max(0, borderWidth);
    borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND,
      BasicStroke.JOIN_ROUND);
    return this;
  }

  public Decorated withBorderRadius(int borderRadius) {
    this.borderRadius = Math.max(0, borderRadius);
    return this;
  }
}
