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

  /**
   * Creates a new Padding with a child and zero object
   * @param child   The child node
   */
  public Padding(Node child) {
    this(child, new Spacing(0));
  }

  /**
   * Creates a new Padding with a child and padding object
   * @param child   The child node
   * @param padding The padding around the child
   */
  public Padding(Node child, Spacing padding) {
    this.child = child;
    this.children = List.of(child);
    this.padding = padding;
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

  /**
   * Sets the padding of this container's child
   * @param padding The child's padding
   */
  public void setPadding(Spacing padding) {
    this.padding = padding;
  }
}
