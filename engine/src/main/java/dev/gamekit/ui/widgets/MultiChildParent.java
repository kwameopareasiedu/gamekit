package dev.gamekit.ui.widgets;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** A parent which contains multiple child {@link Widget}s */
public abstract class MultiChildParent extends Parent {
  protected final List<Widget> children;

  public MultiChildParent(Widget... children) {
    for (Widget child : children) {
      if (child == null)
        throw new IllegalArgumentException("MultiChildParent children cannot be null");
    }

    this.children = new ArrayList<>(List.of(children));
    this.children.forEach(c -> c.setParent(this));
  }

  @Override
  protected void performPostLayout() {
    children.forEach(Widget::postLayout);
  }

  @Override
  protected void performRender(Graphics2D g) {
    renderAppearance(g);
    children.forEach(child -> child.render(g));
  }

  public List<Widget> getChildren() { return children; }

  public final void updateChild(int index, Widget newChild) {
    if (index >= children.size())
      throw new ArrayIndexOutOfBoundsException(
        String.format(
          "Children length: %d, Index: %d",
          children.size(), index
        )
      );
    children.get(index).setParent(null);
    children.set(index, newChild);
    children.get(index).setParent(this);
  }
}
