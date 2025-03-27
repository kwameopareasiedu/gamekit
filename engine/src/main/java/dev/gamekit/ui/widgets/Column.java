package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

/** A {@link Parent} which arranges its children vertically */
public class Column extends Flex {
  protected Column(Widget... children) {
    super(children);
  }

  public static Column create(Widget... children) {
    return new Column(children);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints cc = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    int currentY = 0;
    int maxWidth = 0;
    Widget lastChild = children.get(children.size() - 1);

    for (var child : children) {
      child.computeLayout(cc);
      child.computedBounds.setPosition(0, currentY);

      currentY += child.computedBounds.height;
      maxWidth = Math.max(maxWidth, child.computedBounds.width);
      cc = new Constraints(
        0, cc.maxWidth(),
        0, cc.maxHeight() - child.computedBounds.height
      );

      if (child != lastChild) currentY += gapSize;
    }

    intrinsicBounds.setSize(maxWidth, currentY);

    int computedWidth = constraints.constrainWidth(intrinsicBounds.width);
    int computedHeight = constraints.constrainHeight(intrinsicBounds.height);
    computedBounds.setSize(computedWidth, computedHeight);

    int spaceBetween = (computedHeight - intrinsicBounds.height) / (children.size() - 1);
    int newGapSize = gapSize + spaceBetween;

    int newY = switch (mainAxisAlignment) {
      case CENTER -> computedHeight / 2 - intrinsicBounds.height / 2;
      case END -> computedHeight - intrinsicBounds.height;
      default -> 0;
    };

    for (var child : children) {
      child.computedBounds.setY(newY);
      newY += child.computedBounds.height;

      if (child != lastChild) newY += newGapSize;
    }

    for (var child : children) {
      switch (crossAxisAlignment) {
        case CENTER -> child.computedBounds.setX(
          computedWidth / 2 - child.computedBounds.width / 2
        );
        case END -> child.computedBounds.setX(
          computedHeight - child.computedBounds.width
        );
        case STRETCH -> {
          Constraints c = new Constraints(
            computedWidth, computedWidth,
            child.constraints.minHeight(),
            child.constraints.maxHeight()
          );
          child.computeLayout(c);
          child.computedBounds.setX(0);
        }
      }
    }
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Column columnWidget) {
      return super.stateEquals(columnWidget);
    }

    return false;
  }
}
