package dev.gamekit.ui.widgets;

import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

import java.util.Objects;

/** A {@link MultiChildParent} which arranges its children linearly along one axis */
public abstract class Flex extends MultiChildParent {
  protected final int gapSize;
  protected final MainAxisAlignment mainAxisAlignment;
  protected final CrossAxisAlignment crossAxisAlignment;

  public Flex(
    int gapSize,
    MainAxisAlignment mainAxisAlignment,
    CrossAxisAlignment crossAxisAlignment,
    Widget... children
  ) {
    super(children);
    this.gapSize = gapSize;
    this.mainAxisAlignment = mainAxisAlignment;
    this.crossAxisAlignment = crossAxisAlignment;
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Flex flexWidget) {
      return Objects.equals(gapSize, flexWidget.gapSize) &&
        Objects.equals(mainAxisAlignment, flexWidget.mainAxisAlignment) &&
        Objects.equals(crossAxisAlignment, flexWidget.crossAxisAlignment);
    }

    return false;
  }
}
