package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

/** A {@link MultiChildParent} which arranges its children in a grid */
@WidgetBuilder
public class Grid extends MultiChildParent {
  @WidgetBuilderField(fallback = "2")
  public Integer columnCount;
  @WidgetBuilderField(fallback = "0")
  public Integer columnGapSize;
  @WidgetBuilderField(fallback = "0")
  public Integer rowGapSize;

  private int rowCount;

  public Grid(GridConfig config, Widget... children) {
    super(config, children);
  }

  public static Grid create(GridConfig.Updater updater, Widget... children) {
    return new Grid(Widgets.configureGrid(updater), children);
  }

  public static Grid create(Widget... children) {
    return Grid.create(props -> { }, children);
  }

  @Override
  protected void performInit() {
    if (columnCount <= 0)
      throw new IllegalArgumentException("Grid columnCount must be a positive number");

    rowCount = (int) Math.ceil(children.length / (double) columnCount);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    if (children.length == 0) return;

    double cellWidth = (constraints.maxWidth() - (columnCount - 1) * columnGapSize) / columnCount;
    double cellHeight = (constraints.maxHeight() - (rowCount - 1) * rowGapSize) / rowCount;

    for (int childIdx = 0; childIdx < children.length; childIdx++) {
      Widget child = children[childIdx];
      double childPosX = (childIdx % columnCount) * (cellWidth + columnGapSize);
      //noinspection IntegerDivisionInFloatingPointContext
      double childPosY = (childIdx / columnCount) * (cellHeight + rowGapSize);

      child.layout(new Constraints(cellWidth, cellWidth, cellHeight, cellHeight));
      child.computedBounds.setPosition(childPosX, childPosY);
    }

    intrinsicSize.set(
      columnCount * cellWidth + (columnCount - 1) * columnGapSize,
      rowCount * cellHeight + (rowCount - 1) * rowGapSize
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }
}
