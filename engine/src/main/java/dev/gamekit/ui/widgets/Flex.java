package dev.gamekit.ui.widgets;

import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

import java.util.Objects;

/** A {@link MultiChildParent} which arranges its children linearly along one axis */
public abstract class Flex extends MultiChildParent {
  protected int gapSize;
  protected MainAxisAlignment mainAxisAlignment;
  protected CrossAxisAlignment crossAxisAlignment;

  public Flex(Widget... children) {
    super(children);

    if (children.length == 0)
      throw new IllegalArgumentException("Flex must have at least one child");

    gapSize = 0;
    mainAxisAlignment = MainAxisAlignment.START;
    crossAxisAlignment = CrossAxisAlignment.START;
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

  public Flex withGapSize(int gapSize) {
    this.gapSize = gapSize;
    return this;
  }

  public Flex withMainAxisAlignment(MainAxisAlignment axisAlignment) {
    this.mainAxisAlignment = axisAlignment;
    return this;
  }

  public Flex withCrossAxisAlignment(CrossAxisAlignment axisAlignment) {
    this.crossAxisAlignment = axisAlignment;
    return this;
  }
}
