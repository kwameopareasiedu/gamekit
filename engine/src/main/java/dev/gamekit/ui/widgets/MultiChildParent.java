package dev.gamekit.ui.widgets;

import java.awt.*;

/** A parent which contains multiple child {@link Widget widgets} */
public abstract class MultiChildParent extends Parent {
  protected Widget[] children;

  public MultiChildParent(String key, Config config, Widget... children) {
    super(key, config);

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
  protected void performMount() {
    for (Widget child : children)
      child.mount();
  }

  @Override
  protected void performPostLayout() {
    for (Widget child : children)
      child.postLayout();
  }

  @Override
  protected final void performRender(Graphics2D g) {
    preRender(g);
    renderSelf(g);

    for (Widget child : children)
      child.render(g);

    postRender(g);
  }

  @Override
  protected void performUnmount() {
    for (Widget child : children)
      child.unmount();
  }

  /** Returns the index of the given child widget */
  protected int getIndexOf(Widget child) {
    for (int i = 0; i < children.length; i++)
      if (children[i] == child)
        return i;

    return -1;
  }

  /** Replaces an existing child at the specified {@code index} with the {@code newChild} widget */
  protected final void updateChild(int index, Widget newChild) {
    if (index >= children.length) {
      throw new ArrayIndexOutOfBoundsException(
        String.format(
          "Children length: %d, Index: %d",
          children.length, index
        )
      );
    }

    if (children[index] != null)
      children[index].parent = null;

    children[index] = newChild;
    children[index].parent = this;
  }

  /**
   * Resizes the children array to match the new size.
   * <p>
   * If the new size is greater, the remaining slots are filled with empty placeholders
   */
  protected void resize(int newSize) {
    if (newSize == children.length)
      return;

    int oldSize = children.length;
    Widget[] newChildren = new Widget[newSize];
    System.arraycopy(children, 0, newChildren, 0, Math.min(children.length, newSize));
    children = newChildren;

    if (newSize > oldSize) {
      for (int i = oldSize; i < newSize; i++)
        updateChild(i, Empty.create());
    }
  }
}
