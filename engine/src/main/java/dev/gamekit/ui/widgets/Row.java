package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.MainAxisAlignment;

/** A {@link Flex} which arranges its children horizontally */
public class Row extends Flex {
  public Row(Config<? extends Config<?>> config, Widget... children) {
    super(config, children);
  }

  public static Row create(Config<? extends Config<?>> config, Widget... children) {
    return new Row(config, children);
  }

  public static Row create(Widget... children) {
    return new Row(new Config<>(), children);
  }

  public static Config<? extends Config<?>> config() {
    return new Config<>();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    Constraints cc = new Constraints(
      0, constraints.maxWidth(),
      0, constraints.maxHeight()
    );

    double currentWidth = 0;
    double maxHeight = 0;

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

    double freeSpace = Math.max(0, computedBounds.width - intrinsicBounds.width);
    double spaceBetween = freeSpace / Math.max(children.size() - 1, 1);

    double newX = switch (mainAxisAlignment) {
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

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Row rowWidget)
      return super.stateEquals(rowWidget);

    return false;
  }

  public static class Config<T extends Config<T>> extends Flex.Config<T> { }
}
