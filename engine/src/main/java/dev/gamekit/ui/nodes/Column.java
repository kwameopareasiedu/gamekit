package dev.gamekit.ui.nodes;

import dev.gamekit.ui.Container;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Size;

import java.util.List;

/** Column container arranges its children horizontally */
public class Column extends Container {
  protected final List<Node> children;

  protected Column(Node... children) {
    if (children == null)
      throw new NullPointerException("Column children cannot be null");
    this.children = List.of(children);
    this.children.forEach(c -> c.setParent(this));
  }

  public static Builder create(Node... children) {
    return new Builder(children);
  }

  @Override
  protected List<Node> getChildren() {
    return children;
  }

  @Override
  protected void onLayout(Constraints constraints) {
    Constraints cc = new Constraints(0, constraints.maxWidth, 0, constraints.maxHeight);
    List<Node> children = getChildren();
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

  public static class Builder {
    private final Column instance;

    private Builder(Node... children) {
      instance = new Column(children);
    }

    public Column get() { return instance; }
  }
}
