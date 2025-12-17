package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.utils.Constraints;
import dev.gamekit.ui.enums.MainAxisAlignment;

/** A {@link Flex} which arranges its children horizontally */
@WidgetBuilder
public class Row extends Flex {
  public Row(RowConfig... config) {
    super(config);
  }

  public static Row create(RowConfig... config) {
    return new Row(config);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints childConstraints = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    double currentWidth = 0;
    double maxHeight = 0;

    for (var child : children) {
      child.layout(childConstraints);
      child.computedBounds.setPosition(currentWidth, 0);

      currentWidth += child.computedBounds.width + gapSize;
      maxHeight = Math.max(maxHeight, child.computedBounds.height);
      childConstraints = new Constraints(
        0, childConstraints.maxWidth() - child.computedBounds.width,
        0, childConstraints.maxHeight()
      );
    }

    currentWidth -= gapSize;
    intrinsicSize.set(currentWidth, maxHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    double freeSpace = Math.max(0, computedBounds.width - intrinsicSize.width);
    double spaceBetween = freeSpace / Math.max(children.size() - 1, 1);

    double newX = switch (mainAxisAlignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicSize.width / 2;
      case END -> computedBounds.width - intrinsicSize.width;
      default -> 0;
    };

    for (var child : children) {
      child.computedBounds.setX(newX);
      newX += child.computedBounds.width;
      newX += mainAxisAlignment == MainAxisAlignment.SPACE_BETWEEN ? spaceBetween : gapSize;
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

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Row rowWidget &&
      super.stateEquals(rowWidget);
  }

//  public static class RowConfig<T extends RowConfig<T>> extends FlexConfig<T> { }
}
