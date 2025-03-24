package dev.gamekit.ui.nodes;

import dev.gamekit.ui.Container;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Size;

import java.util.List;

/** Column container arranges its children vertically */
public class Row extends Container {
  protected final List<Node> children;

  protected Row(Node... children) {
    if (children == null)
      throw new NullPointerException("Row children cannot be null");
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
    int currentX = 0;
    int maxHeight = 0;

    for (var child : children) {
      child.computeLayout(cc);
      child.getComputedPosition().set(currentX, 0);

      Size childSize = child.getComputedSize();

      currentX += childSize.width;
      maxHeight = Math.max(maxHeight, childSize.height);
      cc = cc.update(0, cc.maxWidth - childSize.width, 0, cc.maxHeight);
    }

    intrinsicSize.set(currentX, maxHeight);
    computedSize.set(currentX, maxHeight);
  }

  public static class Builder {
    private final Row instance;

    private Builder(Node... children) {
      instance = new Row(children);
    }

    public Row get() { return instance; }
  }
}
