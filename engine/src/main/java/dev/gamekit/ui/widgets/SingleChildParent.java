package dev.gamekit.ui.widgets;

import java.awt.*;

/** A parent which contains only one child {@link Widget} */
public abstract class SingleChildParent extends Parent {
  protected Widget child;

  public SingleChildParent(Widget child) {
    if (child == null)
      throw new IllegalArgumentException("Child of SingleChildParent cannot be null");
    this.child = child;
    child.setParent(this);
  }

  @Override
  protected void performMounted() {
    child.mounted(uiBridge);
  }

  @Override
  protected void performPostLayout() {
    child.postLayout();
  }

  @Override
  protected void performRender(Graphics2D g) {
    renderAppearance(g);
    child.render(g);
  }

  public Widget getChild() { return child; }

  public final void updateChild(Widget newChild) {
    child.setParent(null);
    child = newChild;
    newChild.setParent(this);
  }
}
