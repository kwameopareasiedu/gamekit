package dev.gamekit.ui.widgets;

import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

import java.util.Objects;

/** A {@link MultiChildParent} which arranges its children linearly along one axis */
public abstract class Flex extends MultiChildParent {
  protected int gapSize;
  protected MainAxisAlignment mainAxisAlignment;
  protected CrossAxisAlignment crossAxisAlignment;

  private final Config<?> config;

  public Flex(Config<? extends Config<?>> config, Widget... children) {
    super(children);
    this.config = config;
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

  @Override
  protected void performUpdateState(Widget widget) {
    this.gapSize = ((Flex) widget).gapSize;
    this.mainAxisAlignment = ((Flex) widget).mainAxisAlignment;
    this.crossAxisAlignment = ((Flex) widget).crossAxisAlignment;
  }

  @Override
  protected void performMounted() {
    this.gapSize = config.gapSize;
    this.mainAxisAlignment = config.mainAxisAlignment;
    this.crossAxisAlignment = config.crossAxisAlignment;
    super.performMounted();
  }

  @SuppressWarnings("unchecked")
  public static class Config<T extends Config<T>> {
    Integer gapSize = 12;
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
