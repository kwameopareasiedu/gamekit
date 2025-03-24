package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Size;
import dev.gamekit.ui.Spacing;

import java.util.List;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Widget} which pads its single child with spacing */
public class Padding extends Parent {
  protected final Widget child;
  protected final List<Widget> children;
  protected Spacing padding;

  protected Padding(Widget child, Spacing padding) {
    this.child = child;
    this.children = List.of(child);
    this.padding = padding;
    child.setParent(this);
  }

  public static Padding create(Widget child, Spacing padding) {
    return new Padding(child, padding);
  }

  @Override
  protected List<Widget> getChildren() { return children; }

  @Override
  protected void onLayout(Constraints constraints) {
    Constraints c = constraints.update(
      0, constraints.maxWidth, 0, constraints.maxHeight
    );

    child.computeLayout(c);

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

      child.computeLayout(cc);
    }

    child.getComputedPosition().set(padding.left, padding.top);
  }
}
