package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilderField;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** A parent which contains multiple child {@link Widget}s */
public abstract class MultiChildParent extends Parent {
  @WidgetBuilderField
  protected ArrayList<Widget> children;

  public MultiChildParent(MultiChildParentConfig... config) {
    super(config);
  }

  @Override
  protected void performInit() {
    for (Widget child : ((MultiChildParentConfig) config).children) {
      if (child == null)
        throw new IllegalArgumentException("MultiChildParent child cannot be null");
    }

    this.children = new ArrayList<>(List.of(((MultiChildParentConfig) config).children));

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

  public ArrayList<Widget> getChildren() {
    return children;
  }

  public final void updateChild(int index, Widget newChild) {
    if (index >= children.size())
      throw new ArrayIndexOutOfBoundsException(
        String.format(
          "Children length: %d, Index: %d",
          children.size(), index
        )
      );
    children.get(index).parent = null;
    children.set(index, newChild);
    children.get(index).parent = this;
  }

  //  public static abstract class MultiChildParentConfig extends ParentConfig { }
}
