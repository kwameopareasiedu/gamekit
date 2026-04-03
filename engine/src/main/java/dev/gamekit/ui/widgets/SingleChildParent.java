package dev.gamekit.ui.widgets;

import java.awt.*;

/** A parent which contains only one child {@link Widget} */
public abstract class SingleChildParent extends Parent {
  protected Widget child;

  public SingleChildParent(Config config, Widget child) {
    super(config);

    if (child == null)
      throw new IllegalArgumentException("Child of SingleChildParent cannot be null");

    this.child = child;
  }

  @Override
  protected void performInit() {
    child.parent = this;
    child.init(host);
  }

  @Override
  protected void performMount() {
    child.mount();
  }

  @Override
  protected void performPostLayout() {
    child.postLayout();
  }

  @Override
  protected final void performRender(Graphics2D g) {
    preRender(g);
    renderSelf(g);
    child.render(g);
    postRender(g);
  }

  @Override
  protected void performUnmount() {
    child.unmount();
  }

  /** Returns the {@code child} widget */
  public Widget getChild() {
    return child;
  }

  /** Replaces the existing child with the {@code newChild} widget */
  public final void updateChild(Widget newChild) {
    child.parent = null;
    child = newChild;
    child.parent = this;
  }
}
