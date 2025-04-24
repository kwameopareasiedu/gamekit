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
    FlexOptions<? extends FlexOptions<?>> options,
    Widget... children
  ) {
    super(children);
    this.gapSize = options.gapSize;
    this.mainAxisAlignment = options.mainAxisAlignment;
    this.crossAxisAlignment = options.crossAxisAlignment;
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Flex flexWidget) {
      return Objects.equals(gapSize, flexWidget.gapSize) &&
        Objects.equals(mainAxisAlignment, flexWidget.mainAxisAlignment) &&
        Objects.equals(crossAxisAlignment, flexWidget.crossAxisAlignment);
    }

    return false;
  }

  @SuppressWarnings("unchecked")
  public static class FlexOptions<T extends FlexOptions<T>> {
    int gapSize = 12;
    MainAxisAlignment mainAxisAlignment = MainAxisAlignment.START;
    CrossAxisAlignment crossAxisAlignment = CrossAxisAlignment.START;

    public T gapSize(int gapSize) {
      this.gapSize = gapSize;
      return (T) this;
    }

    public T mainAxisAlignment(MainAxisAlignment mainAxisAlignment) {
      this.mainAxisAlignment = mainAxisAlignment;
      return (T) this;
    }

    public T crossAxisAlignment(CrossAxisAlignment crossAxisAlignment) {
      this.crossAxisAlignment = crossAxisAlignment;
      return (T) this;
    }
  }
}
