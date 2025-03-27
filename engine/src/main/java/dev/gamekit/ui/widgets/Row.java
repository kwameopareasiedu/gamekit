package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

/** A {@link Flex} which arranges its children horizontally */
public class Row extends Flex {
  protected Row(Widget... children) {
    super(children);
  }

  public static Row create(Widget... children) {
    return new Row(children);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints cc = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    int currentX = 0;
    int maxHeight = 0;
    Widget lastChild = children.get(children.size() - 1);


    for (var child : children) {
      child.computeLayout(cc);
      child.computedBounds.setPosition(currentX, 0);

      currentX += child.computedBounds.width;
      maxHeight = Math.max(maxHeight, child.computedBounds.height);
      cc = new Constraints(
        0, cc.maxWidth() - child.computedBounds.width,
        0, cc.maxHeight()
      );

      if (child != lastChild) currentX += gapSize;
    }

    intrinsicBounds.setSize(currentX, maxHeight);

    int computedWidth = constraints.constrainWidth(currentX);
    int computedHeight = constraints.constrainHeight(maxHeight);
    computedBounds.setSize(computedWidth, computedHeight);

    int spaceBetween = (computedWidth - intrinsicBounds.width) / (children.size() - 1);
    int newGapSize = gapSize + spaceBetween;

    int newX = switch (mainAxisAlignment) {
      case CENTER -> computedWidth / 2 - intrinsicBounds.width / 2;
      case END -> computedWidth - intrinsicBounds.width;
      default -> 0;
    };

    for (var child : children) {
      child.computedBounds.setX(newX);
      newX += child.computedBounds.width;

      if (child != lastChild) newX += newGapSize;
    }

    for (var child : children) {
      switch (crossAxisAlignment) {
        case CENTER -> child.computedBounds.setY(
          computedHeight / 2 - child.computedBounds.height / 2
        );
        case END -> child.computedBounds.setY(
          computedHeight - child.computedBounds.height
        );
        case STRETCH -> {
          Constraints c = new Constraints(
            child.constraints.minWidth(),
            child.constraints.maxWidth(),
            computedHeight, computedHeight
          );
          child.computeLayout(c);
          child.computedBounds.setY(0);
        }
      }
    }
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Row rowWidget) {
      return super.stateEquals(rowWidget);
    }

    return false;
  }
}
