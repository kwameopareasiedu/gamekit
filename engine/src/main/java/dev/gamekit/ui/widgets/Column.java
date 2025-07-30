package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.MainAxisAlignment;

/** A {@link Flex} which arranges its children vertically */
public class Column extends Flex {
  public Column(ColumnConfig<? extends ColumnConfig<?>> config, Widget... children) {
    super(config, children);
  }

  public static Column create(ColumnConfig<? extends ColumnConfig<?>> config, Widget... children) {
    return new Column(config, children);
  }

  public static Column create(Widget... children) {
    return new Column(new ColumnConfig<>(), children);
  }

  public static ColumnConfig<? extends ColumnConfig<?>> config() {
    return new ColumnConfig<>();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints cc = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    double maxWidth = 0;
    double currentHeight = 0;

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

    double freeSpace =
      Math.max(0, computedBounds.height - intrinsicBounds.height);
    double spaceBetween = freeSpace / Math.max(children.size() - 1, 1);

    double newY = switch (mainAxisAlignment) {
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
    if (widget instanceof Column columnWidget)
      return super.stateEquals(columnWidget);

    return false;
  }

  public static class ColumnConfig<T extends ColumnConfig<T>> extends FlexConfig<T> { }
}
