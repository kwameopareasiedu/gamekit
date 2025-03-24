package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Container;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Size;
import dev.gamekit.utils.Spacing;

import java.util.List;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Node} which pads its single child with spacing */
public class Padding extends Container {
  protected final Node child;
  protected final List<Node> children;
  protected Spacing padding;

  protected Padding(Node child, Spacing padding) {
    this.child = child;
    this.children = List.of(child);
    this.padding = padding;
  }

  public static Builder create(Node child, Spacing padding) {
    return new Builder(child, padding);
  }

  @Override
  protected List<Node> getChildren() { return children; }

  @Override
  public void onLayout(Constraints constraints) {
    Constraints c = constraints.update(
      0, constraints.maxWidth, 0, constraints.maxHeight
    );

    child.onLayout(c);

    Size childSize = child.getComputedSize();

    int intrinsicWidth = childSize.width + padding.getHorizontal();
    int intrinsicHeight = childSize.height + padding.getVertical();
    intrinsicSize.set(intrinsicWidth, intrinsicHeight);

    int computedWidth = clamp(intrinsicWidth, constraints.minWidth, constraints.maxWidth);
    int computedHeight = clamp(intrinsicHeight, constraints.minHeight, constraints.maxHeight);
    computedSize.set(computedWidth, computedHeight);

    if (intrinsicWidth > computedWidth || intrinsicHeight > computedHeight) {
      Constraints cc = new Constraints(
        0, computedWidth - padding.getHorizontal(),
        0, computedHeight - padding.getVertical()
      );

      child.onLayout(cc);
    }

    child.getComputedPosition().set(padding.left, padding.top);
  }

  public static class Builder {
    private final Padding instance;

    private Builder(Node child, Spacing padding) {
      instance = new Padding(child, padding);
    }

    public Padding get() { return instance; }
  }
}
