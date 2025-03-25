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
    g.setColor(Constants.TRANSPARENT_COLOR);
    g.fillRect(0, 0, computedSize.width, computedSize.height);

    // The children are drawn in the container's image instead of calling their onRender.
    // This allows for clipping if the child's bounds fall outside that of the container.
    g.drawImage(
      child.getAppearance().image,
      child.getComputedPosition().x,
      child.getComputedPosition().y,
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
