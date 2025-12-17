package dev.gamekit.ui.widgets;

import dev.gamekit.utils.Constraints;
import dev.gamekit.ui.enums.MainAxisAlignment;

/** A {@link Flex} which arranges its children vertically */
public class Column extends Flex {
  public Column(ColumnConfig... config) {
    super(config);
  }

  public static Column create(ColumnConfig... config) {
    return new Column(config);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints childConstraints = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    double maxWidth = 0;
    double currentHeight = 0;

    for (var child : children) {
      child.layout(childConstraints);

      currentHeight += child.computedBounds.height + gapSize;
      maxWidth = Math.max(maxWidth, child.computedBounds.width);
      childConstraints = new Constraints(
        0, childConstraints.maxWidth(),
        0, childConstraints.maxHeight() - child.computedBounds.height
      );
    }

    currentHeight -= gapSize;
    intrinsicSize.set(maxWidth, currentHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    double freeSpace = Math.max(0, computedBounds.height - intrinsicSize.height);
    double spaceBetween = freeSpace / Math.max(children.size() - 1, 1);

    double newY = switch (mainAxisAlignment) {
      case CENTER -> computedBounds.height / 2 - intrinsicSize.height / 2;
      case END -> computedBounds.height - intrinsicSize.height;
      default -> 0;
    };

    for (var child : children) {
      child.computedBounds.setY(newY);
      newY += child.computedBounds.height;
      newY += mainAxisAlignment == MainAxisAlignment.SPACE_BETWEEN ? spaceBetween : gapSize;
    }

    for (var child : children) {
      switch (crossAxisAlignment) {
        case CENTER -> child.computedBounds.setX(
          computedBounds.width / 2 - child.computedBounds.width / 2
        );
        case END -> child.computedBounds.setX(
          computedBounds.width - child.computedBounds.width
        );
        case STRETCH -> {
          Constraints c = new Constraints(
            computedBounds.width, computedBounds.width,
            child.constraints.minHeight(),
            child.constraints.maxHeight()
          );
          child.layout(c);
          child.computedBounds.setX(0);
        }
      }
    }
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Column columnWidget &&
      super.stateEquals(columnWidget);
  }

//  public static class ColumnConfig<T extends ColumnConfig<T>> extends FlexConfig<T> { }
}
