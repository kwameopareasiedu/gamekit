package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.MainAxisAlignment;

/** A {@link Flex} which arranges its children vertically */
public class Column extends Flex {
  public Column(
    ColumnOptions<? extends ColumnOptions<?>> options,
    Widget... children
  ) {
    super(options, children);
  }

  public static Column create(
    ColumnOptions<? extends ColumnOptions<?>> options,
    Widget... children
  ) {
    return new Column(options, children);
  }

  public static Column create(Widget... children) {
    return new Column(new ColumnOptions<>(), children);
  }

  public static ColumnOptions<? extends ColumnOptions<?>> options() {
    return new ColumnOptions<>();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints cc = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    int maxWidth = 0;
    int currentHeight = 0;

    for (var child : children) {
      child.layout(cc);

      currentHeight += child.computedBounds.height + gapSize;
      maxWidth = Math.max(maxWidth, child.computedBounds.width);
      cc = new Constraints(
        0, cc.maxWidth(),
        0, cc.maxHeight() - child.computedBounds.height
      );
    }

    currentHeight -= gapSize;
    intrinsicBounds.setSize(maxWidth, currentHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    int freeSpace =
      Math.max(0, computedBounds.height - intrinsicBounds.height);
    int spaceBetween = freeSpace / Math.max(children.size() - 1, 1);

    int newY = switch (mainAxisAlignment) {
      case CENTER -> computedBounds.height / 2 - intrinsicBounds.height / 2;
      case END -> computedBounds.height - intrinsicBounds.height;
      default -> 0;
    };

    for (var child : children) {
      child.computedBounds.setY(newY);
      newY += child.computedBounds.height;
      newY += mainAxisAlignment == MainAxisAlignment.SPACE_BETWEEN ?
        spaceBetween : gapSize;
    }

    for (var child : children) {
      switch (crossAxisAlignment) {
        case CENTER -> child.computedBounds.setX(
          computedBounds.width / 2 - child.computedBounds.width / 2
        );
        case END -> child.computedBounds.setX(
          computedBounds.height - child.computedBounds.width
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
    return widget instanceof Column;
  }

  public static class ColumnOptions<T extends ColumnOptions<T>> extends FlexOptions<T> { }
}
