package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.utils.Constraints;

/** A {@link Flex} which arranges its children vertically */
@WidgetBuilder
public class Column extends Flex {
  public Column(String key, ColumnConfig config, Widget... children) {
    super(key, config, children);
  }

  public static Column create(String key, ColumnConfig.Updater updater, Widget... children) {
    return new Column(key, Widgets.configureColumn(updater), children);
  }

  public static Column create(ColumnConfig.Updater updater, Widget... children) {
    return new Column(null, Widgets.configureColumn(updater), children);
  }

  public static Column create(String key, Widget... children) {
    return Column.create(key, props -> { }, children);
  }

  public static Column create(Widget... children) {
    return Column.create(props -> { }, children);
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
        0, constraints.maxWidth(),
        0, constraints.maxHeight() - currentHeight
      );
    }

    currentHeight -= gapSize;
    intrinsicSize.set(maxWidth, currentHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    double freeSpace = Math.max(0, computedBounds.height - intrinsicSize.height);
    double spaceBetween = freeSpace / Math.max(children.length - 1, 1);

    double newY = switch (mainAxisAlignment) {
      case CENTER -> computedBounds.height / 2 - intrinsicSize.height / 2;
      case END -> computedBounds.height - intrinsicSize.height;
      default -> 0;
    };

    for (var child : children) {
      child.computedBounds.setY(newY);

      newY += child.computedBounds.height + gapSize;

      if (mainAxisAlignment == MainAxisAlignment.SPACE_BETWEEN)
        newY += spaceBetween;
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
}
