package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;

import java.awt.*;

/** A parent which contains multiple child {@link Widget widgets} */
@WidgetBuilder
public abstract class MultiChildParent extends Parent {
  @WidgetBuilderField(
    customSetterType = "dev.gamekit.ui.widgets.Widget...",
    comparable = false,
    updatable = false,
    themable = false
  )
  protected Widget[] children;

  public MultiChildParent(MultiChildParentConfig... config) {
    super(config);
  }

  @Override
  protected void performInit() {
    for (Widget child : ((MultiChildParentConfig) config).children) {
      if (child == null) throw new IllegalArgumentException("MultiChildParent child cannot be null");
    }

    this.children = ((MultiChildParentConfig) config).children;

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
  protected void performRender(Graphics2D g) {
    renderAppearance(g);

    for (Widget child : children)
      child.render(g);
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
