package dev.gamekit.ui.widgets;

import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link MultiChildParent} which arranges its children linearly along one axis */
public abstract class Flex extends MultiChildParent {
  protected int gapSize;
  protected MainAxisAlignment mainAxisAlignment;
  protected CrossAxisAlignment crossAxisAlignment;

  public Flex(FlexConfig<? extends FlexConfig<?>> config, Widget... children) {
    super(config, children);
    this.config = config;
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Flex flexWidget &&
      Objects.equals(gapSize, flexWidget.gapSize) &&
      Objects.equals(mainAxisAlignment, flexWidget.mainAxisAlignment) &&
      Objects.equals(crossAxisAlignment, flexWidget.crossAxisAlignment);
  }

  @Override
  protected void performInit() {
    FlexConfig<?> config = (FlexConfig<?>) super.config;

    this.gapSize = coalesce(config.gapSize, 12);
    this.mainAxisAlignment = coalesce(config.mainAxisAlignment, MainAxisAlignment.START);
    this.crossAxisAlignment = coalesce(config.crossAxisAlignment, CrossAxisAlignment.START);

    super.performInit();
  }

  @SuppressWarnings("unchecked")
  public static class FlexConfig<T extends FlexConfig<T>> extends MultiChildParentConfig {
    protected Integer gapSize;
    protected MainAxisAlignment mainAxisAlignment;
    protected CrossAxisAlignment crossAxisAlignment;

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
