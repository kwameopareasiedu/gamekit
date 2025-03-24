package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Size;

import java.util.List;

/** Column container arranges its children vertically */
public class Column extends Parent {
  protected final List<Widget> children;

  protected Column(Widget... children) {
    if (children == null)
      throw new NullPointerException("Column children cannot be null");
    this.children = List.of(children);
    this.children.forEach(c -> c.setParent(this));
  }

  public static Column create(Widget... children) {
    return new Column(children);
  }

  @Override
  protected List<Widget> getChildren() {
    return children;
  }

  @Override
  protected void onLayout(Constraints constraints) {
    Constraints cc = new Constraints(0, constraints.maxWidth, 0, constraints.maxHeight);
    List<Widget> children = getChildren();
    int currentY = 0;
    int maxWidth = 0;

    for (var child : children) {
      child.computeLayout(cc);
      child.getComputedPosition().set(0, currentY);

      Size childSize = child.getComputedSize();

      currentY += childSize.height;
      maxWidth = Math.max(maxWidth, childSize.width);
      cc = cc.update(0, cc.maxWidth, 0, cc.maxHeight - childSize.height);
    }

    intrinsicSize.set(maxWidth, currentY);
    computedSize.set(maxWidth, currentY);
  }
}
