package dev.gamekit.ui.widgets;

import java.awt.*;

/** A parent which contains only one child {@link Widget} */
public abstract class SingleChildParent extends Parent {
  protected Widget child;

  public SingleChildParent(SingleChildParentConfig config, Widget child) {
    super(config);

    if (child == null)
      throw new IllegalArgumentException("Child of SingleChildParent cannot be null");

    this.child = child;
    child.parent = this;
  }

  @Override
  protected void performInit() {
    child.init(host);
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

  @Override
  protected void performUnmount() {
    child.unmount();
  }

  public Widget getChild() {
    return child;
  }

  public final void updateChild(Widget newChild) {
    child.parent = null;
    child = newChild;
    newChild.parent = this;
  }

  public abstract static class SingleChildParentConfig extends ParentConfig { }
}
