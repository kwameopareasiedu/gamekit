package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

/** A {@link Flex} which arranges its children horizontally */
public class Row extends Flex {
  public Row(
    int gapSize,
    MainAxisAlignment mainAxisAlignment,
    CrossAxisAlignment crossAxisAlignment,
    Widget... children
  ) {
    super(gapSize, mainAxisAlignment, crossAxisAlignment, children);
  }

  @SafeVarargs
  public static Row create(Param<? super RowParam>... params) {
    return new Row(
      Param.getValue(params, "gapSize", 0),
      Param.getValue(params, "mainAxisAlignment", MainAxisAlignment.START),
      Param.getValue(params, "crossAxisAlignment", CrossAxisAlignment.START),
      Param.getValue(params, "children", null)
    );
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints cc = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    int currentWidth = 0;
    int maxHeight = 0;

    for (var child : children) {
      child.layout(cc);
      child.computedBounds.setPosition(currentWidth, 0);

      currentWidth += child.computedBounds.width + gapSize;
      maxHeight = Math.max(maxHeight, child.computedBounds.height);
      cc = new Constraints(
        0, cc.maxWidth() - child.computedBounds.width,
        0, cc.maxHeight()
      );
    }

    currentWidth -= gapSize;
    intrinsicBounds.setSize(currentWidth, maxHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    int freeSpace = Math.max(0, computedBounds.width - intrinsicBounds.width);
    int spaceBetween = freeSpace / Math.max(children.size() - 1, 1);

    int newX = switch (mainAxisAlignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2;
      case END -> computedBounds.width - intrinsicBounds.width;
      default -> 0;
    };

    for (var child : children) {
      child.computedBounds.setX(newX);
      newX += child.computedBounds.width;
      newX += mainAxisAlignment == MainAxisAlignment.SPACE_BETWEEN ?
        spaceBetween : gapSize;
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
