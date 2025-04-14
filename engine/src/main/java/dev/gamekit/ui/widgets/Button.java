package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.utils.Blend;

import java.awt.*;
import java.util.Objects;

/** A {@link Widget} which can be clicked to trigger an event */
public class Button extends SingleChildParent implements Widget.InputHandler {
  protected Color hoverTintColor;

  protected Button(Widget child) {
    super(child);
    hoverTintColor = new Color(0x22ffffff, true);
  }

  public static Button create(Widget child) {
    return new Button(child);
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
  public void performRender(Graphics2D g) {
    super.performRender(g);

    if (mouseEntered) {
      Composite composite = g.getComposite();

      g.setColor(hoverTintColor);
      g.setComposite(Blend.MULTIPLY);
      g.fillRect(0, 0, computedBounds.width, computedBounds.height);
      g.setComposite(composite);
    }
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Button buttonWidget) {
      return Objects.equals(hoverTintColor, buttonWidget.hoverTintColor);
    }

    return false;
  }

  public Button withHoverTintColor(Color hoverTintColor) {
    this.hoverTintColor = hoverTintColor;
    return this;
  }
}
