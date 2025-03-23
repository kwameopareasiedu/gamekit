package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Container;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Constraints;

import java.util.List;

import static dev.gamekit.utils.Math.clamp;

/** A container which centers its single child */
public class Center extends Container {
  protected final Node child;
  protected final List<Node> children;

  public Center(Node child) {
    this.child = child;
    this.children = List.of(child);
  }

  @Override
  protected List<Node> getChildren() { return children; }

  @Override
  public void onLayout(Constraints constraints) {
    Constraints c = constraints.update(
      0, constraints.maxWidth, 0, constraints.maxHeight
    );

    child.onLayout(c);

    int computedWidth = clamp(
      child.getComputedSize().width,
      constraints.minWidth, constraints.maxWidth
    );

    int computedHeight = clamp(
      child.getComputedSize().height,
      constraints.minHeight, constraints.maxHeight
    );

    intrinsicSize.set(computedWidth, computedHeight);
    computedSize.set(computedWidth, computedHeight);

    child.getComputedPosition().set(
      computedSize.width / 2 - child.getComputedSize().width / 2,
      computedSize.height / 2 - child.getComputedSize().height / 2
    );
  }
}
