package dev.gamekit.ui.widgets;

import java.awt.*;
import java.util.List;

/** A parent which contains multiple child {@link Widget widgets} */
public abstract class MultiChildParent extends Parent {
  protected Widget[] children;

  private List<Widget> childrenList;

  public MultiChildParent(String key, Config config, Widget... children) {
    super(key, config);

    if (children == null)
      throw new IllegalArgumentException("MultiChildParent children cannot be null");

    for (Widget child : children) {
      if (child == null)
        throw new IllegalArgumentException("MultiChildParent child cannot be null");
    }

    this.children = children;
    this.childrenList = List.of(children);
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

  /** Returns the {@link #children} array */
  public Widget[] getChildren() {
    return children;
  }

  /** Returns the backing {@link #children} list */
  public List<Widget> getChildrenList() {
    return childrenList;
  }

  /** Replaces an existing child at the specified {@code index} with the {@code newChild} widget */
  public final void updateChild(int index, Widget newChild) {
    if (index >= children.length) {
      throw new ArrayIndexOutOfBoundsException(
        String.format(
          "Children length: %d, Index: %d",
          children.length, index
        )
      );
    }

    children[index].parent = null;
    children[index] = newChild;
    children[index].parent = this;
  }

  /**
   * Resizes the children array, keeping children up to the specified {@code newSize}.
   * <p>
   * During UI reconciliation, if a {@link MultiChildParent} at the same position in the new tree has fewer children
   * than this one, the children must be resized, removing excess children and preventing them from being rendered.
   */
  public final void resizeChildren(int newSize) {
    if (children.length == newSize)
      return;

    Widget[] newChildren = new Widget[newSize];

    System.arraycopy(children, 0, newChildren, 0, newSize);

    children = newChildren;
    childrenList = List.of(children);
  }
}
