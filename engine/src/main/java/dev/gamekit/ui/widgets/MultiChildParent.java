package dev.gamekit.ui.widgets;

import java.awt.*;

/** A parent which contains multiple child {@link Widget widgets} */
public abstract class MultiChildParent extends Parent {
  protected Widget[] children;

  public MultiChildParent(Config config, Widget... children) {
    super(config);

    if (children == null)
      throw new IllegalArgumentException("MultiChildParent children cannot be null");

    for (Widget child : children) {
      if (child == null)
        throw new IllegalArgumentException("MultiChildParent child cannot be null");
    }

    this.children = children;
  }

  @Override
  protected void performInit() {
    for (Widget child : this.children) {
      child.parent = this;
      child.init(host);
    }
  }

  @Override
  protected void performPostLayout() {
    for (Widget child : children)
      child.postLayout();
  }

  @Override
  protected final void performRender(Graphics2D g) {
    preRender(g);
    renderAppearance(g);

    for (Widget child : children)
      child.render(g);

    postRender(g);
  }

  @Override
  protected void performUnmount() {
    for (Widget child : children)
      child.unmount();
  }

  public Widget[] getChildren() {
    return children;
  }

  public final void updateChild(int index, Widget newChild) {
    if (index >= children.length)
      throw new ArrayIndexOutOfBoundsException(
        String.format(
          "Children length: %d, Index: %d",
          children.length, index
        )
      );
    children[index].parent = null;
    children[index] = newChild;
    children[index].parent = this;
  }
}
