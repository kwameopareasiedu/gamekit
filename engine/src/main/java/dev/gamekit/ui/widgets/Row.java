package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Size;

import java.util.List;

/** Column container arranges its children horizontally */
public class Row extends Parent {
  protected final List<Widget> children;

  protected Row(Widget... children) {
    if (children == null)
      throw new NullPointerException("Row children cannot be null");
    this.children = List.of(children);
    this.children.forEach(c -> c.setParent(this));
  }

  public static Row create(Widget... children) {
    return new Row(children);
  }

  @Override
  protected List<Widget> getChildren() {
    return children;
  }

  @Override
  protected void onLayout(Constraints constraints) {
    Constraints cc = new Constraints(0, constraints.maxWidth(), 0, constraints.maxHeight());
    List<Widget> children = getChildren();
    int currentX = 0;
    int maxHeight = 0;

    for (var child : children) {
      child.computeLayout(cc);
      child.getComputedPosition().set(currentX, 0);

      Size childSize = child.getComputedSize();

      currentX += childSize.width;
      maxHeight = Math.max(maxHeight, childSize.height);
      cc = new Constraints(
        0, cc.maxWidth() - childSize.width,
        0, cc.maxHeight()
      );
    }

    intrinsicSize.set(currentX, maxHeight);
    computedSize.set(currentX, maxHeight);
  }
}
