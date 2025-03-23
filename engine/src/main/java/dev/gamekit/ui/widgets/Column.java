package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Container;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Size;

import java.util.List;

/** Column container arranges its children horizontally */
public class Column extends Container {
  protected final List<Node> children;

  public Column(List<Node> children) {
    if (children == null)
      throw new NullPointerException("Column children cannot be null");
    this.children = children;
  }

  @Override
  protected List<Node> getChildren() {
    return children;
  }

  @Override
  public void onLayout(Constraints constraints) {
    Constraints cc = new Constraints(0, constraints.maxWidth, 0, constraints.maxHeight);
    List<Node> children = getChildren();
    int currentY = 0;
    int maxWidth = 0;

    for (var child : children) {
      child.onLayout(cc);
      child.getComputedPosition().set(0, currentY);

      Size childSize = child.getComputedSize();

      currentY += childSize.height;
      maxWidth = Math.max(maxWidth, childSize.width);
      cc = cc.update(0, cc.maxWidth, 0, cc.maxHeight - childSize.height);
    }

    computedSize.set(maxWidth, currentY);
  }
}
