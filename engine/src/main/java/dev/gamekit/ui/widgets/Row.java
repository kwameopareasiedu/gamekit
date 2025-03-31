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
      child.layout(cc);
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

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    int spaceBetween = (computedBounds.width - intrinsicBounds.width)
      / Math.max(children.size() - 1, 1);
    int newGapSize = gapSize + spaceBetween;

    int newX = switch (mainAxisAlignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2;
      case END -> computedBounds.width - intrinsicBounds.width;
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
          computedBounds.height / 2 - child.computedBounds.height / 2
        );
        case END -> child.computedBounds.setY(
          computedBounds.height - child.computedBounds.height
        );
        case STRETCH -> {
          Constraints c = new Constraints(
            child.constraints.minWidth(),
            child.constraints.maxWidth(),
            computedBounds.height, computedBounds.height
          );
          child.layout(c);
          child.computedBounds.setY(0);
        }
      }
    }
  }
}
