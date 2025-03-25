package dev.gamekit.ui.widgets;

import dev.gamekit.utils.Constants;

import java.awt.*;

/** A parent which contains only one child {@link Widget} */
public abstract class SingleChildParent extends Parent {
  protected Widget child;

  protected SingleChildParent(Widget child) {
    if (child == null)
      throw new IllegalArgumentException("Child of SingleChildParent cannot be null");
    this.child = child;
    child.setParent(this);
  }

  @Override
  public final void performRender(Graphics2D g) {
    g.setBackground(Constants.TRANSPARENT_COLOR);
    g.clearRect(0, 0, computedBounds.width, computedBounds.height);

    // Renders its children within self to enable clipping
    g.drawImage(
      child.getAppearance().image,
      child.computedBounds.x,
      child.computedBounds.y,
      null
    );
  }

  public Widget getChild() { return child; }

  public final void updateChild(Widget newChild) {
    child.setParent(null);
    child = newChild;
    child.setParent(this);
  }
}
